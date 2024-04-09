package app_client.domain.viewModel

import app_client.data.*
import app_client.domain.model.*
import app_client.domain.uiEvent.*
import app_client.domain.uiState.*
import app_shared.domain.model.transactor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class FindClientViewModel(private val findClientRepository: FindClientRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<FindClientUiState> = MutableStateFlow(FindClientUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchedClientData: List<ClientSearchData> = emptyList()

    fun onUiEvent(event: FindClientUiEvent) {
        when (event) {
            is FindClientUiEvent.UpdateSearchText -> updateSearchText(event.text)
            FindClientUiEvent.Search -> search()
            is FindClientUiEvent.Sort -> sort(event.sort)
        }
    }

    private fun updateSearchText(text: String) {
        _uiState.value = uiState.value.copy(
            searchText = text,
            clientSearchData = fetchedClientData.filter {
                (it.name + it.surname + it.login + it.phone + it.age).lowercase().contains(text.lowercase())
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun search() {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                isLoading = true
            )

            when (val searchResult = findClientRepository.search()) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    fetchedClientData = searchResult.data as List<ClientSearchData>

                    _uiState.value = uiState.value.copy(
                        clientSearchData = fetchedClientData,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sort(sort: FindClientSort) {
        _uiState.value = uiState.value.copy(
            clientSearchData = when (sort) {
                FindClientSort.NAME -> uiState.value.clientSearchData.sortedBy { it.name }
                FindClientSort.AGE -> uiState.value.clientSearchData.sortedBy { it.age }
            }
        )
    }
}