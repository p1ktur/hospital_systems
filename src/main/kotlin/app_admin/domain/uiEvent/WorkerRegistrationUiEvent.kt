package app_admin.domain.uiEvent

sealed class WorkerRegistrationUiEvent {
    data object Register : WorkerRegistrationUiEvent()

    data class UpdateName(val name: String) : WorkerRegistrationUiEvent()
    data class UpdateSurname(val surname: String) : WorkerRegistrationUiEvent()
    data class UpdateFathersName(val fathersName: String) : WorkerRegistrationUiEvent()
    data class UpdateAge(val age: String) : WorkerRegistrationUiEvent()
    data class UpdateAddress(val address: String) : WorkerRegistrationUiEvent()
    data class UpdatePhone(val phone: String) : WorkerRegistrationUiEvent()
    data class UpdateEmail(val email: String) : WorkerRegistrationUiEvent()
    data class UpdatePosition(val position: String) : WorkerRegistrationUiEvent()
    data class UpdateSalary(val salary: String) : WorkerRegistrationUiEvent()
    data class UpdateLogin(val login: String) : WorkerRegistrationUiEvent()
    data class UpdatePassword(val password: String) : WorkerRegistrationUiEvent()
}