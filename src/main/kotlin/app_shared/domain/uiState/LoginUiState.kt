package app_shared.domain.uiState

import app_shared.domain.model.util.login.*

data class LoginUiState(
    val login: String = "",
    val password: String = "",
    val errorCodes: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val loginStatus: LoginStatus = LoginStatus.LoggedOut
)