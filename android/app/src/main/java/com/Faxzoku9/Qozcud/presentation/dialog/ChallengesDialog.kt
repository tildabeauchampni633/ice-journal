package com.Faxzoku9.Qozcud.presentation.dialog

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.core.theme.DeepLakeDark
import com.Faxzoku9.Qozcud.core.theme.IceWhite
import com.Faxzoku9.Qozcud.core.theme.Lantern
import com.Faxzoku9.Qozcud.core.theme.SafeBand
import com.Faxzoku9.Qozcud.core.theme.TextMuted
import com.Faxzoku9.Qozcud.core.ui.GlassCard
import com.Faxzoku9.Qozcud.presentation.menu.ChallengeRow

@Composable
fun ChallengesDialog(
    rows: List<ChallengeRow>,
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
                text = stringResource(R.string.challenges_title),
                style = MaterialTheme.typography.headlineSmall,
                color = IceWhite
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.heightIn(max = 340.dp)
            ) {
                items(rows, key = { it.id }) { row ->
                    GlassCard(
                        corner = 18.dp,
                        innerPadding = 14.dp,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(
                                    if (row.completed) R.drawable.ic_check else R.drawable.ic_cross
                                ),
                                contentDescription = null,
                                tint = if (row.completed) SafeBand else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(row.titleRes),
                                style = MaterialTheme.typography.titleMedium,
                                color = IceWhite
                            )
                        }
                        Spacer(modifier = Modifier.size(6.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(row.descriptionRes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.points_value, row.rewardPoints),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Lantern
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(
                                        if (row.completed) R.string.challenge_done else R.string.challenge_open
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (row.completed) SafeBand else TextMuted
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
