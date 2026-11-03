package com.Faxzoku9.Qozcud.presentation.app

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Faxzoku9.Qozcud.core.di.AppViewModelFactory
import com.Faxzoku9.Qozcud.core.navigation.Screen
import com.Faxzoku9.Qozcud.core.theme.DeepLake
import com.Faxzoku9.Qozcud.presentation.game.GameScreen
import com.Faxzoku9.Qozcud.presentation.gameover.GameOverScreen
import com.Faxzoku9.Qozcud.presentation.menu.MenuScreen
import com.Faxzoku9.Qozcud.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    BackHandler(enabled = true) {
        if (!appViewModel.back()) {
            activity?.finish()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepLake)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                val entering = targetState
                val leaving = initialState
                when {
                    entering is Screen.Game && leaving is Screen.Menu ->
                        (slideInHorizontally(animationSpec = tween(380)) { it / 4 } +
                            fadeIn(tween(380))) togetherWith fadeOut(tween(260))

                    entering is Screen.GameOver ->
                        (fadeIn(tween(460)) +
                            scaleIn(animationSpec = tween(460), initialScale = 0.96f)) togetherWith
                            fadeOut(tween(260))

                    else -> fadeIn(tween(420)) togetherWith fadeOut(tween(320))
                }
            },
            label = "app_screen"
        ) { current ->
            when (current) {
                is Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.finishSplash() }
                )

                is Screen.Menu -> MenuScreen(
                    onPlay = { lakeId, lureId, tripIndex ->
                        appViewModel.navigate(Screen.Game(lakeId, lureId, tripIndex))
                    }
                )

                is Screen.Game -> GameScreen(
                    lakeId = current.lakeId,
                    lureId = current.lureId,
                    tripIndex = current.tripIndex,
                    onReport = { points, landed, casts ->
                        appViewModel.replaceTop(Screen.GameOver(points, landed, casts))
                    }
                )

                is Screen.GameOver -> GameOverScreen(
                    tripPoints = current.tripPoints,
                    landed = current.landed,
                    casts = current.casts,
                    onCastAgain = { lakeId, lureId, tripIndex ->
                        appViewModel.replaceTop(Screen.Game(lakeId, lureId, tripIndex))
                    },
                    onMenu = { appViewModel.toMenu() }
                )
            }
        }
    }
}
