package com.Faxzoku9.Qozcud.presentation.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.core.theme.Danger
import com.Faxzoku9.Qozcud.core.theme.DeepLakeDark
import com.Faxzoku9.Qozcud.core.theme.IceWhite
import com.Faxzoku9.Qozcud.core.theme.Lantern
import com.Faxzoku9.Qozcud.core.theme.TextMuted

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onEndTrip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResume,
        containerColor = DeepLakeDark,
        titleContentColor = IceWhite,
        textContentColor = TextMuted,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = stringResource(R.string.pause_title),
                style = MaterialTheme.typography.headlineSmall,
                color = IceWhite
            )
        },
        text = {
            Text(
                text = stringResource(R.string.hint_resolved),
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text(
                    text = stringResource(R.string.pause_resume),
                    style = MaterialTheme.typography.labelLarge,
                    color = Lantern
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onEndTrip) {
                Text(
                    text = stringResource(R.string.pause_end),
                    style = MaterialTheme.typography.labelLarge,
                    color = Danger
                )
            }
        }
    )
}
