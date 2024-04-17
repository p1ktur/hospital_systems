package app.presentation.screens.doctor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import app.domain.tabNavigator.*
import app.domain.uiEvent.doctor.*
import app.domain.uiState.doctor.*
import app.domain.util.result.*
import app.presentation.codes.*
import app.presentation.components.common.*
import app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorRegistrationScreen(
    navController: NavController,
    uiState: DoctorRegistrationUiState,
    onUiEvent: (DoctorRegistrationUiEvent) -> Unit,
    forResult: Boolean
) {
    LaunchedEffect(key1 = uiState.registrationResult, block = {
        when (uiState.registrationResult) {
            TaskResult.Failure -> Unit
            TaskResult.NotCompleted -> Unit
            is TaskResult.Success<*> -> {
                val userDoctorId = uiState.registrationResult.data as? Int

                if (forResult) {
                    navController.goBackWith(userDoctorId)
                } else {
                    navController.navigate("/info/worker/${userDoctorId}")
                }
                onUiEvent(DoctorRegistrationUiEvent.ForgetRegistration)
            }
        }
    })

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
            text = "Register a worker",
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
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdateName(newValue)) },
                errorText = if (uiState.errorCodes.contains(1001)) parseWorkerRegistrationErrorCode(1001) else null
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Surname*",
                startValue = uiState.surname,
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdateSurname(newValue)) },
                errorText = if (uiState.errorCodes.contains(1002)) parseWorkerRegistrationErrorCode(1002) else null
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Fathers name",
                startValue = uiState.fathersName,
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdateFathersName(newValue)) },
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
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdateAge(newValue)) },
                errorText = when {
                    uiState.errorCodes.contains(1003) -> parseWorkerRegistrationErrorCode(1003)
                    uiState.errorCodes.contains(1004) -> parseWorkerRegistrationErrorCode(1004)
                    else -> null
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Address*",
                startValue = uiState.address,
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdateAddress(newValue)) },
                errorText = if (uiState.errorCodes.contains(1005)) parseWorkerRegistrationErrorCode(1005) else null
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
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdatePhone(newValue)) },
                errorText = when {
                    uiState.errorCodes.contains(1006) -> parseWorkerRegistrationErrorCode(1006)
                    uiState.errorCodes.contains(1007) -> parseWorkerRegistrationErrorCode(1007)
                    else -> null
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Email",
                startValue = uiState.email,
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdateEmail(newValue)) },
                errorText = if (uiState.errorCodes.contains(1008)) parseWorkerRegistrationErrorCode(1008) else null
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Work info",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            LoginRegistrationTextField(
                label = "Position*",
                startValue = uiState.position,
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdatePosition(newValue)) },
                errorText = if (uiState.errorCodes.contains(1015)) parseWorkerRegistrationErrorCode(1015) else null
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Salary",
                startValue = uiState.salary,
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdateSalary(newValue)) },
                errorText = if (uiState.errorCodes.contains(1014)) parseWorkerRegistrationErrorCode(1014) else null
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Can receive appointments",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Checkbox(
                checked = uiState.canReceiveAppointments,
                onCheckedChange = { newValue ->
                    onUiEvent(DoctorRegistrationUiEvent.UpdateCanReceiveAppointments(newValue))
                }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
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
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdateLogin(newValue)) },
                errorText = when {
                    uiState.errorCodes.contains(1009) -> parseWorkerRegistrationErrorCode(1009)
                    uiState.errorCodes.contains(1012) -> parseWorkerRegistrationErrorCode(1012)
                    else -> null
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            LoginRegistrationTextField(
                label = "Password*",
                startValue = uiState.password,
                onValueChange = { newValue -> onUiEvent(DoctorRegistrationUiEvent.UpdatePassword(newValue)) },
                isPassword = true,
                errorText = when {
                    uiState.errorCodes.contains(1010) -> parseWorkerRegistrationErrorCode(1010)
                    uiState.errorCodes.contains(1011) -> parseWorkerRegistrationErrorCode(1011)
                    else -> null
                }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.width(160.dp),
            onClick = {
                onUiEvent(DoctorRegistrationUiEvent.Register)
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