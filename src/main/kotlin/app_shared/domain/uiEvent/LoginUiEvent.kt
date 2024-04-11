package app_shared.domain.uiEvent

sealed class LoginUiEvent {
    data object Login : LoginUiEvent()

    data class UpdateLogin(val login: String) : LoginUiEvent()
    data class UpdatePassword(val password: String) : LoginUiEvent()
}