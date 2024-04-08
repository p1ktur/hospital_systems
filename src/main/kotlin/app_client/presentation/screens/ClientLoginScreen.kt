package app_client.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import app_client.domain.uiEvent.*
import app_client.domain.uiState.*
import app_shared.presentation.codes.*
import app_shared.presentation.components.*
import app_shared.presentation.theme.*
import moe.tlaster.precompose.navigation.*

@Composable
fun ClientLoginScreen(
    navigator: Navigator,
    uiState: ClientLoginUiState,
    onUiEvent: (ClientLoginUiEvent) -> Unit
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
            text = "Log into your account",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (uiState.errorCodes.contains(1013)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = parseLoginErrorCode(1013),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Spacer(modifier = Modifier.height(32.dp))
        }
        LoginRegistrationTextField(
            label = "Login*",
            startValue = uiState.login,
            onValueChange = { newValue -> onUiEvent(ClientLoginUiEvent.UpdateLogin(newValue)) },
            errorText = if (uiState.errorCodes.contains(1009)) parseLoginErrorCode(1009) else null
        )
        Spacer(modifier = Modifier.height(32.dp))
        LoginRegistrationTextField(
            label = "Password*",
            startValue = uiState.password,
            onValueChange = { newValue -> onUiEvent(ClientLoginUiEvent.UpdatePassword(newValue)) },
            isPassword = true,
            errorText = if (uiState.errorCodes.contains(1010)) parseLoginErrorCode(1010) else null
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.width(160.dp),
            onClick = {
                onUiEvent(ClientLoginUiEvent.Login)
            },
            colors = buttonColors()
        ) {
            Text(
                text = "Log in",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}