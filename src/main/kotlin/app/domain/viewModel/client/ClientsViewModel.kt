package app.domain.viewModel.client

import app.data.client.*
import app.domain.database.transactor.*
import app.domain.model.client.*
import app.domain.model.doctor.*
import app.domain.uiEvent.client.*
import app.domain.uiState.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class ClientsViewModel(private val clientsRepository: ClientsRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<ClientsUiState> = MutableStateFlow(ClientsUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchedClientData: List<ClientSearchData> = emptyList()
    private var sortAscending = true

    fun onUiEvent(event: ClientsUiEvent) {
        when (event) {
            is ClientsUiEvent.UpdateSearchText -> updateSearchText(event.text)
            ClientsUiEvent.Search -> search()
            is ClientsUiEvent.Sort -> sort(event.sort)
        }
    }

    private fun updateSearchText(text: String) {
        _uiState.value = uiState.value.copy(
            searchText = text,
            clientSearchData = fetchedClientData.filter {
                (it.name + it.surname + it.login + it.phone + it.age).contains(text, ignoreCase = true)
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

            when (val searchResult = clientsRepository.search()) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    fetchedClientData = searchResult.data as List<ClientSearchData>

                    _uiState.value = uiState.value.copy(
                        clientSearchData = when (uiState.value.sort) {
                            ClientsSort.NAME -> fetchedClientData.sortedBy { it.name }
                            ClientsSort.AGE -> fetchedClientData.sortedBy { it.age }
                        },
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sort(sort: ClientsSort) {
        if (uiState.value.isLoading) return

        if (sort == uiState.value.sort) {
            sortAscending = !sortAscending
        } else {
            _uiState.value = uiState.value.copy(
                sort = sort
            )
        }

        _uiState.value = uiState.value.copy(
            clientSearchData = if (sortAscending) {
                when (sort) {
                    ClientsSort.NAME -> uiState.value.clientSearchData.sortedBy { it.name }
                    ClientsSort.AGE -> uiState.value.clientSearchData.sortedBy { it.age }
                }
            } else {
                when (sort) {
                    ClientsSort.NAME -> uiState.value.clientSearchData.sortedByDescending { it.name }
                    ClientsSort.AGE -> uiState.value.clientSearchData.sortedByDescending { it.age }
                }
            }
        )
    }
}