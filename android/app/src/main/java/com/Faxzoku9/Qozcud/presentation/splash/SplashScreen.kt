package com.Faxzoku9.Qozcud.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.Faxzoku9.Qozcud.core.theme.DeepLake
import com.Faxzoku9.Qozcud.core.theme.DeepLakeDark
import com.Faxzoku9.Qozcud.core.theme.FrostSurface
import com.Faxzoku9.Qozcud.core.theme.IceCyan
import com.Faxzoku9.Qozcud.core.theme.IceWhite
import com.Faxzoku9.Qozcud.core.theme.Lantern
import com.Faxzoku9.Qozcud.core.theme.NightIce
import com.Faxzoku9.Qozcud.core.theme.TextMuted
import com.Faxzoku9.Qozcud.core.ui.ScreenBackground
import kotlin.random.Random

private data class Speck(val x: Float, val y: Float, val radius: Float, val alpha: Float)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory())
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    var appeared by remember { mutableStateOf(false) }

    LaunchedEffect(finished) {
        if (finished) {
            onFinished()
        }
    }

    LaunchedEffect(Unit) {
        appeared = true
    }

    val specks = remember {
        val random = Random(1904)
        List(120) {
            Speck(
                x = random.nextFloat(),
                y = random.nextFloat(),
                radius = 0.8f + random.nextFloat() * 1.4f,
                alpha = 0.10f + random.nextFloat() * 0.25f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "splash_loop")
    val ringPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "ring_phase"
    )
    val lureDrop by transition.animateFloat(
        initialValue = -40f,
        targetValue = 34f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lure_drop"
    )
    val glow by transition.animateFloat(
        initialValue = 0.78f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lantern_glow"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.86f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 220f)
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(620)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenBackground(
            alignment = Alignment.Center,
            overlay = Brush.verticalGradient(
                listOf(
                    NightIce.copy(alpha = 0.97f),
                    DeepLakeDark.copy(alpha = 0.93f),
                    DeepLake.copy(alpha = 0.90f)
                )
            )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(IceCyan.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(width * 0.5f, height * 0.18f),
                    radius = width * 0.9f
                ),
                startAngle = 196f,
                sweepAngle = 148f,
                useCenter = false,
                topLeft = Offset(-width * 0.15f, height * 0.02f),
                size = Size(width * 1.3f, height * 0.38f),
                style = Stroke(width = 110.dp.toPx())
            )
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(ClearIce.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(width * 0.35f, height * 0.22f),
                    radius = width * 0.8f
                ),
                startAngle = 204f,
                sweepAngle = 132f,
                useCenter = false,
                topLeft = Offset(-width * 0.05f, height * 0.06f),
                size = Size(width * 1.1f, height * 0.30f),
                style = Stroke(width = 78.dp.toPx())
            )
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(Lantern.copy(alpha = 0.07f), Color.Transparent),
                    center = Offset(width * 0.66f, height * 0.16f),
                    radius = width * 0.7f
                ),
                startAngle = 210f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(width * 0.08f, height * 0.04f),
                size = Size(width * 1.0f, height * 0.26f),
                style = Stroke(width = 60.dp.toPx())
            )
            specks.forEach { speck ->
                drawCircle(
                    color = IceWhite.copy(alpha = speck.alpha),
                    radius = speck.radius.dp.toPx(),
                    center = Offset(speck.x * width, speck.y * height)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Lantern.copy(alpha = 0.22f * glow),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension * 0.5f
                    )
                    repeat(3) { index ->
                        val phase = (ringPhase + index / 3f) % 1f
                        val radius = (60f + (190f - 60f) * phase).dp.toPx()
                        drawCircle(
                            color = IceCyan.copy(alpha = 0.45f * (1f - phase)),
                            radius = radius,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(168.dp)
                        .graphicsLayer {
                            scaleX = logoScale
                            scaleY = logoScale
                            alpha = logoAlpha
                        }
                )
            }

            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .height(96.dp)
                ) {
                    drawLine(
                        color = ClearIce.copy(alpha = 0.55f),
                        start = Offset(size.width / 2f, 0f),
                        end = Offset(size.width / 2f, size.height),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = stringResource(R.string.cd_lure),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .offset(y = (lureDrop + 40f).dp)
                        .size(44.dp)
                )
            }

            AnimatedVisibility(
                visible = appeared,
                enter = slideInVertically(tween(520, delayMillis = 180)) { it / 6 } +
                    fadeIn(tween(520, delayMillis = 180))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.splash_title),
                        style = MaterialTheme.typography.displayMedium,
                        color = IceWhite,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.splash_subtitle),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            LinearProgressIndicator(
                color = Lantern,
                trackColor = FrostSurface,
                modifier = Modifier
                    .width(190.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.splash_progress),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .width(120.dp)
                    .background(Color.Transparent)
            )
        }
    }
}
