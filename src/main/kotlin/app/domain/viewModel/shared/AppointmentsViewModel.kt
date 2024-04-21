package app.domain.viewModel.shared

import app.data.shared.*
import app.domain.database.transactor.*
import app.domain.model.shared.appointment.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class AppointmentsViewModel(private val appointmentsRepository: AppointmentsRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<AppointmentsUiState> = MutableStateFlow(AppointmentsUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: AppointmentsUiEvent) {
        when (event) {
            is AppointmentsUiEvent.FetchAppointmentsForAdmin -> fetchAppointmentsForAdmin(event.openId)
            is AppointmentsUiEvent.FetchAppointmentsForDoctor -> fetchAppointmentsForDoctor(event.userWorkerId, event.openId)
            is AppointmentsUiEvent.FetchAppointmentsForClient -> fetchAppointmentsForClient(event.userClientId, event.openId)

            is AppointmentsUiEvent.CreateAppointment -> createAppointment(event.selfUserWorkerId, event.userWorkerId, event.userClientId, event.localDateTime)
            is AppointmentsUiEvent.CreateAppointmentResult -> createAppointmentResult(event.userWorkerId, event.appointmentId, event.price, event.notes)
            is AppointmentsUiEvent.PayForAppointment -> payForAppointment(event.userClientId, event.appointmentResultId, event.payedAmount, event.payedAccount)
            is AppointmentsUiEvent.DeleteAppointment -> deleteAppointment(event.userWorkerId, event.appointmentId)
            is AppointmentsUiEvent.DeleteAppointmentWithResult -> deleteAppointmentWithResult(event.userWorkerId, event.appointmentId, event.resultId)
            is AppointmentsUiEvent.ToggleEditMode -> toggleEditMode(event.userWorkerId, event.resultId, event.price, event.notes)
            AppointmentsUiEvent.DisableEditMode -> disableEditMode()

            AppointmentsUiEvent.ShowInfoDialog -> showInfoDialog()
            AppointmentsUiEvent.HideInfoDialog -> hideInfoDialog()
            is AppointmentsUiEvent.ShowDateTimePickerDialog -> showDateTimePickerDialog(event.userWorkerId, event.userClientId)
            AppointmentsUiEvent.HideDateTimePickerDialog -> hideDateTimePickerDialog()
        }
    }

    private fun fetchAppointmentsForAdmin(openId: Int?) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchAppointmentsResult = appointmentsRepository.fetchAppointmentsForAdmin()) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1001)
                    )
                }
                is TransactorResult.Success<*> -> {
                    val data = fetchAppointmentsResult.data as FetchAppointmentData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        appointments = data.appointments,
                        results = data.appointmentResults,
                        payments = data.payments,
                        errorCodes = emptyList(),
                        openId = openId
                    )
                }
            }
        }
    }

    private fun fetchAppointmentsForDoctor(userWorkerId: Int, openId: Int?) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchAppointmentsResult = appointmentsRepository.fetchAppointmentsForDoctor(userWorkerId)) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1001)
                    )
                }
                is TransactorResult.Success<*> -> {
                    val data = fetchAppointmentsResult.data as FetchAppointmentData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        appointments = data.appointments,
                        results = data.appointmentResults,
                        payments = data.payments,
                        errorCodes = emptyList(),
                        openId = openId
                    )
                }
            }
        }
    }

    private fun fetchAppointmentsForClient(userClientId: Int, openId: Int?) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchAppointmentsResult = appointmentsRepository.fetchAppointmentsForClient(userClientId)) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1001)
                    )
                }
                is TransactorResult.Success<*> -> {
                    val data = fetchAppointmentsResult.data as FetchAppointmentData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        appointments = data.appointments,
                        results = data.appointmentResults,
                        payments = data.payments,
                        errorCodes = emptyList(),
                        openId = openId
                    )
                }
            }
        }
    }

    private fun createAppointment(selfUserWorkerId: Int, userWorkerId: Int, userClientId: Int, localDateTime: LocalDateTime) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (appointmentsRepository.createAppointment(userWorkerId, userClientId, localDateTime)) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1003)
                    )
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = emptyList()
                    )

                    if (selfUserWorkerId == userWorkerId) fetchAppointmentsForDoctor(selfUserWorkerId, null)
                }
            }
        }
    }

    private fun createAppointmentResult(userWorkerId: Int, appointmentId: Int, price: Float, notes: String) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (appointmentsRepository.createAppointmentResult(appointmentId, price, notes)) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1003)
                    )
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = emptyList()
                    )

                    fetchAppointmentsForDoctor(userWorkerId, null)
                }
            }
        }
    }

    private fun payForAppointment(userClientId: Int, appointmentResultId: Int, payedAmount: Float, payedAccount: String) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (appointmentsRepository.payForAppointment(appointmentResultId, payedAmount, payedAccount)) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1003)
                    )
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = emptyList()
                    )

                    fetchAppointmentsForClient(userClientId, null)
                }
            }
        }
    }

    private fun toggleEditMode(userWorkerId: Int, resultId: Int, price: Float, notes: String) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value.editMode) {
                when (appointmentsRepository.updateAppointmentResult(resultId, price, notes)) {
                    is TransactorResult.Failure -> {
                        _uiState.value = uiState.value.copy(
                            isLoading = false,
                            editMode = false,
                            errorCodes = listOf(1003)
                        )
                    }
                    is TransactorResult.Success<*> -> {
                        _uiState.value = uiState.value.copy(
                            isLoading = false,
                            editMode = false,
                            errorCodes = emptyList()
                        )

                        fetchAppointmentsForDoctor(userWorkerId, null)
                    }
                }
            } else {
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    editMode = true
                )
            }
        }
    }

    private fun deleteAppointmentWithResult(userWorkerId: Int, appointmentId: Int, resultId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (appointmentsRepository.deleteAppointmentAndResult(appointmentId, resultId)) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        editMode = false,
                        errorCodes = listOf(1003)
                    )
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        editMode = false,
                        errorCodes = emptyList()
                    )

                    fetchAppointmentsForDoctor(userWorkerId, null)
                }
            }
        }
    }

    private fun deleteAppointment(userWorkerId: Int, appointmentId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (appointmentsRepository.deleteAppointment(appointmentId)) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        editMode = false,
                        errorCodes = listOf(1003)
                    )
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        editMode = false,
                        errorCodes = emptyList()
                    )

                    fetchAppointmentsForDoctor(userWorkerId, null)
                }
            }
        }
    }

    private fun disableEditMode() {
        _uiState.value = uiState.value.copy(
            editMode = false
        )
    }

    private fun showInfoDialog() {
        _uiState.value = uiState.value.copy(
            showInfoDialog = true
        )
    }

    private fun hideInfoDialog() {
        _uiState.value = uiState.value.copy(
            showInfoDialog = false
        )
    }

    private fun showDateTimePickerDialog(userWorkerId: Int, userClientId: Int) {
        _uiState.value = uiState.value.copy(
            showDateTimePickerDialog = true,
            userWorkerIdForAppointment = userWorkerId,
            userClientIdForAppointment = userClientId
        )
    }

    private fun hideDateTimePickerDialog() {
        _uiState.value = uiState.value.copy(
            showDateTimePickerDialog = false,
            userWorkerIdForAppointment = null,
            userClientIdForAppointment = null
        )
    }
}