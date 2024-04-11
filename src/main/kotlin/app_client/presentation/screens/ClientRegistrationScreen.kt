package app_client.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.*
import app_client.domain.uiEvent.*
import app_client.domain.uiState.*
import app_shared.presentation.codes.*
import app_shared.presentation.components.*
import app_shared.presentation.theme.*
import moe.tlaster.precompose.navigation.*

@Composable
fun ClientRegistrationScreen(
    uiState: ClientRegistrationUiState,
    onUiEvent: (ClientRegistrationUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState())
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Enter) onUiEvent(ClientRegistrationUiEvent.Register)
                false
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Register a patient",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Main info",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            LoginRegistrationTextField(
                label = "Name*",
                startValue = uiState.name,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateName(newValue)) },
                errorText = if (uiState.errorCodes.contains(1001)) parseClientRegistrationErrorCode(1001) else null
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Surname*",
                startValue = uiState.surname,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateSurname(newValue)) },
                errorText = if (uiState.errorCodes.contains(1002)) parseClientRegistrationErrorCode(1002) else null
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Fathers name",
                startValue = uiState.fathersName,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateFathersName(newValue)) },
                errorText = null
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Patient's other data",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            LoginRegistrationTextField(
                label = "Age*",
                startValue = uiState.age,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateAge(newValue)) },
                errorText = when {
                    uiState.errorCodes.contains(1003) -> parseClientRegistrationErrorCode(1003)
                    uiState.errorCodes.contains(1004) -> parseClientRegistrationErrorCode(1004)
                    else -> null
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Address*",
                startValue = uiState.address,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateAddress(newValue)) },
                errorText = if (uiState.errorCodes.contains(1005)) parseClientRegistrationErrorCode(1005) else null
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Contact info",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            LoginRegistrationTextField(
                label = "Phone*",
                startValue = uiState.phone,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdatePhone(newValue)) },
                errorText = when {
                    uiState.errorCodes.contains(1006) -> parseClientRegistrationErrorCode(1006)
                    uiState.errorCodes.contains(1007) -> parseClientRegistrationErrorCode(1007)
                    else -> null
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Email",
                startValue = uiState.email,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateEmail(newValue)) },
                errorText = if (uiState.errorCodes.contains(1008)) parseClientRegistrationErrorCode(1008) else null
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Account info",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            LoginRegistrationTextField(
                label = "Login*",
                startValue = uiState.login,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateLogin(newValue)) },
                errorText = when {
                    uiState.errorCodes.contains(1009) -> parseClientRegistrationErrorCode(1009)
                    uiState.errorCodes.contains(1012) -> parseClientRegistrationErrorCode(1012)
                    else -> null
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Password*",
                startValue = uiState.password,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdatePassword(newValue)) },
                isPassword = true,
                errorText = when {
                    uiState.errorCodes.contains(1010) -> parseClientRegistrationErrorCode(1010)
                    uiState.errorCodes.contains(1011) -> parseClientRegistrationErrorCode(1011)
                    else -> null
                }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.width(160.dp),
            onClick = {
                onUiEvent(ClientRegistrationUiEvent.Register)
            },
            colors = buttonColors()
        ) {
            Text(
                text = "Register",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}