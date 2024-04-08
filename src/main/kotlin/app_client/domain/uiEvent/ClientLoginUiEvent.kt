package app_client.domain.uiEvent

sealed class ClientLoginUiEvent {
    data object Login : ClientLoginUiEvent()

    data class UpdateLogin(val login: String) : ClientLoginUiEvent()
    data class UpdatePassword(val password: String) : ClientLoginUiEvent()
}