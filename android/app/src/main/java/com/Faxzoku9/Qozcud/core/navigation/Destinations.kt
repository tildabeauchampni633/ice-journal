package com.Faxzoku9.Qozcud.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Game(val lakeId: Int, val lureId: Int, val tripIndex: Int) : Screen
    data class GameOver(val tripPoints: Int, val landed: Int, val casts: Int) : Screen
}
