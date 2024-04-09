package app_doctor.domain.uiState

import app_doctor.domain.model.*

data class DoctorLoginUiState(
    val login: String = "",
    val password: String = "",
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val doctorLoginStatus: DoctorLoginStatus = DoctorLoginStatus.LoggedOut
)