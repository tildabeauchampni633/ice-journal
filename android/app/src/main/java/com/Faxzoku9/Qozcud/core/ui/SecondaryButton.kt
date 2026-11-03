package com.Faxzoku9.Qozcud.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.Faxzoku9.Qozcud.core.theme.FrostSurface
import com.Faxzoku9.Qozcud.core.theme.FrostSurfaceHi
import com.Faxzoku9.Qozcud.core.theme.IceWhite

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    container: Color = FrostSurface.copy(alpha = 0.5f),
    border: Color = FrostSurfaceHi,
    contentColor: Color = IceWhite,
    leadingPainter: Painter? = null,
    leadingDescription: String? = null
) {
    val shape = RoundedCornerShape(18.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 420f)
    )
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        interactionSource = interactionSource,
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) border else border.copy(alpha = 0.45f)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = contentColor.copy(alpha = 0.45f)
        ),
        modifier = modifier
            .height(48.dp)
            .semantics { contentDescription = text }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(
                color = if (enabled) container else container.copy(alpha = 0.25f),
                shape = shape
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingPainter != null) {
                Image(
                    painter = leadingPainter,
                    contentDescription = leadingDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        }
    }
}
