package app.domain.viewModel.doctor

import app.data.doctor.*
import app.domain.database.transactor.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import app.domain.util.exceptions.*
import app.domain.util.login.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class DoctorLoginViewModel(private val doctorLoginRegistrationRepository: DoctorLoginRegistrationRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: LoginUiEvent) {
        when (event) {
            LoginUiEvent.LogIn -> login()
            LoginUiEvent.LogOut -> logout()

            is LoginUiEvent.UpdateLogin -> updateLogin(event.login)
            is LoginUiEvent.UpdatePassword -> updatePassword(event.password)
        }
    }

    private fun login() {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            val errorCodes = mutableListOf<Int>()

            if (uiState.value.login.isEmpty()) {
                errorCodes.add(1009)
            }

            if (uiState.value.password.isEmpty()) {
                errorCodes.add(1010)
            }

            _uiState.value = uiState.value.copy(
                errorCodes = errorCodes,
                isLoading = false
            )

            if (errorCodes.isEmpty()) {
                _uiState.value = uiState.value.copy(
                    errorCodes = errorCodes
                )

                val loginResult = doctorLoginRegistrationRepository.login(
                    login = uiState.value.login,
                    password = uiState.value.password,
                )

                when (loginResult) {
                    is TransactorResult.Failure -> {
                        if (loginResult.exception is WrongCredentialsException) {
                            _uiState.value = uiState.value.copy(
                                errorCodes = listOf(loginResult.exception.code)
                            )
                        }
                        _uiState.value = uiState.value.copy(
                            isLoading = false
                        )
                    }
                    is TransactorResult.Success<*> -> {
                        _uiState.value = uiState.value.copy(
                            loginStatus = LoginStatus.LoggedIn(loginResult.data as Int),
                            isLoading = false
                        )
                    }
                }
            } else {
                _uiState.value = uiState.value.copy(
                    errorCodes = errorCodes,
                    isLoading = false
                )
            }
        }
    }

    private fun logout() {
        if (uiState.value.isLoading) return

        _uiState.value = LoginUiState()
    }

    private fun updatePassword(password: String) = _uiState.update { it.copy(password = password) }

    private fun updateLogin(login: String) = _uiState.update { it.copy(login = login) }
}