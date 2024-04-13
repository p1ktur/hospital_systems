package app_client.domain.viewModel

import app_client.data.*
import app_client.domain.model.*
import app_client.domain.uiEvent.*
import app_client.domain.uiState.*
import app_shared.domain.model.database.transactor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class ClientInfoViewModel(private val clientInfoRepository: ClientInfoRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<ClientInfoUiState> = MutableStateFlow(ClientInfoUiState())
    val uiState = _uiState.asStateFlow()

    private var beforeEditingUiStateCopy: ClientInfoUiState? = null

    fun onUiEvent(event: ClientInfoUiEvent) {
        when (event) {
            is ClientInfoUiEvent.FetchInfo -> fetchInfo(event.userClientId)
            is ClientInfoUiEvent.SaveChanges -> saveChanges(event.userClientId)
            ClientInfoUiEvent.ToggleEditMode -> toggleEditMode()

            is ClientInfoUiEvent.UpdateAddress -> updateAddress(event.address)
            is ClientInfoUiEvent.UpdateAge -> updateAge(event.age)
            is ClientInfoUiEvent.UpdateEmail -> updateEmail(event.email)
            is ClientInfoUiEvent.UpdateFathersName -> updateFathersName(event.fathersName)
            is ClientInfoUiEvent.UpdateName -> updateName(event.name)
            is ClientInfoUiEvent.UpdatePhone -> updatePhone(event.phone)
            is ClientInfoUiEvent.UpdateSurname -> updateSurname(event.surname)
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
                        age = data.age.toString(),
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

    private fun saveChanges(userClientId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        val saveChangesResult = clientInfoRepository.saveChanges(
            userClientId = userClientId,
            name = uiState.value.name,
            surname = uiState.value.surname,
            fathersName = uiState.value.fathersName,
            age = uiState.value.age,
            address = uiState.value.address,
            phone = uiState.value.phone,
            email = uiState.value.email,
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (saveChangesResult) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        editMode = false,
                        isLoading = false,
                        errorCodes = listOf(1003)
                    )
                }
                is TransactorResult.Success<*> -> {
                    _uiState.value = uiState.value.copy(
                        editMode = false,
                        isLoading = false,
                        errorCodes = emptyList()
                    )
                }
            }
        }
    }

    private fun toggleEditMode() {
        if (!uiState.value.editMode) beforeEditingUiStateCopy = uiState.value

        _uiState.value = uiState.value.copy(
            editMode = !uiState.value.editMode
        )

        if (!uiState.value.editMode) beforeEditingUiStateCopy?.let {
            _uiState.value = it
        }
    }

    private fun updateSurname(surname: String) = _uiState.update { it.copy(surname = surname) }

    private fun updatePhone(phone: String) = _uiState.update { it.copy(phone = phone) }

    private fun updateName(name: String) = _uiState.update { it.copy(name = name) }

    private fun updateFathersName(fathersName: String) = _uiState.update { it.copy(fathersName = fathersName) }

    private fun updateEmail(email: String) = _uiState.update { it.copy(email = email) }

    private fun updateAge(age: String) = _uiState.update { it.copy(age = age) }

    private fun updateAddress(address: String) = _uiState.update { it.copy(address = address) }
}