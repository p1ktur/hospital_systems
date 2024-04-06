package app_client.domain.viewModel

import app_client.data.*
import app_client.domain.uiEvent.*
import app_client.domain.uiState.*
import app_shared.domain.model.regex.*
import app_shared.domain.model.transactor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*
import java.util.regex.*

class ClientRegistrationViewModel(private val clientInfoRepository: ClientInfoRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<ClientRegistrationUiState> = MutableStateFlow(ClientRegistrationUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: ClientRegistrationUiEvent) {
        when (event) {
            ClientRegistrationUiEvent.Register -> register()

            is ClientRegistrationUiEvent.UpdateAddress -> updateAddress(event.address)
            is ClientRegistrationUiEvent.UpdateAge -> updateAge(event.age)
            is ClientRegistrationUiEvent.UpdateEmail -> updateEmail(event.email)
            is ClientRegistrationUiEvent.UpdateFathersName -> updateFathersName(event.fathersName)
            is ClientRegistrationUiEvent.UpdateLogin -> updateLogin(event.login)
            is ClientRegistrationUiEvent.UpdateName -> updateName(event.name)
            is ClientRegistrationUiEvent.UpdatePassword -> updatePassword(event.password)
            is ClientRegistrationUiEvent.UpdatePhone -> updatePhone(event.phone)
            is ClientRegistrationUiEvent.UpdateSurname -> updateSurname(event.surname)
        }
    }

    // Must fields - name, surname, age, address, phone, login, password
    // Not must fields - father's name, email

    private fun register() {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )

            val errorCodes = mutableListOf<Int>()

            //1012 -> "This login is occupied already."

            if (uiState.value.name.isEmpty()) {
                errorCodes.add(1001)
            }

            if (uiState.value.surname.isEmpty()) {
                errorCodes.add(1002)
            }

            if (uiState.value.age.isEmpty()) {
                errorCodes.add(1003)
            } else {
                try {
                    if (uiState.value.age.toInt() <= 0) {
                        errorCodes.add(1004)
                    }
                } catch (_: Exception) {
                    errorCodes.add(1004)
                }
            }

            if (uiState.value.address.isEmpty()) {
                errorCodes.add(1005)
            }

            if (uiState.value.phone.isEmpty()) {
                errorCodes.add(1006)
            } else if (!phoneNumberPattern.matches(uiState.value.phone)) {
                errorCodes.add(1007)
            }

            if (uiState.value.email.isNotEmpty() && !emailPattern.matches(uiState.value.email)) {
                errorCodes.add(1008)
            }

            if (uiState.value.login.isEmpty()) {
                errorCodes.add(1009)
            }

            if (uiState.value.password.isEmpty()) {
                errorCodes.add(1010)
            } else if (uiState.value.password.length < 8) {
                errorCodes.add(1011)
            }

            _uiState.value = uiState.value.copy(
                errorCodes = errorCodes,
                isLoading = false
            )

            if (errorCodes.isEmpty()) {
                _uiState.value = uiState.value.copy(
                    errorCodes = errorCodes
                )

                val registerResult = clientInfoRepository.register(
                    name = uiState.value.name,
                    surname = uiState.value.surname,
                    fathersName = uiState.value.fathersName,
                    age = uiState.value.age,
                    address = uiState.value.address,
                    phone = uiState.value.phone,
                    email = uiState.value.email,
                    login = uiState.value.login,
                    password = uiState.value.password,
                )

                when (registerResult) {
                    is TransactorResult.Failure -> println("Unable to register because: ${registerResult.exception?.message}")
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

    private fun updateSurname(surname: String) {
        _uiState.update { it.copy(surname = surname) }
    }

    private fun updatePhone(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    private fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    private fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    private fun updateLogin(login: String) {
        _uiState.update { it.copy(login = login) }
    }

    private fun updateFathersName(fathersName: String) {
        _uiState.update { it.copy(fathersName = fathersName) }
    }

    private fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    private fun updateAge(age: String) {
        _uiState.update { it.copy(age = age) }
    }

    private fun updateAddress(address: String) {
        _uiState.update { it.copy(address = address) }
    }
}