package app.presentation.screens.shared

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import app.domain.model.shared.appointment.*
import app.domain.model.shared.payment.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import app.domain.util.args.*
import app.presentation.codes.*
import app.presentation.components.appointments.*
import app.presentation.components.common.*
import app.presentation.components.dialog.*
import kotlinx.coroutines.*

@Composable
fun AppointmentsScreen(
    navController: NavController,
    uiState: AppointmentsUiState,
    onUiEvent: (AppointmentsUiEvent) -> Unit,
    userDoctorId: Int?,
    userClientId: Int?,
    appArgs: AppArgs
) {
    val coroutineScope = rememberCoroutineScope()

    var dialogAppointment by remember { mutableStateOf<Appointment?>(null) }
    var dialogPayment by remember { mutableStateOf<Payment.Default?>(null) }
    var dialogResultNotesAndPrice by remember { mutableStateOf<Pair<String, Float>?>(null) }

    var navigatingToCreateAppointmentJob: Job? = null

    LaunchedEffect(key1 = uiState.payments) {
        val result = uiState.results.find { it.id == dialogAppointment?.resultId }
        dialogPayment = uiState.payments.find { it.id == result?.paymentId }
    }

    LaunchedEffect(key1 = uiState.openId, block = {
        if (uiState.openId != null) {
            val foundAppointment = uiState.appointments.find { it.id == uiState.openId }
            val foundAppointmentResult = uiState.results.find { it.id == foundAppointment?.resultId }

            if (foundAppointment != null && foundAppointmentResult != null) {
                dialogAppointment = foundAppointment
                dialogResultNotesAndPrice = foundAppointmentResult.let { it.notes to it.price }
                onUiEvent(AppointmentsUiEvent.ShowInfoDialog)
            }
        }
    })

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
            if (uiState.errorCodes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                uiState.errorCodes.forEach {
                    Text(
                        text = parseDefaultErrorCode(it),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
                            appointment = appointment,
                            appointmentResult = appointmentResult,
                            payment = payment,
                            appArgs = appArgs,
                            isSelected = dialogAppointment == appointment,
                            onClick = {
                                dialogPayment = payment
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
                navController = navController,
                uiState = uiState,
                onUiEvent = onUiEvent,
                appArgs = appArgs,
                visible = uiState.showInfoDialog,
                userDoctorId = userDoctorId ?: -1,
                dialogAppointment = dialogAppointment!!,
                dialogPayment = dialogPayment,
                dialogResultNotesAndPrice = dialogResultNotesAndPrice,
                onClose = {
                    onUiEvent(AppointmentsUiEvent.HideInfoDialog)
                    if (uiState.editMode) onUiEvent(AppointmentsUiEvent.DisableEditMode)
                }
            )
        }
        if (appArgs == AppArgs.DOCTOR) {
            if (uiState.scheduleData != null) {
                ScheduleDateTimePickerDialog(
                    visible = uiState.showDateTimePickerDialog,
                    appArgs = appArgs,
                    scheduleData = uiState.scheduleData,
                    busyFutureDates = uiState.busyFutureDates,
                    onClose = {
                        onUiEvent(AppointmentsUiEvent.HideDateTimePickerDialog)
                    },
                    onDateTimePicked = { localDateTime ->
                        if (userDoctorId != null && uiState.userWorkerIdForAppointment != null && uiState.userClientIdForAppointment != null) {
                            onUiEvent(
                                AppointmentsUiEvent.CreateAppointment(
                                    selfUserWorkerId = userDoctorId,
                                    userWorkerId = uiState.userWorkerIdForAppointment,
                                    userClientId = uiState.userClientIdForAppointment,
                                    localDateTime = localDateTime
                                )
                            )
                        }

                        onUiEvent(AppointmentsUiEvent.HideDateTimePickerDialog)
                    }
                )
            }
            Row(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(onClick = {
                            if (navigatingToCreateAppointmentJob == null) {
                                navigatingToCreateAppointmentJob = coroutineScope.launch {
                                    val foundUserDoctorId = navController.navigateForResult("/find_worker/true") as Int?

                                    if (foundUserDoctorId != null) {
                                        val foundUserClientId = navController.navigateForResult("/find_patient/true") as Int?

                                        if (foundUserClientId != null) {
                                            onUiEvent(AppointmentsUiEvent.ShowDateTimePickerDialog(appArgs, foundUserDoctorId, foundUserClientId))
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
                                    val foundUserClientId = navController.navigateForResult("/find_patient/true") as Int?

                                    if (userDoctorId != null && foundUserClientId != null) {
                                        onUiEvent(AppointmentsUiEvent.ShowDateTimePickerDialog(appArgs, userDoctorId, foundUserClientId))
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
        } else if (appArgs == AppArgs.CLIENT) {
            if (uiState.scheduleData != null) {
                ScheduleDateTimePickerDialog(
                    visible = uiState.showDateTimePickerDialog,
                    appArgs = appArgs,
                    scheduleData = uiState.scheduleData,
                    busyFutureDates = uiState.busyFutureDates,
                    onClose = {
                        onUiEvent(AppointmentsUiEvent.HideDateTimePickerDialog)
                    },
                    onDateTimePicked = { localDateTime ->
                        if (userClientId != null && uiState.userWorkerIdForAppointment != null && uiState.userClientIdForAppointment != null) {
                            onUiEvent(
                                AppointmentsUiEvent.RequestApprovalForAppointment(
                                    userWorkerId = uiState.userWorkerIdForAppointment,
                                    userClientId = userClientId,
                                    localDateTime = localDateTime
                                )
                            )
                        }

                        onUiEvent(AppointmentsUiEvent.HideDateTimePickerDialog)
                    }
                )
            }
            Icon(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.TopEnd)
                    .clickable(onClick = {
                        if (navigatingToCreateAppointmentJob == null) {
                            navigatingToCreateAppointmentJob = coroutineScope.launch {
                                val foundUserDoctorId = navController.navigateForResult("/find_worker/true") as Int?

                                if (foundUserDoctorId != null && userClientId != null) {
                                    onUiEvent(AppointmentsUiEvent.ShowDateTimePickerDialog(appArgs, foundUserDoctorId, userClientId))
                                }

                                navigatingToCreateAppointmentJob = null
                            }
                        }
                    })
                    .padding(8.dp),
                imageVector = Icons.Default.Add,
                contentDescription = "Request appointment",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun AppointmentInfoDialog(
    navController: NavController,
    uiState: AppointmentsUiState,
    onUiEvent: (AppointmentsUiEvent) -> Unit,
    appArgs: AppArgs,
    visible: Boolean,
    userDoctorId: Int,
    dialogAppointment: Appointment,
    dialogPayment: Payment.Default?,
    dialogResultNotesAndPrice: Pair<String, Float>?,
    onClose: () -> Unit
) {
    var resultNotesAndPrice by remember(dialogResultNotesAndPrice) {
        mutableStateOf(dialogResultNotesAndPrice)
    }

    var approved by remember(dialogAppointment) {
        mutableStateOf(dialogAppointment.approved)
    }

    DetailsDialog(
        size = DpSize(600.dp, 400.dp),
        title = "Appointment Result Info",
        visible = visible,
        onClose = onClose
    ) {
        if (appArgs == AppArgs.DOCTOR) {
            if (approved) {
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
                            if (dialogPayment == null) {
                                DefaultTextField(
                                    startValue = priceText,
                                    label = "Price:",
                                    onValueChange = { priceText = it },
                                    onlyNumbers = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            } else {
                                Text(
                                    text = "Price: $priceText$",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
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
                            if (dialogPayment != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Payed ${dialogPayment.payedAmount}\$ from ${dialogPayment.payedAccount}",
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
                    if (dialogPayment == null) {
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
            } else { // Not approved
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Text(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate("/info/patient/${dialogAppointment.userClientId}")
                                }
                            ),
                            text = "@${dialogAppointment.clientLogin}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = " requested an approval on ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = dialogAppointment.date,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable(onClick = {
                                onUiEvent(
                                    AppointmentsUiEvent.ApproveAppointment(
                                        userWorkerId = userDoctorId,
                                        appointmentId = dialogAppointment.id
                                    )
                                )

                                approved = true
                            })
                            .padding(8.dp),
                        imageVector = Icons.Default.Done,
                        contentDescription = "Approve",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Icon(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable(onClick = {
                                onUiEvent(
                                    AppointmentsUiEvent.DenyRequestedAppointment(
                                        userWorkerId = userDoctorId,
                                        appointmentId = dialogAppointment.id
                                    )
                                )
                                onClose()
                            })
                            .padding(8.dp),
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Deny",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            if (appArgs == AppArgs.CLIENT) {
                if (approved) {
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
                            if (dialogPayment == null) {
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
                                    text = "Your payment: ${dialogPayment.payedAmount} from ${dialogPayment.payedAccount}",
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
                } else { //Not approved
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Waiting approval from ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate("/info/worker/${dialogAppointment.userDoctorId}")
                                }
                            ),
                            text = dialogAppointment.doctorName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else {
                if (approved) {
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
                        if (dialogPayment != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Payed ${dialogPayment.payedAmount}$ from ${dialogPayment.payedAccount}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else { //Not approved
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    ) {
                        Text(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate("/info/patient/${dialogAppointment.userClientId}")
                                }
                            ),
                            text = "@${dialogAppointment.clientLogin}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = " requested approval from ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            modifier = Modifier.clickable(
                                onClick = {
                                    navController.navigate("/info/worker/${dialogAppointment.userDoctorId}")
                                }
                            ),
                            text = dialogAppointment.doctorName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}