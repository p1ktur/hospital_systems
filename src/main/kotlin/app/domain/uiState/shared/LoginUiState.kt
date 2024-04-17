package app.domain.uiState.shared

import app.domain.util.login.*

data class LoginUiState(
    val login: String = "",
    val password: String = "",
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val loginStatus: LoginStatus = LoginStatus.LoggedOut
)