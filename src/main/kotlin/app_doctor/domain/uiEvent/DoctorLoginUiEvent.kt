package app_doctor.domain.uiEvent

sealed class DoctorLoginUiEvent {
    data object Login : DoctorLoginUiEvent()

    data class UpdateLogin(val login: String) : DoctorLoginUiEvent()
    data class UpdatePassword(val password: String) : DoctorLoginUiEvent()
}