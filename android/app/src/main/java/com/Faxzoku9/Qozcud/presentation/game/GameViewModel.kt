package com.Faxzoku9.Qozcud.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.core.config.GameConfig
import com.Faxzoku9.Qozcud.data.sample.SampleData
import com.Faxzoku9.Qozcud.domain.model.CastOutcome
import com.Faxzoku9.Qozcud.domain.model.Catch
import com.Faxzoku9.Qozcud.domain.model.Conditions
import com.Faxzoku9.Qozcud.domain.model.Species
import com.Faxzoku9.Qozcud.domain.model.TripResult
import com.Faxzoku9.Qozcud.domain.repository.JournalRepository
import com.Faxzoku9.Qozcud.domain.repository.ProgressRepository
import com.Faxzoku9.Qozcud.domain.usecase.PickSpeciesUseCase
import com.Faxzoku9.Qozcud.domain.usecase.ResolveConditionsUseCase
import com.Faxzoku9.Qozcud.domain.usecase.ScoreCatchUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

data class ReportRequest(val tripPoints: Int, val landed: Int, val casts: Int)

class GameViewModel(
    private val lakeId: Int,
    private val lureId: Int,
    private val tripIndex: Int,
    private val journalRepository: JournalRepository,
    private val progressRepository: ProgressRepository,
    private val resolveConditions: ResolveConditionsUseCase,
    private val pickSpecies: PickSpeciesUseCase,
    private val scoreCatch: ScoreCatchUseCase
) : ViewModel() {

    private val lake = SampleData.lakeById(lakeId)
    private val lure = SampleData.lureById(lureId)

    private val _state = MutableStateFlow(
        GameUiState(
            lakeNameRes = lake.nameRes,
            castsPerTrip = GameConfig.CASTS_PER_TRIP,
            maxDepthM = lake.maxDepthM,
            safeMin = lure.safeMin,
            safeMax = lure.safeMax,
            tensionMax = GameConfig.TENSION_MAX,
            lureDepthM = START_DEPTH_M,
            echoLabelRes = R.string.echo_none,
            actionLabelRes = R.string.game_drop,
            hintRes = R.string.hint_ready
        )
    )
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _reports = Channel<ReportRequest>(Channel.BUFFERED)
    val reports = _reports.receiveAsFlow()

    private var tickJob: Job? = null
    private var idleJob: Job? = null
    private var engagedJob: Job? = null
    private var bannerJob: Job? = null

    private var castIndex = 0
    private var tripPoints = 0
    private var landedCount = 0
    private var deepestLanded = 0f
    private var bestCatch: Catch? = null
    private var snapped = false
    private var newSpeciesId = -1
    private var escapedSpeciesId = -1
    private var finished = false

    private var species: Species = SampleData.species.first()
    private var conditions: Conditions = resolveConditions(lakeId, tripIndex, 0)
    private var catchWeight = 0f
    private var bandCenter = 0f
    private var driftMs = 0f
    private var phaseMs = 0L
    private var echoMs = 0L
    private var biteMs = 0L
    private var snapMs = 0L
    private var slackMs = 0L
    private var sincePublishMs = 0L
    private var attractionValue = 0f
    private var tensionValue = 0f
    private var reelProgressValue = 0
    private var fishDepthValue = 0f

    init {
        prepareCast()
        idleJob = viewModelScope.launch {
            delay(GameConfig.IDLE_BACKSTOP_MS)
            endTrip(endedEarly = true)
        }
    }

    fun onPrimaryAction() {
        if (finished) {
            return
        }
        registerInput()
        when (_state.value.phase) {
            CastPhase.READY -> dropLine()
            CastPhase.JIGGING -> jig()
            CastPhase.BITE -> setHook()
            CastPhase.REELING -> reel()
            CastPhase.RESOLVED -> advance()
        }
    }

    fun onEchoPulse() {
        if (finished || _state.value.phase == CastPhase.RESOLVED) {
            return
        }
        registerInput()
        echoMs = 0L
        _state.update {
            it.copy(
                echoRevealed = true,
                echoSweep = 0f,
                echoLabelRes = echoLabel(),
                echoStrong = isEchoStrong()
            )
        }
        if (_state.value.phase == CastPhase.READY) {
            dropLine()
        }
    }

    fun onColumnTap(fraction: Float) {
        if (finished) {
            return
        }
        val phase = _state.value.phase
        if (phase != CastPhase.JIGGING && phase != CastPhase.READY) {
            return
        }
        registerInput()
        val depth = (fraction * lake.maxDepthM).coerceIn(0f, lake.maxDepthM)
        _state.update { it.copy(lureDepthM = depth) }
    }

    fun onPackUp() {
        if (finished) {
            return
        }
        endTrip(endedEarly = true)
    }

    fun showPause() {
        stopTick()
        _state.update { it.copy(pauseVisible = true) }
    }

    fun endFromPause() {
        _state.update { it.copy(pauseVisible = false) }
        endTrip(endedEarly = true)
    }

    fun hidePause() {
        _state.update { it.copy(pauseVisible = false) }
        resumeIfActive()
    }

    fun pause() {
        stopTick()
        idleJob?.cancel()
        engagedJob?.cancel()
    }

    fun resumeIfActive() {
        if (finished) {
            return
        }
        val phase = _state.value.phase
        if (phase == CastPhase.JIGGING || phase == CastPhase.BITE || phase == CastPhase.REELING) {
            startTick()
            registerInput()
        }
    }

    private fun prepareCast() {
        conditions = resolveConditions(lakeId, tripIndex, castIndex)
        species = pickSpecies(
            lake = lake,
            catalog = SampleData.species,
            conditions = conditions,
            seed = lakeId * 131 + tripIndex * 17 + castIndex
        )
        val random = Random(lakeId * 977 + tripIndex * 53 + castIndex * 7 + 11)
        val low = species.minDepthM.coerceAtMost(lake.maxDepthM - 2f)
        val high = species.maxDepthM.coerceAtMost(lake.maxDepthM - 0.8f)
        val span = (high - low).coerceAtLeast(0.6f)
        bandCenter = low + random.nextFloat() * span
        catchWeight = (
            species.minWeightKg +
                random.nextFloat() * (species.maxWeightKg - species.minWeightKg)
            ) * conditions.weightBonus
        driftMs = 0f
        phaseMs = 0L
        echoMs = 0L
        biteMs = 0L
        snapMs = 0L
        slackMs = 0L
        sincePublishMs = 0L
        attractionValue = 0f
        tensionValue = 0f
        reelProgressValue = 0
        fishDepthValue = bandCenter
        _state.update {
            it.copy(
                castNumber = castIndex + 1,
                phase = CastPhase.READY,
                lureDepthM = START_DEPTH_M,
                jigOffset = false,
                fishDepthM = bandCenter,
                fishDrawableRes = species.drawableRes,
                fishSpriteDp = species.spriteDp,
                fishIdentified = false,
                fishVisible = false,
                bandMinM = (bandCenter - 1.4f).coerceAtLeast(0.4f),
                bandMaxM = (bandCenter + 1.4f).coerceAtMost(lake.maxDepthM),
                echoRevealed = false,
                echoStrong = false,
                echoLabelRes = R.string.echo_none,
                echoSweep = 0f,
                attraction = 0,
                tension = 0,
                reelProgress = 0,
                biteFlash = false,
                landedLift = 0f,
                lineAlpha = 1f,
                bannerVisible = false,
                bannerLabelRes = 0,
                actionLabelRes = R.string.game_drop,
                hintRes = R.string.hint_ready
            )
        }
    }

    private fun dropLine() {
        echoMs = 0L
        phaseMs = 0L
        val start = ((lake.primeMinM + lake.primeMaxM) / 2f).coerceIn(0.5f, lake.maxDepthM)
        _state.update {
            it.copy(
                phase = CastPhase.JIGGING,
                lureDepthM = if (it.lureDepthM <= START_DEPTH_M + 0.05f) start else it.lureDepthM,
                echoRevealed = true,
                echoSweep = 0f,
                echoStrong = isEchoStrong(),
                echoLabelRes = echoLabel(),
                fishVisible = true,
                actionLabelRes = R.string.game_jig,
                hintRes = R.string.hint_jig
            )
        }
        startTick()
    }

    private fun jig() {
        val matched = abs(_state.value.lureDepthM - fishDepthValue) <=
            GameConfig.DEPTH_MATCH_TOLERANCE_M
        val gain = if (matched) {
            GameConfig.JIG_ATTRACTION_PER_TAP
        } else {
            GameConfig.JIG_ATTRACTION_PER_TAP / 3
        }
        attractionValue = (attractionValue + gain)
            .coerceAtMost(GameConfig.BITE_THRESHOLD.toFloat())
        sincePublishMs = 0L
        _state.update {
            it.copy(
                attraction = attractionValue.toInt(),
                fishDepthM = fishDepthValue,
                jigOffset = !it.jigOffset
            )
        }
        if (attractionValue >= GameConfig.BITE_THRESHOLD && matched) {
            enterBite()
        }
    }

    private fun enterBite() {
        biteMs = 0L
        _state.update {
            it.copy(
                phase = CastPhase.BITE,
                biteFlash = true,
                fishIdentified = true,
                actionLabelRes = R.string.game_hook,
                hintRes = R.string.hint_bite
            )
        }
        startTick()
    }

    private fun setHook() {
        phaseMs = 0L
        snapMs = 0L
        slackMs = 0L
        sincePublishMs = 0L
        tensionValue = ((lure.safeMin + lure.safeMax) / 2).toFloat()
        reelProgressValue = 0
        _state.update {
            it.copy(
                phase = CastPhase.REELING,
                biteFlash = false,
                tension = tensionValue.toInt(),
                reelProgress = 0,
                actionLabelRes = R.string.game_reel,
                hintRes = R.string.hint_reel
            )
        }
        startTick()
    }

    private fun reel() {
        reelProgressValue = (reelProgressValue + GameConfig.REEL_PROGRESS_PER_TAP)
            .coerceAtMost(GameConfig.REEL_TARGET_PROGRESS)
        tensionValue = (tensionValue + GameConfig.REEL_TENSION_PER_TAP)
            .coerceAtMost(GameConfig.TENSION_MAX.toFloat())
        sincePublishMs = 0L
        _state.update {
            it.copy(reelProgress = reelProgressValue, tension = tensionValue.toInt())
        }
        if (reelProgressValue >= GameConfig.REEL_TARGET_PROGRESS) {
            resolveCast(CastOutcome.LANDED)
        }
    }

    private fun advance() {
        if (castIndex >= GameConfig.CASTS_PER_TRIP - 1) {
            endTrip(endedEarly = false)
        } else {
            castIndex += 1
            prepareCast()
        }
    }

    private fun startTick() {
        if (tickJob?.isActive == true) {
            return
        }
        tickJob = viewModelScope.launch {
            var last = System.currentTimeMillis()
            while (isActive) {
                delay(GameConfig.TICK_MS)
                val now = System.currentTimeMillis()
                val elapsed = (now - last).coerceIn(0L, 200L)
                last = now
                onTick(elapsed)
            }
        }
    }

    private fun stopTick() {
        tickJob?.cancel()
        tickJob = null
    }

    private fun onTick(elapsed: Long) {
        when (_state.value.phase) {
            CastPhase.JIGGING -> tickJigging(elapsed)
            CastPhase.BITE -> tickBite(elapsed)
            CastPhase.REELING -> tickReeling(elapsed)
            else -> stopTick()
        }
    }

    private fun tickJigging(elapsed: Long) {
        phaseMs += elapsed
        echoMs = (echoMs + elapsed).coerceAtMost(GameConfig.ECHO_SWEEP_MS)
        driftMs += elapsed
        val decay = GameConfig.JIG_ATTRACTION_DECAY_PER_SEC * elapsed / 1000f
        attractionValue = (attractionValue - decay).coerceAtLeast(0f)
        val drift = sin(driftMs / 3200f * 2f * Math.PI).toFloat() * 1.1f
        fishDepthValue = (bandCenter + drift).coerceIn(0.4f, lake.maxDepthM)
        if (duePublish(elapsed)) {
            _state.update {
                it.copy(
                    attraction = attractionValue.toInt(),
                    fishDepthM = fishDepthValue,
                    echoSweep = echoMs.toFloat() / GameConfig.ECHO_SWEEP_MS
                )
            }
        }
        if (phaseMs >= GameConfig.JIG_PHASE_MAX_MS) {
            resolveCast(CastOutcome.FISH_LEFT)
        }
    }

    private fun duePublish(elapsed: Long): Boolean {
        sincePublishMs += elapsed
        if (sincePublishMs < PUBLISH_INTERVAL_MS) {
            return false
        }
        sincePublishMs = 0L
        return true
    }

    private fun tickBite(elapsed: Long) {
        biteMs += elapsed
        if (biteMs >= 160L && _state.value.biteFlash) {
            _state.update { it.copy(biteFlash = false) }
        }
        if (biteMs >= lure.biteWindowMs + conditions.biteWindowBonusMs) {
            resolveCast(CastOutcome.MISSED_HOOK)
        }
    }

    private fun tickReeling(elapsed: Long) {
        phaseMs += elapsed
        val decay = GameConfig.REEL_TENSION_DECAY_PER_SEC * elapsed / 1000f
        tensionValue = (tensionValue - decay).coerceAtLeast(0f)
        val tension = tensionValue.toInt()
        if (duePublish(elapsed)) {
            _state.update { it.copy(tension = tension) }
        }
        if (tension > lure.safeMax) {
            snapMs += elapsed
            slackMs = 0L
        } else if (tension < lure.safeMin) {
            slackMs += elapsed
            snapMs = 0L
        } else {
            snapMs = 0L
            slackMs = 0L
        }
        if (snapMs >= GameConfig.SNAP_GRACE_MS || phaseMs >= GameConfig.REEL_PHASE_MAX_MS) {
            resolveCast(CastOutcome.LINE_SNAPPED)
        } else if (slackMs >= GameConfig.SLACK_GRACE_MS) {
            resolveCast(CastOutcome.FISH_LEFT)
        }
    }

    private fun resolveCast(outcome: CastOutcome) {
        stopTick()
        engagedJob?.cancel()
        bannerJob?.cancel()

        if (outcome == CastOutcome.LANDED) {
            val points = scoreCatch(species, catchWeight, lure)
            tripPoints += points
            landedCount += 1
            if (bandCenter > deepestLanded) {
                deepestLanded = bandCenter
            }
            if (journalRepository.recordCatch(species.id, catchWeight, lakeId)) {
                newSpeciesId = species.id
            }
            val landedCatch = Catch(
                speciesId = species.id,
                weightKg = catchWeight,
                depthM = bandCenter,
                points = points,
                lakeId = lakeId,
                conditions = conditions
            )
            val previous = bestCatch
            if (previous == null || points > previous.points) {
                bestCatch = landedCatch
            }
        } else {
            escapedSpeciesId = species.id
            if (outcome == CastOutcome.LINE_SNAPPED) {
                snapped = true
            }
        }

        val lastCast = castIndex >= GameConfig.CASTS_PER_TRIP - 1
        _state.update {
            it.copy(
                phase = CastPhase.RESOLVED,
                attraction = 0,
                biteFlash = false,
                fishIdentified = outcome == CastOutcome.LANDED,
                fishVisible = outcome == CastOutcome.LANDED,
                landedLift = if (outcome == CastOutcome.LANDED) 1f else 0f,
                lineAlpha = if (outcome == CastOutcome.LINE_SNAPPED) 0f else 1f,
                tripPoints = tripPoints,
                bannerVisible = true,
                bannerLabelRes = bannerLabel(outcome),
                bannerPositive = outcome == CastOutcome.LANDED,
                actionLabelRes = if (lastCast) R.string.game_report else R.string.game_next_cast,
                hintRes = R.string.hint_resolved
            )
        }

        bannerJob = viewModelScope.launch {
            delay(GameConfig.BANNER_HOLD_MS)
            if (outcome == CastOutcome.LINE_SNAPPED || lastCast) {
                endTrip(endedEarly = outcome == CastOutcome.LINE_SNAPPED && !lastCast)
            } else {
                registerInput()
            }
        }
    }

    private fun endTrip(endedEarly: Boolean) {
        if (finished) {
            return
        }
        finished = true
        stopTick()

        val castsPlayed = (castIndex + 1).coerceAtMost(GameConfig.CASTS_PER_TRIP)
        val newBest = tripPoints > progressRepository.bestTripPoints()
        val result = TripResult(
            lakeId = lakeId,
            lureId = lureId,
            casts = castsPlayed,
            landed = landedCount,
            points = tripPoints,
            bestCatch = bestCatch,
            deepestLandedM = deepestLanded,
            escapedSpeciesId = escapedSpeciesId,
            endedEarly = endedEarly,
            snapped = snapped,
            newSpeciesId = newSpeciesId,
            newBestPoints = newBest
        )
        progressRepository.saveTrip(result)
        _reports.trySend(ReportRequest(tripPoints, landedCount, castsPlayed))

        idleJob?.cancel()
        idleJob = null
        val pendingEngaged = engagedJob
        engagedJob = null
        val pendingBanner = bannerJob
        bannerJob = null
        pendingEngaged?.cancel()
        pendingBanner?.cancel()
    }

    private fun registerInput() {
        idleJob?.cancel()
        engagedJob?.cancel()
        if (finished) {
            return
        }
        engagedJob = viewModelScope.launch {
            delay(GameConfig.ENGAGED_BACKSTOP_MS)
            if (_state.value.phase != CastPhase.RESOLVED) {
                stopTick()
            }
            endTrip(endedEarly = true)
        }
    }

    private fun isEchoStrong(): Boolean = when {
        conditions.alwaysStrongEcho -> true
        conditions.weakEcho -> false
        else -> bandCenter >= lake.primeMinM && bandCenter <= lake.primeMaxM
    }

    private fun echoLabel(): Int =
        if (isEchoStrong()) R.string.echo_strong else R.string.echo_faint

    private fun bannerLabel(outcome: CastOutcome): Int = when (outcome) {
        CastOutcome.LANDED -> R.string.banner_landed
        CastOutcome.LINE_SNAPPED -> R.string.banner_snapped
        CastOutcome.MISSED_HOOK -> R.string.banner_missed
        CastOutcome.FISH_LEFT -> R.string.banner_left
    }

    private companion object {
        const val START_DEPTH_M = 1.5f
        const val PUBLISH_INTERVAL_MS = 32L
    }

    override fun onCleared() {
        super.onCleared()
        stopTick()
        idleJob?.cancel()
        engagedJob?.cancel()
        bannerJob?.cancel()
    }
}
