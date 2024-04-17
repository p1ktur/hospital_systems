package app.domain.viewModel.shared

import app.data.shared.*
import app.domain.database.transactor.*
import app.domain.model.shared.hospitalization.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class HospitalizationsViewModel(
    private val hospitalizationsRepository: HospitalizationsRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<HospitalizationsUiState> = MutableStateFlow(HospitalizationsUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: HospitalizationsUiEvent) {
        when (event) {
            HospitalizationsUiEvent.FetchHospitalizationsForDoctorOrAdmin -> fetchHospitalizationsForDoctorOrAdmin()
            is HospitalizationsUiEvent.FetchHospitalizationsForClient -> fetchHospitalizationsForClient(event.userClientId)

            is HospitalizationsUiEvent.StartCreatingHospitalization -> startCreatingHospitalization(event.userClientId, event.roomId)
            is HospitalizationsUiEvent.CreateHospitalization -> createHospitalization(event.reason, event.price)
            is HospitalizationsUiEvent.PayForHospitalization -> payForHospitalization(event.userClientId, event.hospitalizationId, event.payedAmount, event.payedAccount)
            is HospitalizationsUiEvent.EndHospitalization -> endHospitalization(event.hospitalizationId)
            is HospitalizationsUiEvent.DeleteHospitalization -> deleteHospitalization(event.hospitalizationId)
            is HospitalizationsUiEvent.ToggleEditMode -> toggleEditMode(event.hospitalizationId, event.reason, event.price)
            HospitalizationsUiEvent.DisableEditMode -> disableEditMode()

            HospitalizationsUiEvent.ShowInfoDialog -> showInfoDialog()
            HospitalizationsUiEvent.HideInfoDialog -> hideInfoDialog()
        }
    }

    private fun fetchHospitalizationsForDoctorOrAdmin() {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchHospitalizationsResult = hospitalizationsRepository.fetchHospitalizationsForDoctorOrAdmin()) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1001)
                    )
                }
                is TransactorResult.Success<*> -> {
                    val data = fetchHospitalizationsResult.data as FetchHospitalizationData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        hospitalizations = data.hospitalizations,
                        payments = data.payments,
                        errorCodes = emptyList()
                    )
                }
            }
        }
    }

    private fun fetchHospitalizationsForClient(userClientId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchHospitalizationsResult = hospitalizationsRepository.fetchHospitalizationsForClient(userClientId)) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1001)
                    )
                }
                is TransactorResult.Success<*> -> {
                    val data = fetchHospitalizationsResult.data as FetchHospitalizationData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        hospitalizations = data.hospitalizations,
                        payments = data.payments,
                        errorCodes = emptyList()
                    )
                }
            }
        }
    }

    private fun startCreatingHospitalization(userClientId: Int, roomId: Int) {
        _uiState.value = uiState.value.copy(
            creatingHospitalization = true,
            showInfoDialog = true,
            userClientIdForHospitalization = userClientId,
            roomIdForHospitalization = roomId
        )
    }

    private fun createHospitalization(reason: String, price: Float) {
        if (uiState.value.userClientIdForHospitalization == null || uiState.value.roomIdForHospitalization == null) return

        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (hospitalizationsRepository.createHospitalization(
                uiState.value.userClientIdForHospitalization!!,
                uiState.value.roomIdForHospitalization!!,
                reason,
                price
            )) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1003),
                        creatingHospitalization = false
                    )
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = emptyList(),
                        creatingHospitalization = false
                    )

                    fetchHospitalizationsForDoctorOrAdmin()
                }
            }
        }
    }

    private fun payForHospitalization(userClientId: Int, hospitalizationId: Int, payedAmount: Float, payedAccount: String) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (hospitalizationsRepository.payForHospitalization(hospitalizationId, payedAmount, payedAccount)) {
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

                    fetchHospitalizationsForClient(userClientId)
                }
            }
        }
    }

    private fun toggleEditMode(hospitalizationId: Int, reason: String, price: Float) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value.editMode) {
                when (hospitalizationsRepository.updateHospitalization(hospitalizationId, reason, price)) {
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

                        fetchHospitalizationsForDoctorOrAdmin()
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

    private fun endHospitalization(hospitalizationId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (hospitalizationsRepository.endHospitalization(hospitalizationId)) {
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

                    fetchHospitalizationsForDoctorOrAdmin()
                }
            }
        }
    }

    private fun deleteHospitalization(hospitalizationId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (hospitalizationsRepository.deleteHospitalization(hospitalizationId)) {
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

                    fetchHospitalizationsForDoctorOrAdmin()
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
}