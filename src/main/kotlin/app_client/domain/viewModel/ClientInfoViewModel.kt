package app_client.domain.viewModel

import app_client.data.*
import app_client.domain.model.*
import app_client.domain.uiEvent.*
import app_client.domain.uiState.*
import app_shared.domain.model.transactor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class ClientInfoViewModel(private val clientInfoRepository: ClientInfoRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<ClientInfoUiState> = MutableStateFlow(ClientInfoUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: ClientInfoUiEvent) {
        when (event) {
            is ClientInfoUiEvent.FetchInfo -> fetchInfo(event.userClientId)
        }
    }

    private fun fetchInfo(userClientId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchInfoResult = clientInfoRepository.fetchInfo(userClientId)) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    val data = fetchInfoResult.data as ClientInfoData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        name = data.name,
                        surname = data.surname,
                        fathersName = data.fathersName,
                        age = data.age,
                        address = data.address,
                        phone = data.phone,
                        email = data.email,
                        registrationDate = data.registrationDate,
                        pendingAppointments = data.pendingAppointments,
                        visitedAppointments = data.visitedAppointments,
                        isHospitalized = data.isHospitalized,
                        pendingPayments = data.pendingPayments,
                        payedPayments = data.payedPayments
                    )
                }
            }
        }
    }
}