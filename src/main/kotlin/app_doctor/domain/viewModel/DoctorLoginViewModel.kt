package app_doctor.domain.viewModel

import app_doctor.data.*
import app_doctor.domain.uiEvent.*
import app_doctor.domain.uiState.*
import app_shared.domain.model.exceptions.*
import app_shared.domain.model.transactor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class DoctorLoginViewModel(private val doctorLoginRegistrationRepository: DoctorLoginRegistrationRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<DoctorLoginUiState> = MutableStateFlow(DoctorLoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: DoctorLoginUiEvent) {
        when (event) {
            DoctorLoginUiEvent.Login -> login()

            is DoctorLoginUiEvent.UpdateLogin -> updateLogin(event.login)
            is DoctorLoginUiEvent.UpdatePassword -> updatePassword(event.password)
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

                val loginResult = doctorLoginRegistrationRepository.login(
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