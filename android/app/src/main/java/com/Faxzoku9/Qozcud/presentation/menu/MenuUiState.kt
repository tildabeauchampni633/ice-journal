package com.Faxzoku9.Qozcud.presentation.menu

data class LakeOption(
    val id: Int,
    val nameRes: Int,
    val unlocked: Boolean,
    val unlockPoints: Int
)

data class LureOption(
    val id: Int,
    val nameRes: Int,
    val unlocked: Boolean,
    val unlockPoints: Int
)

data class JournalRow(
    val speciesId: Int,
    val nameRes: Int,
    val noteRes: Int,
    val drawableRes: Int,
    val discovered: Boolean,
    val bestWeightKg: Float,
    val lakeNameRes: Int
)

data class ChallengeRow(
    val id: Int,
    val titleRes: Int,
    val descriptionRes: Int,
    val rewardPoints: Int,
    val completed: Boolean
)

data class MenuUiState(
    val lakes: List<LakeOption> = emptyList(),
    val lures: List<LureOption> = emptyList(),
    val selectedLakeId: Int = 0,
    val selectedLureId: Int = 0,
    val selectedLureTradeOffRes: Int = 0,
    val lifetimePoints: Int = 0,
    val bestTripPoints: Int = 0,
    val tripIndex: Int = 0,
    val weatherRes: Int = 0,
    val lightRes: Int = 0,
    val journalRows: List<JournalRow> = emptyList(),
    val challenges: List<ChallengeRow> = emptyList(),
    val journalVisible: Boolean = false,
    val challengesVisible: Boolean = false
)
