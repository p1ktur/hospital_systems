package app_doctor.domain.uiState

data class DoctorLoginUiState(
    val login: String = "",
    val password: String = "",
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false
)