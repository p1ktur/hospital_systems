package app.domain.viewModel.doctor

import app.data.doctor.*
import app.domain.database.transactor.*
import app.domain.uiEvent.doctor.*
import app.domain.uiState.doctor.*
import app.domain.util.exceptions.*
import app.domain.util.regex.*
import app.domain.util.result.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class DoctorRegistrationViewModel(private val doctorLoginRegistrationRepository: DoctorLoginRegistrationRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<DoctorRegistrationUiState> = MutableStateFlow(DoctorRegistrationUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: DoctorRegistrationUiEvent) {
        when (event) {
            DoctorRegistrationUiEvent.Register -> register()
            DoctorRegistrationUiEvent.ForgetRegistration -> forgetRegistration()

            is DoctorRegistrationUiEvent.UpdateAddress -> updateAddress(event.address)
            is DoctorRegistrationUiEvent.UpdateAge -> updateAge(event.age)
            is DoctorRegistrationUiEvent.UpdateEmail -> updateEmail(event.email)
            is DoctorRegistrationUiEvent.UpdateFathersName -> updateFathersName(event.fathersName)
            is DoctorRegistrationUiEvent.UpdateLogin -> updateLogin(event.login)
            is DoctorRegistrationUiEvent.UpdateName -> updateName(event.name)
            is DoctorRegistrationUiEvent.UpdatePassword -> updatePassword(event.password)
            is DoctorRegistrationUiEvent.UpdatePhone -> updatePhone(event.phone)
            is DoctorRegistrationUiEvent.UpdateSurname -> updateSurname(event.surname)
            is DoctorRegistrationUiEvent.UpdatePosition -> updatePosition(event.position)
            is DoctorRegistrationUiEvent.UpdateSalary -> updateSalary(event.salary)
            is DoctorRegistrationUiEvent.UpdateCanReceiveAppointments -> updateCanReceiveAppointments(event.canReceiveAppointments)
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

                val registerResult = doctorLoginRegistrationRepository.register(
                    name = uiState.value.name,
                    surname = uiState.value.surname,
                    fathersName = uiState.value.fathersName,
                    age = uiState.value.age,
                    address = uiState.value.address,
                    phone = uiState.value.phone,
                    email = uiState.value.email,
                    position = uiState.value.position,
                    salary = uiState.value.salary,
                    canReceiveAppointments = uiState.value.canReceiveAppointments,
                    login = uiState.value.login,
                    password = uiState.value.password
                )

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
                        val userWorkerId = registerResult.data as Int
                        _uiState.value = uiState.value.copy(
                            isLoading = false,
                            registrationResult = TaskResult.Success(userWorkerId)
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

    private fun forgetRegistration() {
        _uiState.value = uiState.value.copy(
            registrationResult = TaskResult.NotCompleted
        )
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

    private fun updateCanReceiveAppointments(canReceiveAppointments: Boolean) = _uiState.update { it.copy(canReceiveAppointments = canReceiveAppointments) }
}