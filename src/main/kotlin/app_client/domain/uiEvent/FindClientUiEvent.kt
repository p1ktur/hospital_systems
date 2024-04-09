package app_client.domain.uiEvent

import app_client.domain.model.*

sealed class FindClientUiEvent {
    data class UpdateSearchText(val text: String) : FindClientUiEvent()
    data object Search : FindClientUiEvent()
    data class Sort(val sort: FindClientSort) : FindClientUiEvent()
}