package app.domain.viewModel.shared

import app.data.shared.*
import app.domain.database.transactor.*
import app.domain.model.doctor.*
import app.domain.model.shared.appointment.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import app.domain.util.args.*
import app.domain.util.exceptions.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*
import java.time.*

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
            is AppointmentsUiEvent.RequestApprovalForAppointment -> requestApprovalForAppointment(event.userWorkerId, event.userClientId, event.localDateTime)
            is AppointmentsUiEvent.ApproveAppointment -> approveAppointment(event.userWorkerId, event.appointmentId)
            is AppointmentsUiEvent.PayForAppointment -> payForAppointment(event.userClientId, event.appointmentResultId, event.payedAmount, event.payedAccount)
            is AppointmentsUiEvent.DenyRequestedAppointment -> denyRequestedAppointment(event.userWorkerId, event.appointmentId)
            is AppointmentsUiEvent.DeleteRequestedAppointment -> deleteRequestedAppointment(event.userClientId, event.appointmentId)
            is AppointmentsUiEvent.DeleteAppointment -> deleteAppointment(event.userWorkerId, event.appointmentId)
            is AppointmentsUiEvent.DeleteAppointmentWithResult -> deleteAppointmentWithResult(event.userWorkerId, event.appointmentId, event.resultId)
            is AppointmentsUiEvent.ToggleEditMode -> toggleEditMode(event.userWorkerId, event.resultId, event.price, event.notes)
            AppointmentsUiEvent.DisableEditMode -> disableEditMode()

            AppointmentsUiEvent.ShowInfoDialog -> showInfoDialog()
            AppointmentsUiEvent.HideInfoDialog -> hideInfoDialog()
            is AppointmentsUiEvent.ShowDateTimePickerDialog -> showDateTimePickerDialog(event.appArgs, event.userWorkerId, event.userClientId)
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
            _uiState.value = uiState.value.copy(
                appointments = emptyList(),
                results = emptyList(),
                payments = emptyList()
            )

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

    private fun requestApprovalForAppointment(userWorkerId: Int, userClientId: Int, localDateTime: LocalDateTime) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = appointmentsRepository.requestApprovalForAppointment(userWorkerId, userClientId, localDateTime)) {
                is TransactorResult.Failure -> {
                    val exception = result.exception as? FailedOperationException
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(exception?.code ?: 1003)
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

    private fun approveAppointment(userWorkerId: Int, appointmentId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (appointmentsRepository.approveAppointment(appointmentId)) {
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
            when (val result = appointmentsRepository.deleteAppointmentAndResult(appointmentId, resultId)) {
                is TransactorResult.Failure -> {
                    val exception = result.exception as? FailedOperationException
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        editMode = false,
                        errorCodes = listOf(exception?.code ?: 1003)
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

    private fun denyRequestedAppointment(userWorkerId: Int, appointmentId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = appointmentsRepository.deleteRequestedAppointment(appointmentId)) {
                is TransactorResult.Failure -> {
                    val exception = result.exception as? FailedOperationException
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        editMode = false,
                        errorCodes = listOf(exception?.code ?: 1003)
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

    private fun deleteRequestedAppointment(userClientId: Int, appointmentId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = appointmentsRepository.deleteRequestedAppointment(appointmentId)) {
                is TransactorResult.Failure -> {
                    val exception = result.exception as? FailedOperationException
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        editMode = false,
                        errorCodes = listOf(exception?.code ?: 1003)
                    )
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        editMode = false,
                        errorCodes = emptyList()
                    )

                    fetchAppointmentsForClient(userClientId, null)
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

    @Suppress("UNCHECKED_CAST")
    private fun showDateTimePickerDialog(appArgs: AppArgs, userWorkerId: Int, userClientId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val scheduleResult = appointmentsRepository.requestSchedule(userWorkerId)) {
                is TransactorResult.Failure -> {
                    val data = scheduleResult.exception as? FailedOperationException
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(data?.code ?: 1003)
                    )
                }
                is TransactorResult.Success<*> -> {
                    val data = scheduleResult.data as Pair<*, *>

                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = emptyList(),
                        showDateTimePickerDialog = true,
                        userWorkerIdForAppointment = userWorkerId,
                        userClientIdForAppointment = userClientId,
                        scheduleData = data.first as DoctorScheduleData,
                        busyFutureDates = data.second as List<LocalDateTime>
                    )

                    when (appArgs) {
                        AppArgs.CLIENT -> fetchAppointmentsForClient(userClientId, null)
                        AppArgs.DOCTOR -> fetchAppointmentsForDoctor(userWorkerId, null)
                        AppArgs.ADMIN -> fetchAppointmentsForAdmin(null)
                    }
                }
            }
        }
    }

    private fun hideDateTimePickerDialog() {
        _uiState.value = uiState.value.copy(
            showDateTimePickerDialog = false,
            userWorkerIdForAppointment = null,
            userClientIdForAppointment = null
        )
    }
}