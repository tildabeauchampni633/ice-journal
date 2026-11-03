package com.Faxzoku9.Qozcud.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Faxzoku9.Qozcud.core.theme.FrostSurface
import com.Faxzoku9.Qozcud.core.theme.FrostSurfaceHi

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    container: Color = FrostSurface.copy(alpha = 0.55f),
    border: Color = FrostSurfaceHi,
    corner: Dp = 26.dp,
    innerPadding: Dp = 16.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(corner)
    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = modifier
            .background(color = container, shape = shape)
            .border(width = 1.dp, color = border, shape = shape)
            .padding(innerPadding),
        content = content
    )
}
