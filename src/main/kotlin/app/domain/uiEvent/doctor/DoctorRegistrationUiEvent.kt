package app.domain.uiEvent.doctor

sealed class DoctorRegistrationUiEvent {
    data object Register : DoctorRegistrationUiEvent()
    data object ForgetRegistration : DoctorRegistrationUiEvent()

    data class UpdateName(val name: String) : DoctorRegistrationUiEvent()
    data class UpdateSurname(val surname: String) : DoctorRegistrationUiEvent()
    data class UpdateFathersName(val fathersName: String) : DoctorRegistrationUiEvent()
    data class UpdateAge(val age: String) : DoctorRegistrationUiEvent()
    data class UpdateAddress(val address: String) : DoctorRegistrationUiEvent()
    data class UpdatePhone(val phone: String) : DoctorRegistrationUiEvent()
    data class UpdateEmail(val email: String) : DoctorRegistrationUiEvent()
    data class UpdatePosition(val position: String) : DoctorRegistrationUiEvent()
    data class UpdateSalary(val salary: String) : DoctorRegistrationUiEvent()
    data class UpdateCanReceiveAppointments(val canReceiveAppointments: Boolean) : DoctorRegistrationUiEvent()
    data class UpdateLogin(val login: String) : DoctorRegistrationUiEvent()
    data class UpdatePassword(val password: String) : DoctorRegistrationUiEvent()
}