package app_client.domain.viewModel

import app_client.data.*
import app_client.domain.uiEvent.*
import app_client.domain.uiState.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class ClientInfoViewModel(clientInfoRepository: ClientInfoRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<ClientInfoUiState> = MutableStateFlow(ClientInfoUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: ClientInfoUiEvent) {
        when (event) {

            else -> {}
        }
    }

}