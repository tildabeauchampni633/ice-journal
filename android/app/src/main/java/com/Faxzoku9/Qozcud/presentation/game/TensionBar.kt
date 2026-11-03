package com.Faxzoku9.Qozcud.presentation.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.core.theme.Danger
import com.Faxzoku9.Qozcud.core.theme.FrostSurface
import com.Faxzoku9.Qozcud.core.theme.IceWhite
import com.Faxzoku9.Qozcud.core.theme.Lantern
import com.Faxzoku9.Qozcud.core.theme.SafeBand

@Composable
fun TensionBar(
    tension: Int,
    safeMin: Int,
    safeMax: Int,
    tensionMax: Int,
    modifier: Modifier = Modifier
) {
    val description = stringResource(R.string.state_tension, tension, tensionMax)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(18.dp)
            .clip(RoundedCornerShape(9.dp))
            .semantics {
                stateDescription = description
                liveRegion = LiveRegionMode.Polite
            }
    ) {
        val width = size.width
        val height = size.height
        drawRect(color = FrostSurface, size = Size(width, height))
        val safeStart = width * (safeMin.toFloat() / tensionMax)
        val safeEnd = width * (safeMax.toFloat() / tensionMax)
        drawRect(
            color = SafeBand.copy(alpha = 0.45f),
            topLeft = Offset(safeStart, 0f),
            size = Size((safeEnd - safeStart).coerceAtLeast(1f), height)
        )
        val filled = width * (tension.toFloat() / tensionMax).coerceIn(0f, 1f)
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(SafeBand, Lantern, Danger),
                startX = 0f,
                endX = width
            ),
            topLeft = Offset.Zero,
            size = Size(filled, height)
        )
        drawRect(
            color = IceWhite,
            topLeft = Offset((filled - 2.dp.toPx()).coerceAtLeast(0f), 0f),
            size = Size(4.dp.toPx(), height)
        )
    }
}
