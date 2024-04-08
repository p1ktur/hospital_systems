package app_client.domain.model

sealed class ClientLoginStatus {
    data class LoggedIn(val userClientId: Int) : ClientLoginStatus()
    data object LoggedOut : ClientLoginStatus()
}