package com.Faxzoku9.Qozcud.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Faxzoku9.Qozcud.R

@Composable
fun SelectChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    accent: Color,
    idleBorder: Color,
    selectedLabel: Color,
    idleLabel: Color,
    state: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    chipHeight: Dp = 40.dp
) {
    val shape = RoundedCornerShape(20.dp)
    val alpha = if (enabled) 1f else 0.45f
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .sizeIn(minHeight = 48.dp)
            .semantics { stateDescription = state }
            .clickable(enabled = true, onClick = onClick)
            .padding(vertical = 4.dp)
            .height(chipHeight)
            .background(
                color = if (selected) accent.copy(alpha = 0.14f) else Color.Transparent,
                shape = shape
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) accent.copy(alpha = alpha) else idleBorder.copy(alpha = alpha),
                shape = shape
            )
            .padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (!enabled) {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = idleLabel.copy(alpha = alpha),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) selectedLabel else idleLabel.copy(alpha = alpha),
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}
