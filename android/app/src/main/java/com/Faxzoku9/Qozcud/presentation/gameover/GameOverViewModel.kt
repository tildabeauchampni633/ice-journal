package com.Faxzoku9.Qozcud.presentation.gameover

import androidx.lifecycle.ViewModel
import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.data.sample.SampleData
import com.Faxzoku9.Qozcud.domain.repository.JournalRepository
import com.Faxzoku9.Qozcud.domain.repository.ProgressRepository
import com.Faxzoku9.Qozcud.domain.usecase.EvaluateChallengesUseCase
import com.Faxzoku9.Qozcud.presentation.menu.ChallengeRow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    private val tripPoints: Int,
    private val landed: Int,
    private val casts: Int,
    private val progressRepository: ProgressRepository,
    private val journalRepository: JournalRepository,
    private val evaluateChallenges: EvaluateChallengesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    init {
        build()
    }

    private fun build() {
        val trip = progressRepository.lastTrip()
        val dayOfYear = progressRepository.dayOfYear()
        val challenges = evaluateChallenges(SampleData.challengeTemplates, dayOfYear, trip)
        challenges.filter { it.completed }.forEach { challenge ->
            progressRepository.markChallengeDone(challenge.id, dayOfYear)
        }

        val hero = trip?.bestCatch
        val heroSpecies = hero?.let { SampleData.speciesById(it.speciesId) }
        val escaped = trip?.escapedSpeciesId ?: -1
        val emptyNoteRes = if (escaped >= 0) {
            SampleData.speciesById(escaped).noteRes
        } else {
            R.string.note_perch
        }

        _state.value = GameOverUiState(
            titleRes = if (trip?.endedEarly == true) {
                R.string.result_title_early
            } else {
                R.string.result_title
            },
            landed = trip?.landed ?: landed,
            casts = trip?.casts ?: casts,
            tripPoints = trip?.points ?: tripPoints,
            bestTripPoints = progressRepository.bestTripPoints(),
            discovered = journalRepository.discoveredCount(),
            totalSpecies = SampleData.species.size,
            hasHero = hero != null && heroSpecies != null,
            heroDrawableRes = heroSpecies?.drawableRes ?: 0,
            heroNameRes = heroSpecies?.nameRes ?: 0,
            heroWeightKg = hero?.weightKg ?: 0f,
            heroDepthM = hero?.depthM ?: 0f,
            heroWeatherRes = hero?.let { SampleData.weatherLabel(it.conditions.weather) } ?: 0,
            emptyNoteRes = emptyNoteRes,
            newSpeciesNameRes = if (trip != null && trip.newSpeciesId >= 0) {
                SampleData.speciesById(trip.newSpeciesId).nameRes
            } else {
                0
            },
            newBestPoints = if (trip?.newBestPoints == true) trip.points else 0,
            challenges = challenges.map { challenge ->
                ChallengeRow(
                    id = challenge.id,
                    titleRes = challenge.titleRes,
                    descriptionRes = challenge.descriptionRes,
                    rewardPoints = challenge.rewardPoints,
                    completed = challenge.completed
                )
            },
            lakeId = trip?.lakeId ?: progressRepository.selectedLakeId(),
            lureId = trip?.lureId ?: progressRepository.selectedLureId(),
            nextTripIndex = progressRepository.tripIndex()
        )
    }
}
