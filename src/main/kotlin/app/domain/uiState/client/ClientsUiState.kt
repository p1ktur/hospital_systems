package app.domain.uiState.client

import app.domain.model.client.*

data class ClientsUiState(
    val sort: ClientsSort = ClientsSort.NAME,
    val searchText: String = "",
    val clientSearchData: List<ClientSearchData> = emptyList(),
    val isLoading: Boolean = false
)