package com.Faxzoku9.Qozcud.presentation.game

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.core.theme.ClearIce
import com.Faxzoku9.Qozcud.core.theme.DeepLake
import com.Faxzoku9.Qozcud.core.theme.DeepLakeDark
import com.Faxzoku9.Qozcud.core.theme.FrostSurfaceHi
import com.Faxzoku9.Qozcud.core.theme.IceCyan
import com.Faxzoku9.Qozcud.core.theme.IceWhite
import com.Faxzoku9.Qozcud.core.theme.Lantern
import com.Faxzoku9.Qozcud.core.theme.NightIce
import com.Faxzoku9.Qozcud.core.theme.Snow
import com.Faxzoku9.Qozcud.core.theme.TextMuted

@Composable
fun IceColumn(
    state: GameUiState,
    onTapDepth: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(26.dp)
    val description = stringResource(
        R.string.state_column,
        state.lureDepthM,
        stringResource(if (state.echoLabelRes != 0) state.echoLabelRes else R.string.echo_none)
    )
    val jigShift by animateDpAsState(
        targetValue = if (state.jigOffset) 6.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f)
    )
    val flashAlpha by animateFloatAsState(
        targetValue = if (state.biteFlash) 0.3f else 0f,
        animationSpec = tween(160)
    )
    val lift by animateFloatAsState(
        targetValue = state.landedLift,
        animationSpec = tween(420)
    )

    BoxWithConstraints(
        modifier = modifier
            .clip(shape)
            .border(width = 1.5.dp, color = FrostSurfaceHi, shape = shape)
            .semantics { contentDescription = description }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val top = size.height * CRUST_FRACTION
                    val span = (size.height - top).coerceAtLeast(1f)
                    onTapDepth(((offset.y - top) / span).coerceIn(0f, 1f))
                }
            }
    ) {
        val columnHeight: Dp = maxHeight
        val waterTop = columnHeight * CRUST_FRACTION
        val waterSpan = columnHeight - waterTop
        val maxDepth = state.maxDepthM.coerceAtLeast(1f)
        val depthToDp: (Float) -> Dp = { depth ->
            waterTop + waterSpan * (depth.coerceIn(0f, maxDepth) / maxDepth)
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val crustPx = height * CRUST_FRACTION

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ClearIce.copy(alpha = 0.72f),
                        DeepLake,
                        DeepLakeDark,
                        NightIce
                    ),
                    startY = crustPx,
                    endY = height
                ),
                topLeft = Offset(0f, crustPx),
                size = Size(width, height - crustPx)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(IceCyan.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(width * 0.22f, crustPx + (height - crustPx) * 0.35f),
                    radius = width * 0.7f
                ),
                radius = width * 0.7f,
                center = Offset(width * 0.22f, crustPx + (height - crustPx) * 0.35f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Lantern.copy(alpha = 0.05f), Color.Transparent),
                    center = Offset(width * 0.82f, crustPx + (height - crustPx) * 0.72f),
                    radius = width * 0.6f
                ),
                radius = width * 0.6f,
                center = Offset(width * 0.82f, crustPx + (height - crustPx) * 0.72f)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Snow, IceWhite, ClearIce.copy(alpha = 0.55f)),
                    startY = 0f,
                    endY = crustPx
                ),
                topLeft = Offset.Zero,
                size = Size(width, crustPx)
            )
            listOf(0.26f, 0.52f, 0.78f).forEachIndexed { index, fraction ->
                drawLine(
                    color = Snow.copy(alpha = 0.6f),
                    start = Offset(width * fraction, crustPx * (0.15f + index * 0.1f)),
                    end = Offset(width * (fraction + 0.12f), crustPx * 0.92f),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val holeCenter = Offset(width * 0.56f, crustPx * 0.62f)
            val holeSize = Size(width * 0.26f, crustPx * 0.62f)
            drawOval(
                color = NightIce,
                topLeft = Offset(
                    holeCenter.x - holeSize.width / 2f,
                    holeCenter.y - holeSize.height / 2f
                ),
                size = holeSize
            )
            drawOval(
                color = IceCyan.copy(alpha = 0.5f),
                topLeft = Offset(
                    holeCenter.x - holeSize.width / 2f,
                    holeCenter.y - holeSize.height / 2f
                ),
                size = holeSize,
                style = Stroke(width = 3.dp.toPx())
            )

            if (state.echoRevealed) {
                val bandTop = crustPx + (height - crustPx) * (state.bandMinM / maxDepth)
                val bandBottom = crustPx + (height - crustPx) * (state.bandMaxM / maxDepth)
                drawRect(
                    color = IceCyan.copy(alpha = 0.18f),
                    topLeft = Offset(0f, bandTop),
                    size = Size(width, (bandBottom - bandTop).coerceAtLeast(2f))
                )
                drawLine(
                    color = IceCyan,
                    start = Offset(0f, bandTop),
                    end = Offset(width, bandTop),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = IceCyan,
                    start = Offset(0f, bandBottom),
                    end = Offset(width, bandBottom),
                    strokeWidth = 1.dp.toPx()
                )

                if (state.echoSweep < 1f) {
                    repeat(2) { index ->
                        val phase = (state.echoSweep + index * 0.3f).coerceIn(0f, 1f)
                        val y = crustPx + (height - crustPx) * phase
                        drawLine(
                            color = IceCyan.copy(alpha = 0.5f * (1f - phase)),
                            start = Offset(width * 0.08f, y),
                            end = Offset(width * 0.92f, y),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            }

            val tickCount = maxDepth.toInt()
            for (meter in 2..tickCount step 2) {
                val y = crustPx + (height - crustPx) * (meter / maxDepth)
                drawLine(
                    color = TextMuted.copy(alpha = 0.5f),
                    start = Offset(width - 18.dp.toPx(), y),
                    end = Offset(width - 6.dp.toPx(), y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val lureY = crustPx + (height - crustPx) * (state.lureDepthM / maxDepth)
            drawLine(
                color = IceCyan.copy(alpha = 0.7f * state.lineAlpha),
                start = Offset(holeCenter.x, crustPx),
                end = Offset(holeCenter.x, lureY),
                strokeWidth = 2.dp.toPx()
            )
        }

        Image(
            painter = painterResource(R.drawable.sprite_4),
            contentDescription = stringResource(R.string.cd_lantern),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 14.dp, top = 6.dp)
                .size(34.dp)
        )

        if (state.echoRevealed) {
            Text(
                text = stringResource(
                    if (state.echoStrong) R.string.echo_strong else R.string.echo_faint
                ),
                style = MaterialTheme.typography.labelSmall,
                color = IceCyan,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = depthToDp(state.bandMinM) + 2.dp)
                    .padding(start = 10.dp)
            )
        }

        for (meter in 4..state.maxDepthM.toInt() step 4) {
            Text(
                text = stringResource(R.string.depth_value, meter.toFloat()),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = depthToDp(meter.toFloat()) - 7.dp)
                    .padding(end = 22.dp)
            )
        }

        if (state.fishVisible && state.fishDrawableRes != 0) {
            val fishDepth = state.fishDepthM * (1f - lift) + 0.2f * lift
            Image(
                painter = painterResource(state.fishDrawableRes),
                contentDescription = stringResource(R.string.cd_fish),
                contentScale = ContentScale.Fit,
                alpha = if (state.fishIdentified) 1f else 0.55f,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = depthToDp(fishDepth) - (state.fishSpriteDp / 2).dp)
                    .padding(start = 26.dp)
                    .size(state.fishSpriteDp.dp)
            )
        }

        Image(
            painter = painterResource(R.drawable.sprite_3),
            contentDescription = stringResource(R.string.cd_lure),
            contentScale = ContentScale.Fit,
            alpha = state.lineAlpha,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = 14.dp, y = depthToDp(state.lureDepthM) - 15.dp + jigShift)
                .size(30.dp)
        )

        if (flashAlpha > 0f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = Snow.copy(alpha = flashAlpha))
            }
        }
    }
}

private const val CRUST_FRACTION = 0.10f
