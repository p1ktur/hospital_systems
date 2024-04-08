package app_admin.domain.viewModel

import app_admin.data.*
import app_admin.domain.uiEvent.*
import app_admin.domain.uiState.*
import app_shared.domain.model.exceptions.*
import app_shared.domain.model.regex.*
import app_shared.domain.model.transactor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class WorkerRegistrationViewModel(private val workerRegistrationRepository: WorkerRegistrationRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<WorkerRegistrationUiState> = MutableStateFlow(WorkerRegistrationUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: WorkerRegistrationUiEvent) {
        when (event) {
            WorkerRegistrationUiEvent.Register -> register()

            is WorkerRegistrationUiEvent.UpdateAddress -> updateAddress(event.address)
            is WorkerRegistrationUiEvent.UpdateAge -> updateAge(event.age)
            is WorkerRegistrationUiEvent.UpdateEmail -> updateEmail(event.email)
            is WorkerRegistrationUiEvent.UpdateFathersName -> updateFathersName(event.fathersName)
            is WorkerRegistrationUiEvent.UpdateLogin -> updateLogin(event.login)
            is WorkerRegistrationUiEvent.UpdateName -> updateName(event.name)
            is WorkerRegistrationUiEvent.UpdatePassword -> updatePassword(event.password)
            is WorkerRegistrationUiEvent.UpdatePhone -> updatePhone(event.phone)
            is WorkerRegistrationUiEvent.UpdateSurname -> updateSurname(event.surname)
            is WorkerRegistrationUiEvent.UpdatePosition -> updatePosition(event.position)
            is WorkerRegistrationUiEvent.UpdateSalary -> updateSalary(event.salary)
        }
    }

    private fun register() {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )

            val errorCodes = mutableListOf<Int>()

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

            if (uiState.value.position.isEmpty()) {
                errorCodes.add(1015)
            }

            if (uiState.value.salary.isNotEmpty()) {
                try {
                    if (uiState.value.salary.toDouble() <= 0.0) {
                        errorCodes.add(1014)
                    }
                } catch (_: Exception) {
                    errorCodes.add(1014)
                }
            }

            if (uiState.value.password.isNotEmpty() && uiState.value.password.length < 8) {
                errorCodes.add(1011)
            }

            if (uiState.value.password.isEmpty() && uiState.value.login.isNotEmpty()) {
                errorCodes.add(1017)
            } else if (uiState.value.password.isNotEmpty() && uiState.value.login.isEmpty()) {
                errorCodes.add(1016)
            }

            _uiState.value = uiState.value.copy(
                errorCodes = errorCodes,
                isLoading = false
            )

            if (errorCodes.isEmpty()) {
                _uiState.value = uiState.value.copy(
                    errorCodes = errorCodes
                )

                val registerResult = if (uiState.value.login.isEmpty() || uiState.value.password.isEmpty()) {
                    workerRegistrationRepository.register(
                        name = uiState.value.name,
                        surname = uiState.value.surname,
                        fathersName = uiState.value.fathersName,
                        age = uiState.value.age,
                        address = uiState.value.address,
                        phone = uiState.value.phone,
                        email = uiState.value.email,
                        position = uiState.value.position,
                        salary = uiState.value.salary
                    )
                } else {
                    workerRegistrationRepository.register(
                        name = uiState.value.name,
                        surname = uiState.value.surname,
                        fathersName = uiState.value.fathersName,
                        age = uiState.value.age,
                        address = uiState.value.address,
                        phone = uiState.value.phone,
                        email = uiState.value.email,
                        position = uiState.value.position,
                        salary = uiState.value.salary,
                        login = uiState.value.login,
                        password = uiState.value.password
                    )
                }

                when (registerResult) {
                    is TransactorResult.Failure -> {
                        if (registerResult.exception is AlreadyExistsException) {
                            errorCodes.add(registerResult.exception.code)
                            _uiState.value = uiState.value.copy(
                                errorCodes = errorCodes
                            )
                        }

                        _uiState.value = uiState.value.copy(
                            isLoading = false
                        )
                        println("Unable to register because: ${registerResult.exception?.message}")
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

    private fun updateSurname(surname: String) = _uiState.update { it.copy(surname = surname) }

    private fun updatePhone(phone: String) = _uiState.update { it.copy(phone = phone) }

    private fun updatePassword(password: String) = _uiState.update { it.copy(password = password) }

    private fun updateName(name: String) = _uiState.update { it.copy(name = name) }

    private fun updateLogin(login: String) = _uiState.update { it.copy(login = login) }

    private fun updateFathersName(fathersName: String) = _uiState.update { it.copy(fathersName = fathersName) }

    private fun updateEmail(email: String) = _uiState.update { it.copy(email = email) }

    private fun updateAge(age: String) = _uiState.update { it.copy(age = age) }

    private fun updateAddress(address: String) = _uiState.update { it.copy(address = address) }

    private fun updatePosition(position: String) = _uiState.update { it.copy(position = position) }

    private fun updateSalary(salary: String) = _uiState.update { it.copy(salary = salary) }
}