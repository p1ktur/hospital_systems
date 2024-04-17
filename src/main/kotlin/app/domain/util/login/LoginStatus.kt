package app.domain.util.login

sealed class LoginStatus {
    data class LoggedIn(val userId: Int) : LoginStatus()
    data object LoggedOut : LoginStatus()
}