package app_client.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import app_client.domain.uiEvent.*
import app_client.domain.uiState.*
import app_shared.presentation.codes.*
import app_shared.presentation.components.common.*
import app_shared.presentation.theme.*

@Composable
fun ClientInfoScreen(
    uiState: ClientInfoUiState,
    onUiEvent: (ClientInfoUiEvent) -> Unit,
    userClientId: Int,
    canEdit: Boolean
) {
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
                text = if (!canEdit) "Your medical card" else "${uiState.name}\'s medical card",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (uiState.errorCodes.contains(1001)) {
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
                text = "Registered on ${uiState.registrationDate}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (uiState.isHospitalized) "Hospitalized" else "Not hospitalized",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
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
                    onValueChange = { onUiEvent(ClientInfoUiEvent.UpdateName(it)) }
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
                    onValueChange = { onUiEvent(ClientInfoUiEvent.UpdateSurname(it)) }
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
                        onValueChange = { onUiEvent(ClientInfoUiEvent.UpdateFathersName(it)) }
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
                    startValue = uiState.age,
                    label = "Age:",
                    onValueChange = { onUiEvent(ClientInfoUiEvent.UpdateAge(it)) },
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
                    onValueChange = { onUiEvent(ClientInfoUiEvent.UpdateAddress(it)) }
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
                    onValueChange = { onUiEvent(ClientInfoUiEvent.UpdatePhone(it)) }
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
                    onValueChange = { onUiEvent(ClientInfoUiEvent.UpdateEmail(it)) }
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
                text = "Visited: ${uiState.visitedAppointments}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Payments",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Pending: ${uiState.pendingPayments}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Payed: ${uiState.payedPayments}",
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
                            onUiEvent(ClientInfoUiEvent.SaveChanges(userClientId))
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
                        onUiEvent(ClientInfoUiEvent.ToggleEditMode)
                    })
                    .padding(8.dp),
                imageVector = Icons.Default.Edit,
                contentDescription = "Go back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}