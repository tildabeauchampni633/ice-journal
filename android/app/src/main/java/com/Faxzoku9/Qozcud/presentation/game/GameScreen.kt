package com.Faxzoku9.Qozcud.presentation.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.core.di.AppViewModelArgs
import com.Faxzoku9.Qozcud.core.di.AppViewModelFactory
import com.Faxzoku9.Qozcud.core.theme.ClearIce
import com.Faxzoku9.Qozcud.core.theme.DeepLake
import com.Faxzoku9.Qozcud.core.theme.DeepLakeDark
import com.Faxzoku9.Qozcud.core.theme.FrostSurfaceHi
import com.Faxzoku9.Qozcud.core.theme.IceCyan
import com.Faxzoku9.Qozcud.core.theme.IceWhite
import com.Faxzoku9.Qozcud.core.theme.Lantern
import com.Faxzoku9.Qozcud.core.theme.LanternDeep
import com.Faxzoku9.Qozcud.core.theme.NightIce
import com.Faxzoku9.Qozcud.core.theme.SafeBand
import com.Faxzoku9.Qozcud.core.theme.Snow
import com.Faxzoku9.Qozcud.core.theme.TextMuted
import com.Faxzoku9.Qozcud.core.ui.PrimaryButton
import com.Faxzoku9.Qozcud.core.ui.ScreenBackground
import com.Faxzoku9.Qozcud.core.ui.SecondaryButton
import com.Faxzoku9.Qozcud.core.ui.StatCard
import com.Faxzoku9.Qozcud.presentation.dialog.PauseDialog

@Composable
fun GameScreen(
    lakeId: Int,
    lureId: Int,
    tripIndex: Int,
    onReport: (Int, Int, Int) -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(
        key = "game_" + lakeId + "_" + lureId + "_" + tripIndex,
        factory = AppViewModelFactory(
            AppViewModelArgs(lakeId = lakeId, lureId = lureId, tripIndex = tripIndex)
        )
    )
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        gameViewModel.reports.collect { request ->
            onReport(request.tripPoints, request.landed, request.casts)
        }
    }

    LaunchedEffect(state.phase) {
        gameViewModel.resumeIfActive()
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }

    GameContent(
        state = state,
        onPrimaryAction = gameViewModel::onPrimaryAction,
        onEchoPulse = gameViewModel::onEchoPulse,
        onPackUp = gameViewModel::onPackUp,
        onColumnTap = gameViewModel::onColumnTap,
        onShowPause = gameViewModel::showPause,
        onHidePause = gameViewModel::hidePause,
        onEndFromPause = gameViewModel::endFromPause
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onPrimaryAction: () -> Unit,
    onEchoPulse: () -> Unit,
    onPackUp: () -> Unit,
    onColumnTap: (Float) -> Unit,
    onShowPause: () -> Unit,
    onHidePause: () -> Unit,
    onEndFromPause: () -> Unit
) {
    val pointsScale by animateFloatAsState(
        targetValue = if (state.bannerVisible && state.bannerPositive) 1.12f else 1f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 320f)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenBackground(
            alignment = Alignment.BottomCenter,
            overlay = Brush.verticalGradient(
                listOf(
                    DeepLakeDark.copy(alpha = 0.88f),
                    DeepLake.copy(alpha = 0.94f),
                    NightIce.copy(alpha = 0.97f)
                )
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            GameHeader(
                state = state,
                pointsScale = pointsScale,
                onShowPause = onShowPause
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                IceColumn(
                    state = state,
                    onTapDepth = onColumnTap,
                    modifier = Modifier.fillMaxSize()
                )
                CastBanner(
                    visible = state.bannerVisible,
                    labelRes = state.bannerLabelRes,
                    positive = state.bannerPositive,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 22.dp)
                )
            }

            GameControls(
                state = state,
                onPrimaryAction = onPrimaryAction,
                onEchoPulse = onEchoPulse,
                onPackUp = onPackUp
            )
        }
    }

    if (state.pauseVisible) {
        PauseDialog(onResume = onHidePause, onEndTrip = onEndFromPause)
    }
}

@Composable
private fun GameHeader(
    state: GameUiState,
    pointsScale: Float,
    onShowPause: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepLakeDark.copy(alpha = 0.55f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .height(56.dp)
    ) {
        IconButton(onClick = onShowPause, modifier = Modifier.size(48.dp)) {
            Image(
                painter = painterResource(R.drawable.icon_pause),
                contentDescription = stringResource(R.string.cd_pause),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = stringResource(
                    if (state.lakeNameRes != 0) state.lakeNameRes else R.string.lake_still_hollow
                ),
                style = MaterialTheme.typography.titleMedium,
                color = IceWhite,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = stringResource(
                    R.string.game_cast_of,
                    state.castNumber,
                    state.castsPerTrip
                ),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        val pointsDescription = stringResource(R.string.state_points, state.tripPoints)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = pointsScale
                    scaleY = pointsScale
                }
                .padding(end = 6.dp)
                .semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = pointsDescription,
                tint = Lantern,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = state.tripPoints.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
                color = IceWhite
            )
        }
    }
}

@Composable
private fun GameControls(
    state: GameUiState,
    onPrimaryAction: () -> Unit,
    onEchoPulse: () -> Unit,
    onPackUp: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                label = stringResource(R.string.stat_depth),
                value = stringResource(R.string.depth_value, state.lureDepthM),
                accent = ClearIce,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.stat_echo),
                value = stringResource(
                    if (state.echoLabelRes != 0) state.echoLabelRes else R.string.echo_none
                ),
                accent = IceCyan,
                modifier = Modifier.weight(1f)
            )
            if (state.attraction > 0) {
                StatCard(
                    label = stringResource(R.string.stat_attract),
                    value = stringResource(R.string.percent_value, state.attraction),
                    accent = Lantern,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (state.phase == CastPhase.REELING) {
            Spacer(modifier = Modifier.height(12.dp))
            TensionBar(
                tension = state.tension,
                safeMin = state.safeMin,
                safeMax = state.safeMax,
                tensionMax = state.tensionMax
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(
                if (state.hintRes != 0) state.hintRes else R.string.hint_ready
            ),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        PrimaryButton(
            text = stringResource(
                if (state.actionLabelRes != 0) state.actionLabelRes else R.string.game_drop
            ),
            onClick = onPrimaryAction,
            gradient = when (state.phase) {
                CastPhase.REELING -> listOf(SafeBand, ClearIce)
                CastPhase.BITE -> listOf(Snow, IceCyan)
                else -> listOf(Lantern, LanternDeep)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SecondaryButton(
                text = stringResource(R.string.game_echo),
                onClick = onEchoPulse,
                border = FrostSurfaceHi,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = stringResource(R.string.game_pack_up),
                onClick = onPackUp,
                border = FrostSurfaceHi,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
