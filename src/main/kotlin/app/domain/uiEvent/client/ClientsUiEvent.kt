package app.domain.uiEvent.client

import app.domain.model.client.*

sealed class ClientsUiEvent {
    data class UpdateSearchText(val text: String) : ClientsUiEvent()
    data object Search : ClientsUiEvent()
    data class Sort(val sort: ClientsSort) : ClientsUiEvent()
}