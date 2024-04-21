package app.presentation.components.payments

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import app.domain.model.shared.payment.*
import app.domain.util.args.*

@Composable
fun PaymentView(
    modifier: Modifier = Modifier,
    payment: Payment,
    appArgs: AppArgs,
    isSelected: Boolean,
    onClick: (() -> Unit)? = null,
    onClientNameClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8))
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8))
            .border(
                2.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(8)
            )
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
                        text = when (payment) {
                            is Payment.Default -> "Payment"
                            is Payment.Sub -> "Additional payment"
                        },
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
                        text = "${payment.clientName}\'s ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = when (payment) {
                            is Payment.Default -> "payment"
                            is Payment.Sub -> "additional payment"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            if (payment is Payment.Default) {
                Text(
                    text = when (payment.helpIdType) {
                        0 -> "Appointment"
                        1 -> "Hospitalization"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            if (payment.payedAmount == 0f) {
                Text(
                    text = "Not payed yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Text(
                    text = "Payed ${payment.payedAmount}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "from ${payment.payedAccount}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "on ${payment.time}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}