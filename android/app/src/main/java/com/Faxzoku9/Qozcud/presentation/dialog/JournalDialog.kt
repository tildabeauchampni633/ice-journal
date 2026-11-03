package com.Faxzoku9.Qozcud.presentation.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.core.theme.DeepLakeDark
import com.Faxzoku9.Qozcud.core.theme.FrostSurfaceHi
import com.Faxzoku9.Qozcud.core.theme.IceWhite
import com.Faxzoku9.Qozcud.core.theme.Lantern
import com.Faxzoku9.Qozcud.core.theme.TextMuted
import com.Faxzoku9.Qozcud.presentation.menu.JournalRow

@Composable
fun JournalDialog(
    rows: List<JournalRow>,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = DeepLakeDark,
        titleContentColor = IceWhite,
        textContentColor = TextMuted,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = stringResource(R.string.journal_title),
                style = MaterialTheme.typography.headlineSmall,
                color = IceWhite
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.heightIn(max = 360.dp)
            ) {
                items(rows, key = { it.speciesId }) { row ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(row.drawableRes),
                            contentDescription = stringResource(row.nameRes),
                            contentScale = ContentScale.Fit,
                            alpha = if (row.discovered) 1f else 0.25f,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(row.nameRes),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (row.discovered) IceWhite else TextMuted
                            )
                            Text(
                                text = stringResource(row.noteRes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                            if (row.discovered) {
                                Text(
                                    text = stringResource(R.string.journal_best, row.bestWeightKg),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Lantern
                                )
                                Text(
                                    text = stringResource(row.lakeNameRes),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FrostSurfaceHi
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.journal_not_logged),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FrostSurfaceHi
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) {
                Text(
                    text = stringResource(R.string.dialog_close),
                    style = MaterialTheme.typography.labelLarge,
                    color = Lantern
                )
            }
        }
    )
}
