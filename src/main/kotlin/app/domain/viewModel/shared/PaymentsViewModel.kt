package app.domain.viewModel.shared

import app.data.shared.*
import app.domain.database.transactor.*
import app.domain.model.shared.payment.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class PaymentsViewModel(private val paymentsRepository: PaymentsRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<PaymentsUiState> = MutableStateFlow(PaymentsUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: PaymentsUiEvent) {
        when (event) {
            PaymentsUiEvent.FetchPaymentsForDoctorOrAdmin -> fetchPaymentsForDoctorOrAdmin()
            is PaymentsUiEvent.FetchPaymentsForClient -> fetchPaymentsForClient(event.userClientId)

            is PaymentsUiEvent.StartCreatingSubPayment -> startCreatingPayment(event.userClientId)
            is PaymentsUiEvent.CreateSubPayment -> createPayment(event.subject, event.amount)
            is PaymentsUiEvent.PayForSubPayment -> payForPayment(event.userClientId, event.subPaymentId, event.payedAmount, event.payedAccount)
            is PaymentsUiEvent.DeleteSubPayment -> deletePayment(event.subPaymentId)
            is PaymentsUiEvent.ToggleEditMode -> toggleEditMode(event.subPaymentId, event.subject, event.amount)
            PaymentsUiEvent.DisableEditMode -> disableEditMode()

            PaymentsUiEvent.ShowInfoDialog -> showInfoDialog()
            PaymentsUiEvent.HideInfoDialog -> hideInfoDialog()

            is PaymentsUiEvent.UpdateDisplayMode -> updateDisplayMode(event.mode)
            is PaymentsUiEvent.UpdatePageTitle -> updatePageTitle(event.title)
        }
    }

    private fun fetchPaymentsForDoctorOrAdmin() {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchPaymentsResult = paymentsRepository.fetchPaymentsForDoctorOrAdmin()) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1001)
                    )
                }
                is TransactorResult.Success<*> -> {
                    val data = fetchPaymentsResult.data as FetchPaymentData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        payments = data.payments,
                        subPayments = data.subPayments,
                        errorCodes = emptyList()
                    )
                }
            }
        }
    }

    private fun fetchPaymentsForClient(userClientId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchPaymentsResult = paymentsRepository.fetchPaymentsForClient(userClientId)) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1001)
                    )
                }
                is TransactorResult.Success<*> -> {
                    val data = fetchPaymentsResult.data as FetchPaymentData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        payments = data.payments,
                        subPayments = data.subPayments,
                        errorCodes = emptyList()
                    )
                }
            }
        }
    }

    private fun startCreatingPayment(userClientId: Int) {
        _uiState.value = uiState.value.copy(
            creatingSubPayment = true,
            showInfoDialog = true,
            userClientIdForSubPayment = userClientId
        )
    }

    private fun createPayment(subject: String, amount: Float) {
        if (uiState.value.userClientIdForSubPayment == null) return

        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (paymentsRepository.createSubPayment(
                uiState.value.userClientIdForSubPayment!!,
                subject,
                amount
            )) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1003),
                        creatingSubPayment = false
                    )
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = emptyList(),
                        creatingSubPayment = false
                    )

                    fetchPaymentsForDoctorOrAdmin()
                }
            }
        }
    }

    private fun payForPayment(userClientId: Int, subPaymentId: Int, payedAmount: Float, payedAccount: String) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (paymentsRepository.payForSubPayment(subPaymentId, payedAmount, payedAccount)) {
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

                    fetchPaymentsForClient(userClientId)
                }
            }
        }
    }

    private fun toggleEditMode(subPaymentId: Int, subject: String, amount: Float) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value.editMode) {
                when (paymentsRepository.updateSubPayment(subPaymentId, subject, amount)) {
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

                        fetchPaymentsForDoctorOrAdmin()
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

    private fun deletePayment(subPaymentId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (paymentsRepository.deleteSubPayment(subPaymentId)) {
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

                    fetchPaymentsForDoctorOrAdmin()
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

    private fun updateDisplayMode(mode: Int) {
        _uiState.value = uiState.value.copy(
            displayMode = mode
        )
    }

    private fun updatePageTitle(title: String) {
        _uiState.value = uiState.value.copy(
            pageTitle = title
        )
    }
}