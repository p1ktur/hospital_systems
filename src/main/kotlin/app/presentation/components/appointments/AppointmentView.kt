package app.presentation.components.appointments

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import app.domain.model.shared.appointment.*
import app.domain.model.shared.payment.*
import app.domain.util.args.*

@Composable
fun AppointmentView(
    modifier: Modifier = Modifier,
    appointment: Appointment,
    appointmentResult: AppointmentResult?,
    payment: Payment.Default?,
    appArgs: AppArgs,
    isSelected: Boolean,
    onClick: (() -> Unit)? = null,
    onDoctorNameClick: (() -> Unit)? = null,
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
                        text = "Your appointment with ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                onDoctorNameClick?.invoke()
                            }
                        ),
                        text = appointment.doctorName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                AppArgs.DOCTOR -> Row {
                    Text(
                        text = "Your appointment with ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                onClientNameClick?.invoke()
                            }
                        ),
                        text = appointment.clientName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                AppArgs.ADMIN -> Row {
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                onDoctorNameClick?.invoke()
                            }
                        ),
                        text = "${appointment.doctorName}\'s ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "appointment with ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                onClientNameClick?.invoke()
                            }
                        ),
                        text = appointment.clientName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "on ${appointment.date}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = if (appointmentResult == null) {
                    "To be"
                } else {
                    "Finished for ${appointmentResult.price}"
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