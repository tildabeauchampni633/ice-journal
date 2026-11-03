package com.Faxzoku9.Qozcud.presentation.gameover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.core.theme.IceWhite
import com.Faxzoku9.Qozcud.core.theme.Lantern
import com.Faxzoku9.Qozcud.core.theme.TextMuted
import com.Faxzoku9.Qozcud.core.ui.GlassCard

@Composable
fun HeroCatchCard(
    state: GameOverUiState,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        if (state.hasHero && state.heroDrawableRes != 0) {
            Image(
                painter = painterResource(state.heroDrawableRes),
                contentDescription = stringResource(state.heroNameRes),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(state.heroNameRes),
                style = MaterialTheme.typography.titleLarge,
                color = IceWhite,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.weight_value, state.heroWeightKg),
                style = MaterialTheme.typography.displaySmall.copy(fontFeatureSettings = "tnum"),
                color = Lantern,
                textAlign = TextAlign.Center
            )
            if (state.heroWeatherRes != 0) {
                Text(
                    text = stringResource(
                        R.string.result_taken_at,
                        state.heroDepthM,
                        stringResource(state.heroWeatherRes)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = stringResource(R.string.cd_lure),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.result_no_fish),
                style = MaterialTheme.typography.titleLarge,
                color = IceWhite,
                textAlign = TextAlign.Center
            )
            if (state.emptyNoteRes != 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(state.emptyNoteRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
