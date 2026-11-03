package com.Faxzoku9.Qozcud.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Faxzoku9.Qozcud.core.theme.DeepLakeDark
import com.Faxzoku9.Qozcud.core.theme.IceWhite
import com.Faxzoku9.Qozcud.core.theme.SafeBand
import com.Faxzoku9.Qozcud.core.theme.TextMuted

@Composable
fun CastBanner(
    visible: Boolean,
    labelRes: Int,
    positive: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible && labelRes != 0,
        enter = slideInVertically(tween(260)) { it / 2 } + fadeIn(tween(260)),
        exit = fadeOut(tween(200)),
        modifier = modifier
    ) {
        val accent = if (positive) SafeBand else TextMuted
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = DeepLakeDark.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(18.dp)
                )
                .border(
                    width = 1.dp,
                    color = accent.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(vertical = 10.dp, horizontal = 16.dp)
                .semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.titleMedium,
                color = if (positive) SafeBand else IceWhite,
                textAlign = TextAlign.Center
            )
        }
    }
}
