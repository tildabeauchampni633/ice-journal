package com.Faxzoku9.Qozcud.presentation.gameover

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.Faxzoku9.Qozcud.core.theme.IceCyan
import com.Faxzoku9.Qozcud.core.theme.IceWhite
import com.Faxzoku9.Qozcud.core.theme.Lantern
import com.Faxzoku9.Qozcud.core.theme.NightIce
import com.Faxzoku9.Qozcud.core.theme.SafeBand
import com.Faxzoku9.Qozcud.core.theme.TextMuted
import com.Faxzoku9.Qozcud.core.ui.PrimaryButton
import com.Faxzoku9.Qozcud.core.ui.ScreenBackground
import com.Faxzoku9.Qozcud.core.ui.SecondaryButton
import com.Faxzoku9.Qozcud.core.ui.StatCard

@Composable
fun GameOverScreen(
    tripPoints: Int,
    landed: Int,
    casts: Int,
    onCastAgain: (Int, Int, Int) -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(
        key = "report_" + tripPoints + "_" + landed + "_" + casts,
        factory = AppViewModelFactory(
            AppViewModelArgs(tripPoints = tripPoints, landed = landed, casts = casts)
        )
    )
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()

    GameOverContent(
        state = state,
        onCastAgain = { onCastAgain(state.lakeId, state.lureId, state.nextTripIndex) },
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onCastAgain: () -> Unit,
    onMenu: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }
    val pointsShown by animateIntAsState(
        targetValue = if (appeared) state.tripPoints else 0,
        animationSpec = tween(700)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenBackground(
            alignment = Alignment.Center,
            overlay = Brush.verticalGradient(
                listOf(
                    DeepLake.copy(alpha = 0.82f),
                    DeepLakeDark.copy(alpha = 0.90f),
                    NightIce.copy(alpha = 0.95f)
                )
            )
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Lantern.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.34f),
                    radius = size.width * 0.8f
                ),
                radius = size.width * 0.8f,
                center = Offset(size.width * 0.5f, size.height * 0.34f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 22.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(
                    if (state.titleRes != 0) state.titleRes else R.string.result_title
                ),
                style = MaterialTheme.typography.headlineMedium,
                color = IceWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.result_caught, state.landed, state.casts),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            HeroCatchCard(state = state)

            Spacer(modifier = Modifier.height(16.dp))
            StatRow(state = state, pointsShown = pointsShown)

            JournalDelta(state = state)

            ChallengeResults(state = state)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.result_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.result_again),
                onClick = onCastAgain,
                leadingPainter = painterResource(R.drawable.ic_refresh),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            SecondaryButton(
                text = stringResource(R.string.result_menu),
                onClick = onMenu,
                leadingPainter = painterResource(R.drawable.icon_home),
                leadingDescription = stringResource(R.string.cd_home),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}

@Composable
private fun StatRow(state: GameOverUiState, pointsShown: Int) {
    val showPoints = state.tripPoints > 0
    val showBest = state.bestTripPoints > 0
    val showSpecies = state.discovered > 0
    val visible = listOf(showPoints, showBest, showSpecies).count { it }
    if (visible < 2) {
        return
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (showPoints) {
            StatCard(
                label = stringResource(R.string.stat_points),
                value = pointsShown.toString(),
                accent = Lantern,
                modifier = Modifier.weight(1f)
            )
        }
        if (showBest) {
            StatCard(
                label = stringResource(R.string.stat_best),
                value = state.bestTripPoints.toString(),
                accent = ClearIce,
                modifier = Modifier.weight(1f)
            )
        }
        if (showSpecies) {
            StatCard(
                label = stringResource(R.string.stat_species),
                value = stringResource(
                    R.string.species_count,
                    state.discovered,
                    state.totalSpecies
                ),
                accent = IceCyan,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun JournalDelta(state: GameOverUiState) {
    if (state.newSpeciesNameRes == 0 && state.newBestPoints == 0) {
        return
    }
    Spacer(modifier = Modifier.height(12.dp))
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (state.newSpeciesNameRes != 0) {
            Text(
                text = stringResource(
                    R.string.result_new_species,
                    stringResource(state.newSpeciesNameRes)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = Lantern,
                textAlign = TextAlign.Center
            )
        }
        if (state.newBestPoints > 0) {
            Text(
                text = stringResource(R.string.result_new_best, state.newBestPoints),
                style = MaterialTheme.typography.bodyMedium,
                color = Lantern,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ChallengeResults(state: GameOverUiState) {
    if (state.challenges.isEmpty()) {
        return
    }
    Spacer(modifier = Modifier.height(12.dp))
    Column(modifier = Modifier.fillMaxWidth()) {
        state.challenges.take(3).forEach { challenge ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (challenge.completed) R.drawable.ic_check else R.drawable.ic_cross
                    ),
                    contentDescription = null,
                    tint = if (challenge.completed) SafeBand else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(challenge.titleRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (challenge.completed) IceWhite else TextMuted
                )
            }
        }
    }
}
