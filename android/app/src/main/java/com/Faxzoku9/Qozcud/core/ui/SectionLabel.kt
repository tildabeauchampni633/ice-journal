package com.Faxzoku9.Qozcud.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.Faxzoku9.Qozcud.core.theme.TextMuted

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextMuted,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        textAlign = align,
        maxLines = 2,
        modifier = modifier
    )
}
