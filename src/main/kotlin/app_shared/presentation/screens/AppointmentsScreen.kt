package app_shared.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import app_shared.domain.model.forShared.appointment.*
import app_shared.domain.model.tabNavigator.*
import app_shared.domain.model.util.args.*
import app_shared.domain.uiEvent.*
import app_shared.domain.uiState.*
import app_shared.presentation.codes.*
import app_shared.presentation.components.appointments.*
import app_shared.presentation.components.common.*
import app_shared.presentation.components.dialog.*
import kotlinx.coroutines.*

@Composable
fun AppointmentsScreen(
    navController: NavController,
    uiState: AppointmentsUiState,
    onUiEvent: (AppointmentsUiEvent) -> Unit,
    userDoctorId: Int?,
    appArgs: AppArgs
) {
    val coroutineScope = rememberCoroutineScope()

    var dialogAppointment by remember { mutableStateOf<Appointment?>(null) }
    var dialogResultNotesAndPrice by remember { mutableStateOf<Pair<String, Float>?>(null) }

    var navigatingToCreateAppointmentJob: Job? = null

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = if (appArgs != AppArgs.ADMIN) "Your appointments" else "Appointments",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (uiState.errorCodes.contains(1001)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = parseDefaultErrorCode(1001),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }
            if (uiState.appointments.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.appointments) { appointment ->
                        val appointmentResult by remember(appointment) {
                            mutableStateOf(uiState.results.find { it.id == appointment.resultId })
                        }
                        val payment by remember(appointment) {
                            mutableStateOf(appointmentResult?.let { result -> uiState.payments.find { it.id == result.paymentId } })
                        }
                        AppointmentView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8))
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8))
                                .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8)),
                            appointment = appointment,
                            appointmentResult = appointmentResult,
                            payment = payment,
                            appArgs = appArgs,
                            onClick = {
                                dialogAppointment = appointment
                                dialogResultNotesAndPrice = appointmentResult?.let { it.notes to it.price }
                                onUiEvent(AppointmentsUiEvent.ShowInfoDialog)
                            },
                            onDoctorNameClick = if (appArgs == AppArgs.DOCTOR) null else ({
                                navController.navigate("/info/worker/${appointment.userDoctorId}")
                            }),
                            onClientNameClick = if (appArgs == AppArgs.CLIENT) null else ({
                                navController.navigate("/info/patient/${appointment.userClientId}")
                            }),
                        )
                    }
                }
            } else {
                Text(
                    text = "No appointments yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        if (dialogAppointment != null && ((appArgs == AppArgs.DOCTOR && userDoctorId != null) || appArgs != AppArgs.DOCTOR)) {
            AppointmentInfoDialog(
                uiState = uiState,
                onUiEvent = onUiEvent,
                appArgs = appArgs,
                visible = uiState.showInfoDialog,
                userDoctorId = userDoctorId ?: -1,
                dialogAppointment = dialogAppointment!!,
                dialogResultNotesAndPrice = dialogResultNotesAndPrice,
                onClose = {
                    onUiEvent(AppointmentsUiEvent.HideInfoDialog)
                    if (uiState.editMode) onUiEvent(AppointmentsUiEvent.DisableEditMode)
                }
            )
        }
        if (appArgs == AppArgs.DOCTOR) {
            DateTimePickerDialog(
                visible = uiState.showDateTimePickerDialog,
                onClose = {
                    onUiEvent(AppointmentsUiEvent.HideDateTimePickerDialog)
                },
                onDateTimePicked = { date ->
                    if (userDoctorId != null && uiState.userWorkerIdForAppointment != null && uiState.userClientIdForAppointment != null) {
                        onUiEvent(
                            AppointmentsUiEvent.CreateAppointment(
                                selfUserWorkerId = userDoctorId,
                                userWorkerId = uiState.userWorkerIdForAppointment,
                                userClientId = uiState.userClientIdForAppointment,
                                date = date
                            )
                        )
                    }

                    onUiEvent(AppointmentsUiEvent.HideDateTimePickerDialog)
                }
            )
            Row(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(onClick = {
                            if (navigatingToCreateAppointmentJob == null) {
                                navigatingToCreateAppointmentJob = coroutineScope.launch {
                                    val userWorkerId = navController.navigateForResult("/find_worker/true") as Int?

                                    if (userWorkerId != null) {
                                        val userClientId = navController.navigateForResult("/find_patient/true") as Int?

                                        if (userClientId != null) {
                                            onUiEvent(AppointmentsUiEvent.ShowDateTimePickerDialog(userWorkerId, userClientId))
                                        }
                                    }

                                    navigatingToCreateAppointmentJob = null
                                }
                            }
                        })
                        .padding(8.dp),
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Add appointment to a doctor",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(onClick = {
                            if (navigatingToCreateAppointmentJob == null) {
                                navigatingToCreateAppointmentJob = coroutineScope.launch {
                                    val userClientId = navController.navigateForResult("/find_patient/true") as Int?

                                    if (userDoctorId != null && userClientId != null) {
                                        onUiEvent(AppointmentsUiEvent.ShowDateTimePickerDialog(userDoctorId, userClientId))
                                    }

                                    navigatingToCreateAppointmentJob = null
                                }
                            }
                        })
                        .padding(8.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add appointment",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun AppointmentInfoDialog(
    uiState: AppointmentsUiState,
    onUiEvent: (AppointmentsUiEvent) -> Unit,
    appArgs: AppArgs,
    visible: Boolean,
    userDoctorId: Int,
    dialogAppointment: Appointment,
    dialogResultNotesAndPrice: Pair<String, Float>?,
    onClose: () -> Unit
) {
    val payment by remember(dialogAppointment) {
        val possibleResult = uiState.results.find { it.id == dialogAppointment.resultId }
        mutableStateOf(uiState.payments.find { possibleResult?.paymentId == it.id })
    }

    var resultNotesAndPrice by remember(dialogResultNotesAndPrice) {
        mutableStateOf(dialogResultNotesAndPrice)
    }

    DetailsDialog(
        size = DpSize(600.dp, 400.dp),
        title = "Appointment Result Info",
        visible = visible,
        onClose = onClose
    ) {
        if (appArgs == AppArgs.DOCTOR) {
            if (dialogResultNotesAndPrice == null) {
                var priceText by remember { mutableStateOf("") }
                var notesText by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    DefaultTextField(
                        startValue = "",
                        label = "Price:",
                        onValueChange = { priceText = it },
                        onlyNumbers = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DefaultTextField(
                        startValue = "",
                        label = "Result Notes:",
                        onValueChange = { notesText = it },
                        maxLength = 256,
                        multiLine = true
                    )
                }
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.TopEnd)
                        .clickable(onClick = {
                            if (priceText.isNotBlank() && notesText.isNotBlank()) {
                                onUiEvent(
                                    AppointmentsUiEvent.CreateAppointmentResult(
                                        userWorkerId = userDoctorId,
                                        appointmentId = dialogAppointment.id,
                                        price = priceText.toFloat(),
                                        notes = notesText
                                    )
                                )
                                onClose()
                            }
                        })
                        .padding(8.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add result",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.BottomEnd)
                        .clickable(onClick = {
                            onUiEvent(
                                AppointmentsUiEvent.DeleteAppointment(
                                    userWorkerId = userDoctorId,
                                    appointmentId = dialogAppointment.id
                                )
                            )
                            onClose()
                        })
                        .padding(8.dp),
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete appointment",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            } else {
                var priceText by remember { mutableStateOf(dialogResultNotesAndPrice.second.toString()) }
                var notesText by remember { mutableStateOf(dialogResultNotesAndPrice.first) }

                if (uiState.editMode) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if (payment == null) {
                            DefaultTextField(
                                startValue = priceText,
                                label = "Price:",
                                onValueChange = { priceText = it },
                                onlyNumbers = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        DefaultTextField(
                            startValue = notesText,
                            label = "Result Notes:",
                            onValueChange = { notesText = it },
                            maxLength = 256,
                            multiLine = true
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Price: ${dialogResultNotesAndPrice.second}$",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Result: ${dialogResultNotesAndPrice.first}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (payment != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Payed ${payment?.payedAmount}\$ from ${payment?.payedAccount}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.TopEnd)
                        .clickable(onClick = {
                            if ((uiState.editMode && notesText.isNotBlank() && priceText.isNotBlank()) || !uiState.editMode) {
                                onUiEvent(
                                    AppointmentsUiEvent.ToggleEditMode(
                                        userWorkerId = userDoctorId,
                                        resultId = dialogAppointment.resultId,
                                        price = priceText.toFloat(),
                                        notes = notesText
                                    )
                                )
                                if (uiState.editMode) {
                                    resultNotesAndPrice = notesText to priceText.toFloat()
                                }
                            }
                        })
                        .padding(8.dp),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit appointment result",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                if (payment == null) {
                    Icon(
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.BottomEnd)
                            .clickable(onClick = {
                                onUiEvent(
                                    AppointmentsUiEvent.DeleteAppointmentWithResult(
                                        userWorkerId = userDoctorId,
                                        appointmentId = dialogAppointment.id,
                                        resultId = dialogAppointment.resultId
                                    )
                                )
                                onClose()
                            })
                            .padding(8.dp),
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete appointment",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            if (appArgs == AppArgs.CLIENT) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = if (dialogResultNotesAndPrice == null) Alignment.Start else Alignment.CenterHorizontally
                ) {
                    if (dialogResultNotesAndPrice != null) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Price: ${dialogResultNotesAndPrice.second}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Result: ${dialogResultNotesAndPrice.first}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (payment == null) {
                            Text(
                                text = "Your have not payed yet.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        onUiEvent(
                                            AppointmentsUiEvent.PayForAppointment(
                                                userClientId = dialogAppointment.userClientId,
                                                appointmentResultId = dialogAppointment.resultId,
                                                payedAmount = dialogResultNotesAndPrice.second,
                                                payedAccount = "@${dialogAppointment.clientLogin}'s Account"
                                            )
                                        )
                                        onClose()
                                    }
                                ),
                                text = "Click to pay",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.tertiary,
                                textDecoration = TextDecoration.Underline
                            )
                        } else {
                            Text(
                                text = "Your payment: ${payment?.payedAmount} from ${payment?.payedAccount}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    } else {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "No result yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = if (dialogResultNotesAndPrice == null) Alignment.CenterHorizontally else Alignment.Start
                ) {
                    if (dialogResultNotesAndPrice != null) {
                        Text(
                            text = "Price: ${dialogResultNotesAndPrice.second}$",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Result: ${dialogResultNotesAndPrice.first}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        Text(
                            text = "No result yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    if (payment != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Payed ${payment?.payedAmount}$ from ${payment?.payedAccount}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}