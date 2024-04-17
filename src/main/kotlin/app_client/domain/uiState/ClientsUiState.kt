package app_client.domain.uiState

import app_client.domain.model.*

data class ClientsUiState(
    val sort: ClientsSort = ClientsSort.NAME,
    val searchText: String = "",
    val clientSearchData: List<ClientSearchData> = emptyList(),
    val isLoading: Boolean = false
)