package app_client.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.foundation.text2.*
import androidx.compose.foundation.text2.input.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import app_client.domain.uiEvent.*
import app_client.domain.uiState.*
import app_shared.presentation.codes.*
import app_shared.presentation.theme.*
import moe.tlaster.precompose.navigation.*
import java.util.*

@Composable
fun ClientRegistrationScreen(
    navigator: Navigator,
    uiState: ClientRegistrationUiState,
    onUiEvent: (ClientRegistrationUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Register an account",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Patient's name",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            RegistrationTextField(
                label = "Name*",
                value = uiState.name,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateName(newValue)) },
                errorText = if (uiState.errorCodes.contains(1001)) parseRegistrationErrorCode(1001) else null
            )
            Spacer(modifier = Modifier.width(16.dp))
            RegistrationTextField(
                label = "Surname*",
                value = uiState.surname,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateSurname(newValue)) },
                errorText = if (uiState.errorCodes.contains(1002)) parseRegistrationErrorCode(1002) else null
            )
            Spacer(modifier = Modifier.width(16.dp))
            RegistrationTextField(
                label = "Fathers name",
                value = uiState.fathersName,
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
            RegistrationTextField(
                label = "Age*",
                value = uiState.age,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateAge(newValue)) },
                errorText = when {
                    uiState.errorCodes.contains(1003) -> parseRegistrationErrorCode(1003)
                    uiState.errorCodes.contains(1004) -> parseRegistrationErrorCode(1004)
                    else -> null
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            RegistrationTextField(
                label = "Address*",
                value = uiState.address,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateAddress(newValue)) },
                errorText = if (uiState.errorCodes.contains(1005)) parseRegistrationErrorCode(1005) else null
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Additional info",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            RegistrationTextField(
                label = "Phone*",
                value = uiState.phone,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdatePhone(newValue)) },
                errorText = when {
                    uiState.errorCodes.contains(1006) -> parseRegistrationErrorCode(1006)
                    uiState.errorCodes.contains(1007) -> parseRegistrationErrorCode(1007)
                    else -> null
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            RegistrationTextField(
                label = "Email",
                value = uiState.email,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateEmail(newValue)) },
                errorText = if (uiState.errorCodes.contains(1008)) parseRegistrationErrorCode(1008) else null
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
            RegistrationTextField(
                label = "Login*",
                value = uiState.login,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdateLogin(newValue)) },
                errorText = when {
                    uiState.errorCodes.contains(1009) -> parseRegistrationErrorCode(1009)
                    uiState.errorCodes.contains(1012) -> parseRegistrationErrorCode(1012)
                    else -> null
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            RegistrationTextField(
                label = "Password*",
                value = uiState.password,
                onValueChange = { newValue -> onUiEvent(ClientRegistrationUiEvent.UpdatePassword(newValue)) },
                isPassword = true,
                errorText = when {
                    uiState.errorCodes.contains(1010) -> parseRegistrationErrorCode(1010)
                    uiState.errorCodes.contains(1011) -> parseRegistrationErrorCode(1011)
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

@Composable
private fun RegistrationTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    errorText: String?
) {
    TextField(
        modifier = Modifier.width(320.dp),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        label = {
            Text(
                text = errorText ?: label,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        placeholder = {
            Text(
                text = "Type ${label.replaceFirstChar { it.lowercase(Locale.getDefault()) }}",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        isError = errorText != null
    )
}