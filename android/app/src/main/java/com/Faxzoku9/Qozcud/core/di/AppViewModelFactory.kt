package com.Faxzoku9.Qozcud.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.Faxzoku9.Qozcud.presentation.app.AppViewModel
import com.Faxzoku9.Qozcud.presentation.game.GameViewModel
import com.Faxzoku9.Qozcud.presentation.gameover.GameOverViewModel
import com.Faxzoku9.Qozcud.presentation.menu.MenuViewModel
import com.Faxzoku9.Qozcud.presentation.splash.SplashViewModel

data class AppViewModelArgs(
    val lakeId: Int = 0,
    val lureId: Int = 0,
    val tripIndex: Int = 0,
    val tripPoints: Int = 0,
    val landed: Int = 0,
    val casts: Int = 0
)

class AppViewModelFactory(
    private val args: AppViewModelArgs = AppViewModelArgs()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                progressRepository = ServiceLocator.progressRepository,
                journalRepository = ServiceLocator.journalRepository,
                resolveConditions = ServiceLocator.resolveConditions,
                evaluateChallenges = ServiceLocator.evaluateChallenges
            )

            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                lakeId = args.lakeId,
                lureId = args.lureId,
                tripIndex = args.tripIndex,
                journalRepository = ServiceLocator.journalRepository,
                progressRepository = ServiceLocator.progressRepository,
                resolveConditions = ServiceLocator.resolveConditions,
                pickSpecies = ServiceLocator.pickSpecies,
                scoreCatch = ServiceLocator.scoreCatch
            )

            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                tripPoints = args.tripPoints,
                landed = args.landed,
                casts = args.casts,
                progressRepository = ServiceLocator.progressRepository,
                journalRepository = ServiceLocator.journalRepository,
                evaluateChallenges = ServiceLocator.evaluateChallenges
            )

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
