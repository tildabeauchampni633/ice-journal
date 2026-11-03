package com.Faxzoku9.Qozcud.presentation.menu

import androidx.lifecycle.ViewModel
import com.Faxzoku9.Qozcud.data.sample.SampleData
import com.Faxzoku9.Qozcud.domain.repository.JournalRepository
import com.Faxzoku9.Qozcud.domain.repository.ProgressRepository
import com.Faxzoku9.Qozcud.domain.usecase.EvaluateChallengesUseCase
import com.Faxzoku9.Qozcud.domain.usecase.ResolveConditionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val journalRepository: JournalRepository,
    private val resolveConditions: ResolveConditionsUseCase,
    private val evaluateChallenges: EvaluateChallengesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val points = progressRepository.lifetimePoints()
        val tripIndex = progressRepository.tripIndex()
        val lakeId = resolveUnlockedLake(progressRepository.selectedLakeId(), points)
        val lureId = resolveUnlockedLure(progressRepository.selectedLureId(), points)
        val conditions = resolveConditions(lakeId, tripIndex, 0)
        val lastTrip = progressRepository.lastTrip()
        val dayOfYear = progressRepository.dayOfYear()
        val challenges = evaluateChallenges(SampleData.challengeTemplates, dayOfYear, lastTrip)

        _state.update { current ->
            current.copy(
                lakes = SampleData.lakes.map { lake ->
                    LakeOption(
                        id = lake.id,
                        nameRes = lake.nameRes,
                        unlocked = points >= lake.unlockPoints,
                        unlockPoints = lake.unlockPoints
                    )
                },
                lures = SampleData.lures.map { lure ->
                    LureOption(
                        id = lure.id,
                        nameRes = lure.nameRes,
                        unlocked = points >= lure.unlockPoints,
                        unlockPoints = lure.unlockPoints
                    )
                },
                selectedLakeId = lakeId,
                selectedLureId = lureId,
                selectedLureTradeOffRes = SampleData.lureById(lureId).tradeOffRes,
                lifetimePoints = points,
                bestTripPoints = progressRepository.bestTripPoints(),
                tripIndex = tripIndex,
                weatherRes = SampleData.weatherLabel(conditions.weather),
                lightRes = SampleData.lightLabel(conditions.light),
                journalRows = buildJournalRows(),
                challenges = challenges.map { challenge ->
                    ChallengeRow(
                        id = challenge.id,
                        titleRes = challenge.titleRes,
                        descriptionRes = challenge.descriptionRes,
                        rewardPoints = challenge.rewardPoints,
                        completed = progressRepository.isChallengeDone(challenge.id, dayOfYear)
                    )
                }
            )
        }
    }

    fun selectLake(lakeId: Int) {
        val option = _state.value.lakes.firstOrNull { it.id == lakeId } ?: return
        if (!option.unlocked) {
            return
        }
        progressRepository.selectLake(lakeId)
        val conditions = resolveConditions(lakeId, _state.value.tripIndex, 0)
        _state.update {
            it.copy(
                selectedLakeId = lakeId,
                weatherRes = SampleData.weatherLabel(conditions.weather),
                lightRes = SampleData.lightLabel(conditions.light)
            )
        }
    }

    fun selectLure(lureId: Int) {
        val option = _state.value.lures.firstOrNull { it.id == lureId } ?: return
        if (!option.unlocked) {
            return
        }
        progressRepository.selectLure(lureId)
        _state.update {
            it.copy(
                selectedLureId = lureId,
                selectedLureTradeOffRes = SampleData.lureById(lureId).tradeOffRes
            )
        }
    }

    fun openJournal() {
        _state.update { it.copy(journalRows = buildJournalRows(), journalVisible = true) }
    }

    fun closeJournal() {
        _state.update { it.copy(journalVisible = false) }
    }

    fun openChallenges() {
        _state.update { it.copy(challengesVisible = true) }
    }

    fun closeChallenges() {
        _state.update { it.copy(challengesVisible = false) }
    }

    private fun buildJournalRows(): List<JournalRow> = SampleData.species.map { species ->
        val entry = journalRepository.entryFor(species.id)
        JournalRow(
            speciesId = species.id,
            nameRes = species.nameRes,
            noteRes = species.noteRes,
            drawableRes = species.drawableRes,
            discovered = entry.discovered,
            bestWeightKg = entry.bestWeightKg,
            lakeNameRes = SampleData.lakeById(entry.lakeId).nameRes
        )
    }

    private fun resolveUnlockedLake(preferred: Int, points: Int): Int {
        val lake = SampleData.lakes.firstOrNull { it.id == preferred }
        return if (lake != null && points >= lake.unlockPoints) preferred else SampleData.lakes.first().id
    }

    private fun resolveUnlockedLure(preferred: Int, points: Int): Int {
        val lure = SampleData.lures.firstOrNull { it.id == preferred }
        return if (lure != null && points >= lure.unlockPoints) preferred else SampleData.lures.first().id
    }
}
