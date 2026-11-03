package com.Faxzoku9.Qozcud.presentation.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.core.di.AppViewModelFactory
import com.Faxzoku9.Qozcud.core.theme.ClearIce
import com.Faxzoku9.Qozcud.core.theme.IceCyan
import com.Faxzoku9.Qozcud.core.theme.Lantern
import com.Faxzoku9.Qozcud.core.theme.SheetIce
import com.Faxzoku9.Qozcud.core.theme.SheetIceEdge
import com.Faxzoku9.Qozcud.core.theme.Snow
import com.Faxzoku9.Qozcud.core.theme.TextOnIce
import com.Faxzoku9.Qozcud.core.theme.TextOnIceMuted
import com.Faxzoku9.Qozcud.core.ui.GlassCard
import com.Faxzoku9.Qozcud.core.ui.PrimaryButton
import com.Faxzoku9.Qozcud.core.ui.ScreenBackground
import com.Faxzoku9.Qozcud.core.ui.SecondaryButton
import com.Faxzoku9.Qozcud.core.ui.SectionLabel
import com.Faxzoku9.Qozcud.core.ui.SelectChip
import com.Faxzoku9.Qozcud.core.ui.StatCard
import com.Faxzoku9.Qozcud.presentation.dialog.ChallengesDialog
import com.Faxzoku9.Qozcud.presentation.dialog.JournalDialog

@Composable
fun MenuScreen(onPlay: (Int, Int, Int) -> Unit) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    MenuContent(
        state = state,
        onSelectLake = menuViewModel::selectLake,
        onSelectLure = menuViewModel::selectLure,
        onOpenJournal = menuViewModel::openJournal,
        onCloseJournal = menuViewModel::closeJournal,
        onOpenChallenges = menuViewModel::openChallenges,
        onCloseChallenges = menuViewModel::closeChallenges,
        onPlay = { onPlay(state.selectedLakeId, state.selectedLureId, state.tripIndex) }
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onSelectLake: (Int) -> Unit,
    onSelectLure: (Int) -> Unit,
    onOpenJournal: () -> Unit,
    onCloseJournal: () -> Unit,
    onOpenChallenges: () -> Unit,
    onCloseChallenges: () -> Unit,
    onPlay: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }
    val reveal by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(480)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenBackground(
            alignment = Alignment.TopCenter,
            overlay = Brush.verticalGradient(
                listOf(
                    Color.Transparent,
                    SheetIce.copy(alpha = 0.35f),
                    SheetIce.copy(alpha = 0.92f)
                )
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ArtZone(
                lifetimePoints = state.lifetimePoints,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = SheetIce,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = reveal
                        translationY = (1f - reveal) * 120f
                    }
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        ambientColor = TextOnIce,
                        spotColor = TextOnIce
                    )
            ) {
                MenuSheet(
                    state = state,
                    onSelectLake = onSelectLake,
                    onSelectLure = onSelectLure,
                    onOpenJournal = onOpenJournal,
                    onOpenChallenges = onOpenChallenges,
                    onPlay = onPlay
                )
            }
        }
    }

    if (state.journalVisible) {
        JournalDialog(rows = state.journalRows, onClose = onCloseJournal)
    }
    if (state.challengesVisible) {
        ChallengesDialog(rows = state.challenges, onClose = onCloseChallenges)
    }
}

@Composable
private fun ArtZone(
    lifetimePoints: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            drawLine(
                color = Snow.copy(alpha = 0.5f),
                start = Offset(width * 0.04f, height * 0.70f),
                end = Offset(width * 0.46f, height * 0.84f),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = Snow.copy(alpha = 0.4f),
                start = Offset(width * 0.42f, height * 0.78f),
                end = Offset(width * 0.95f, height * 0.66f),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = Snow.copy(alpha = 0.32f),
                start = Offset(width * 0.20f, height * 0.92f),
                end = Offset(width * 0.72f, height * 0.88f),
                strokeWidth = 1.5.dp.toPx()
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 22.dp, bottom = 18.dp)
                .size(96.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Lantern.copy(alpha = 0.26f), Color.Transparent)
                    ),
                    radius = size.minDimension * 0.5f
                )
            }
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = stringResource(R.string.cd_lantern),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(64.dp)
            )
        }

        if (lifetimePoints > 0) {
            GlassCard(
                corner = 18.dp,
                innerPadding = 10.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp, top = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = stringResource(R.string.cd_home),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.points_value, lifetimePoints),
                        style = MaterialTheme.typography.titleMedium,
                        color = Snow
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuSheet(
    state: MenuUiState,
    onSelectLake: (Int) -> Unit,
    onSelectLure: (Int) -> Unit,
    onOpenJournal: () -> Unit,
    onOpenChallenges: () -> Unit,
    onPlay: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(
            modifier = Modifier
                .width(42.dp)
                .height(4.dp)
                .background(color = SheetIceEdge, shape = RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.cd_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.menu_title),
                style = MaterialTheme.typography.headlineSmall,
                color = TextOnIce
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.menu_tagline),
            style = MaterialTheme.typography.labelMedium,
            color = TextOnIceMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))
        SectionLabel(
            text = stringResource(R.string.menu_lake_label),
            color = TextOnIceMuted,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            state.lakes.forEach { lake ->
                SelectChip(
                    text = stringResource(lake.nameRes),
                    selected = lake.id == state.selectedLakeId,
                    enabled = lake.unlocked,
                    accent = ClearIce,
                    idleBorder = SheetIceEdge,
                    selectedLabel = TextOnIce,
                    idleLabel = TextOnIceMuted,
                    state = chipState(lake.unlocked, lake.id == state.selectedLakeId),
                    onClick = { onSelectLake(lake.id) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        SectionLabel(
            text = stringResource(R.string.menu_lure_label),
            color = TextOnIceMuted,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            state.lures.forEach { lure ->
                SelectChip(
                    text = stringResource(lure.nameRes),
                    selected = lure.id == state.selectedLureId,
                    enabled = lure.unlocked,
                    accent = Lantern,
                    idleBorder = SheetIceEdge,
                    selectedLabel = TextOnIce,
                    idleLabel = TextOnIceMuted,
                    state = chipState(lure.unlocked, lure.id == state.selectedLureId),
                    onClick = { onSelectLure(lure.id) },
                    chipHeight = 36.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (state.selectedLureTradeOffRes != 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(state.selectedLureTradeOffRes),
                style = MaterialTheme.typography.labelSmall,
                color = TextOnIceMuted,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (state.weatherRes != 0 && state.lightRes != 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    label = stringResource(R.string.stat_weather),
                    value = stringResource(state.weatherRes),
                    accent = IceCyan,
                    container = Color.Transparent,
                    labelColor = TextOnIceMuted,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.stat_light),
                    value = stringResource(state.lightRes),
                    accent = Lantern,
                    container = Color.Transparent,
                    labelColor = TextOnIceMuted,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.menu_hint),
            style = MaterialTheme.typography.labelSmall,
            color = TextOnIceMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        PrimaryButton(
            text = stringResource(R.string.cta_play),
            onClick = onPlay,
            leadingPainter = painterResource(R.drawable.ic_play),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SecondaryButton(
                text = stringResource(R.string.menu_journal),
                onClick = onOpenJournal,
                container = ClearIce.copy(alpha = 0.10f),
                border = SheetIceEdge,
                contentColor = TextOnIce,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = stringResource(R.string.menu_challenges),
                onClick = onOpenChallenges,
                container = Lantern.copy(alpha = 0.12f),
                border = SheetIceEdge,
                contentColor = TextOnIce,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
    }
}

@Composable
private fun chipState(unlocked: Boolean, selected: Boolean): String = stringResource(
    when {
        !unlocked -> R.string.state_locked
        selected -> R.string.state_selected
        else -> R.string.state_idle
    }
)
