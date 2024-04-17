package app.presentation.screens.doctor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import app.domain.uiEvent.doctor.*
import app.domain.uiState.doctor.*
import app.domain.util.numbers.*
import app.presentation.codes.*
import app.presentation.components.common.*
import app.presentation.theme.*

@Composable
fun DoctorInfoScreen(
    uiState: DoctorInfoUiState,
    onUiEvent: (DoctorInfoUiEvent) -> Unit,
    userDoctorId: Int,
    canEdit: Boolean,
    isRemote: Boolean
) {
    var saveChangesAllowed by remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (!canEdit && !isRemote) "Your work information" else "${uiState.name}\'s work information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (uiState.errorCodes.contains(1002)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = parseDefaultErrorCode(1002),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            } else if (uiState.errorCodes.contains(1001)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = parseDefaultErrorCode(1001),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Main",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.editMode) {
                DefaultTextField(
                    startValue = uiState.name,
                    label = "Name:",
                    onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateName(it)) }
                )
            } else {
                Text(
                    text = "Name: ${uiState.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.editMode) {
                DefaultTextField(
                    startValue = uiState.surname,
                    label = "Surname:",
                    onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateSurname(it)) }
                )
            } else {
                Text(
                    text = "Surname: ${uiState.surname}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            if (uiState.fathersName.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.editMode) {
                    DefaultTextField(
                        startValue = uiState.fathersName,
                        label = "Father's name:",
                        onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateFathersName(it)) }
                    )
                } else {
                    Text(
                        text = "Father's name: ${uiState.fathersName}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.editMode) {
                DefaultTextField(
                    startValue = uiState.position,
                    label = "Position:",
                    onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdatePosition(it)) }
                )
            } else {
                Text(
                    text = "Position: ${uiState.position}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.editMode) {
                DefaultTextField(
                    startValue = uiState.salary,
                    label = "Salary:",
                    onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateSalary(it)) },
                    onlyNumbers = true
                )
            } else {
                Text(
                    text = "Salary: ${uiState.salary}$",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.editMode) {
                DefaultTextField(
                    startValue = uiState.age,
                    label = "Age:",
                    onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateAge(it)) },
                    onlyIntegerNumbers = true
                )
            } else {
                Text(
                    text = "Age: ${uiState.age}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.editMode) {
                DefaultTextField(
                    startValue = uiState.address,
                    label = "Address:",
                    onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateAddress(it)) }
                )
            } else {
                Text(
                    text = "Address: ${uiState.address}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Contact info",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.editMode) {
                DefaultTextField(
                    startValue = uiState.phone,
                    label = "Phone number:",
                    onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdatePhone(it)) }
                )
            } else {
                Text(
                    text = "Phone number: ${uiState.phone}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.editMode) {
                DefaultTextField(
                    startValue = uiState.email,
                    label = "Email:",
                    onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateEmail(it)) }
                )
            } else {
                Text(
                    text = "Email: ${uiState.email.ifBlank { "None" }}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Designation",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.editMode) {
                OptionsTextField(
                    startValue = uiState.designationName,
                    label = "Room name:",
                    options = uiState.preloadedRooms.map { it.name },
                    onOptionSelected = { index ->
                        onUiEvent(DoctorInfoUiEvent.UpdateDesignationName(uiState.preloadedRooms[index].name))
                        onUiEvent(DoctorInfoUiEvent.UpdateDesignationFloor(uiState.preloadedRooms[index].floor.toString()))
                        onUiEvent(DoctorInfoUiEvent.UpdateDesignationNumber(uiState.preloadedRooms[index].number.toString()))
                        onUiEvent(DoctorInfoUiEvent.UpdateDesignationIndex(index))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Floor: ${uiState.designationFloor}",
                    style = MaterialTheme.typography.bodyLarge.copy(),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Number: ${uiState.designationNumber}",
                    style = MaterialTheme.typography.bodyLarge.copy(),
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Text(
                    text = if (uiState.designationName.isNotBlank()) {
                        "Room ${uiState.designationNumber}, ${uiState.designationName} on ${uiState.designationFloor.toInt().asOrdinal()} floor"
                    } else {
                        "None"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            //----
            if (canEdit) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Your schedule",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.editMode) {
                    DefaultTextField(
                        startValue = uiState.startDay,
                        label = "Start day:",
                        onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateStartDay(it)) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DefaultTextField(
                        startValue = uiState.endDay,
                        label = "End day:",
                        onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateEndDay(it)) }
                    )
                } else {
                    Text(
                        text = "Working days: ${uiState.startDay} - ${uiState.endDay}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.editMode) {
                    var startTimeError by remember {
                        mutableStateOf(false)
                    }

                    var endTimeError by remember {
                        mutableStateOf(false)
                    }

                    LaunchedEffect(key1 = startTimeError, key2 = endTimeError, block = {
                        saveChangesAllowed = !startTimeError && !endTimeError
                    })

                    TimeTextField(
                        startValue = uiState.startTime,
                        label = "Start time:",
                        onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateStartTime(it)) },
                        isError = startTimeError,
                        onErrorChange = { isError ->
                            startTimeError = isError
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TimeTextField(
                        startValue = uiState.endTime,
                        label = "End time:",
                        onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateEndTime(it)) },
                        isError = endTimeError,
                        onErrorChange = { isError ->
                            endTimeError = isError
                        }
                    )
                } else {
                    Text(
                        text = "Working time: ${uiState.startTime} - ${uiState.endTime}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.editMode) {
                    DefaultTextField(
                        startValue = uiState.hoursForRest,
                        label = "Rest hours per day:",
                        onValueChange = { onUiEvent(DoctorInfoUiEvent.UpdateRestHours(it)) }
                    )
                } else {
                    Text(
                        text = "Rest hours per day: ${uiState.hoursForRest}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
            //----
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Appointments",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Pending: ${uiState.pendingAppointments}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Finished: ${uiState.finishedAppointments}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (uiState.editMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.width(180.dp),
                        onClick = {
                            if (saveChangesAllowed) {
                                onUiEvent(DoctorInfoUiEvent.SaveChanges(userDoctorId))
                            }
                        },
                        colors = buttonColors()
                    ) {
                        Text(
                            text = "Save changes",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        if (canEdit) {
            Icon(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.TopEnd)
                    .clickable(onClick = {
                        onUiEvent(DoctorInfoUiEvent.ToggleEditMode)
                    })
                    .padding(8.dp),
                imageVector = Icons.Default.Edit,
                contentDescription = "Go back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}