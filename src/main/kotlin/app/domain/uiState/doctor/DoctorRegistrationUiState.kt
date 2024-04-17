package app.domain.uiState.doctor

import app.domain.util.result.*

data class DoctorRegistrationUiState(
    val name: String = "",
    val surname: String = "",
    val fathersName: String = "",
    val age: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val position: String = "",
    val salary: String = "",
    val canReceiveAppointments: Boolean = false,
    val login: String = "",
    val password: String = "",
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val registrationResult: TaskResult = TaskResult.NotCompleted
)