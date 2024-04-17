package app.domain.uiEvent.client

sealed class ClientRegistrationUiEvent {
    data object Register : ClientRegistrationUiEvent()
    data object ForgetRegistration : ClientRegistrationUiEvent()

    data class UpdateName(val name: String) : ClientRegistrationUiEvent()
    data class UpdateSurname(val surname: String) : ClientRegistrationUiEvent()
    data class UpdateFathersName(val fathersName: String) : ClientRegistrationUiEvent()
    data class UpdateAge(val age: String) : ClientRegistrationUiEvent()
    data class UpdateAddress(val address: String) : ClientRegistrationUiEvent()
    data class UpdatePhone(val phone: String) : ClientRegistrationUiEvent()
    data class UpdateEmail(val email: String) : ClientRegistrationUiEvent()
    data class UpdateLogin(val login: String) : ClientRegistrationUiEvent()
    data class UpdatePassword(val password: String) : ClientRegistrationUiEvent()

}