package app_doctor.domain.model

sealed class DoctorLoginStatus {
    data class LoggedIn(val userDoctorId: Int) : DoctorLoginStatus()
    data object LoggedOut : DoctorLoginStatus()
}