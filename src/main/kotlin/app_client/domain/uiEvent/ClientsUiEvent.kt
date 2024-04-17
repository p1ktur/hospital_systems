package app_client.domain.uiEvent

import app_client.domain.model.*

sealed class ClientsUiEvent {
    data class UpdateSearchText(val text: String) : ClientsUiEvent()
    data object Search : ClientsUiEvent()
    data class Sort(val sort: ClientsSort) : ClientsUiEvent()
}