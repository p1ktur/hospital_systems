package app.domain.uiEvent.client

sealed class ClientInfoUiEvent {
    data class FetchInfo(val userClientId: Int) : ClientInfoUiEvent()
    data class SaveChanges(val userClientId: Int) : ClientInfoUiEvent()
    data object ToggleEditMode : ClientInfoUiEvent()

    data class UpdateName(val name: String) : ClientInfoUiEvent()
    data class UpdateSurname(val surname: String) : ClientInfoUiEvent()
    data class UpdateFathersName(val fathersName: String) : ClientInfoUiEvent()
    data class UpdateAge(val age: String) : ClientInfoUiEvent()
    data class UpdateAddress(val address: String) : ClientInfoUiEvent()
    data class UpdatePhone(val phone: String) : ClientInfoUiEvent()
    data class UpdateEmail(val email: String) : ClientInfoUiEvent()
}