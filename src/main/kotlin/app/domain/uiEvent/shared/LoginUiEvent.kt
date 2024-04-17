package app.domain.uiEvent.shared

sealed class LoginUiEvent {
    data object LogIn : LoginUiEvent()
    data object LogOut : LoginUiEvent()

    data class UpdateLogin(val login: String) : LoginUiEvent()
    data class UpdatePassword(val password: String) : LoginUiEvent()
}