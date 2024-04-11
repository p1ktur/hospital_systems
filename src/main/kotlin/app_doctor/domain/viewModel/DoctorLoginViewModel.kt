package app_doctor.domain.viewModel

import app_doctor.data.*
import app_shared.domain.model.exceptions.*
import app_shared.domain.model.login.*
import app_shared.domain.model.transactor.*
import app_shared.domain.uiEvent.*
import app_shared.domain.uiState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class DoctorLoginViewModel(private val doctorLoginRepository: DoctorLoginRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: LoginUiEvent) {
        when (event) {
            LoginUiEvent.Login -> login()

            is LoginUiEvent.UpdateLogin -> updateLogin(event.login)
            is LoginUiEvent.UpdatePassword -> updatePassword(event.password)
        }
    }

    private fun login() {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )

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

                val loginResult = doctorLoginRepository.login(
                    login = uiState.value.login,
                    password = uiState.value.password,
                )

                when (loginResult) {
                    is TransactorResult.Failure -> {
                        if (loginResult.exception is WrongCredentialsException) {
                            errorCodes.add(loginResult.exception.code)
                            _uiState.value = uiState.value.copy(
                                errorCodes = errorCodes
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

    private fun updatePassword(password: String) = _uiState.update { it.copy(password = password) }

    private fun updateLogin(login: String) = _uiState.update { it.copy(login = login) }
}