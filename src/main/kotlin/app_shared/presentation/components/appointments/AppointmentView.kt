package app_shared.presentation.components.appointments

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import app_shared.domain.model.args.*
import app_shared.domain.model.database.dbModels.*

@Composable
fun AppointmentView(
    modifier: Modifier,
    appointment: Appointment,
    appointmentResult: AppointmentResult?,
    payment: Payment?,
    appArgs: AppArgs,
    onClick: (() -> Unit)? = null,
    onDoctorNameClick: (() -> Unit)? = null,
    onClientNameClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clickable(
                onClick = {
                    onClick?.invoke()
                }
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            when (appArgs) {
                AppArgs.CLIENT -> Row {
                    Text(
                        text = "Your appointment with ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                onDoctorNameClick?.invoke()
                            }
                        ),
                        text = appointment.doctorName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                AppArgs.DOCTOR -> Row {
                    Text(
                        text = "Your appointment with ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                onClientNameClick?.invoke()
                            }
                        ),
                        text = appointment.clientName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
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
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "appointment with ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        modifier = Modifier.clickable(
                            onClick = {
                                onClientNameClick?.invoke()
                            }
                        ),
                        text = appointment.doctorName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "on ${appointment.date}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
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
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (payment == null) {
                    "Not payed yet"
                } else {
                    "Payed ${payment.payedAmount}"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}