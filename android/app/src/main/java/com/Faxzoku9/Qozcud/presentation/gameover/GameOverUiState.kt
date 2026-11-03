package com.Faxzoku9.Qozcud.presentation.gameover

import com.Faxzoku9.Qozcud.presentation.menu.ChallengeRow

data class GameOverUiState(
    val titleRes: Int = 0,
    val landed: Int = 0,
    val casts: Int = 0,
    val tripPoints: Int = 0,
    val bestTripPoints: Int = 0,
    val discovered: Int = 0,
    val totalSpecies: Int = 0,
    val hasHero: Boolean = false,
    val heroDrawableRes: Int = 0,
    val heroNameRes: Int = 0,
    val heroWeightKg: Float = 0f,
    val heroDepthM: Float = 0f,
    val heroWeatherRes: Int = 0,
    val emptyNoteRes: Int = 0,
    val newSpeciesNameRes: Int = 0,
    val newBestPoints: Int = 0,
    val challenges: List<ChallengeRow> = emptyList(),
    val lakeId: Int = 0,
    val lureId: Int = 0,
    val nextTripIndex: Int = 0
)
