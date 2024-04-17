package app_client.domain.uiState

import app_shared.domain.model.util.result.*

data class ClientRegistrationUiState(
    val name: String = "",
    val surname: String = "",
    val fathersName: String = "",
    val age: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val login: String = "",
    val password: String = "",
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val registrationResult: TaskResult = TaskResult.NotCompleted
)