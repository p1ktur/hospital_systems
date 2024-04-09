package app_client.domain.uiState

import app_client.domain.model.*

data class FindClientUiState(
    val searchText: String = "",
    val clientSearchData: List<ClientSearchData> = emptyList(),
    val isLoading: Boolean = false
)