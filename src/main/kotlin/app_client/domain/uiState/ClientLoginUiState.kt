package app_client.domain.uiState

import app_client.domain.model.*

data class ClientLoginUiState(
    val login: String = "",
    val password: String = "",
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val clientLoginStatus: ClientLoginStatus = ClientLoginStatus.LoggedOut
)