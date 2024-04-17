package app.presentation.components.hospitalizations

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import app.domain.model.shared.*
import app.domain.model.shared.hospitalization.*
import app.domain.util.args.*

@Composable
fun HospitalizationView(
    modifier: Modifier,
    hospitalization: Hospitalization,
    payment: Payment?,
    appArgs: AppArgs,
    onClick: (() -> Unit)? = null,
    onClientNameClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clickable(
                onClick = {
                    onClick?.invoke()
                }
            )
            .padding(vertical = 12.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            when (appArgs) {
                AppArgs.CLIENT -> Row {
                    Text(
                        text = "Your hospitalization",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                else -> Row {
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                onClientNameClick?.invoke()
                            }
                        ),
                        text = "${hospitalization.clientName}\'s ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Your appointment with ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Started on ${hospitalization.startDate}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (hospitalization.endDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ended on ${hospitalization.endDate}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = if (hospitalization.endDate == null) {
                    "Currently"
                } else {
                    "Finished for ${hospitalization.price}"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (payment == null) {
                    "Not payed yet"
                } else {
                    "Payed ${payment.payedAmount}"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}