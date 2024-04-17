package app.domain.uiEvent.doctor

import app.domain.model.doctor.*

sealed class DoctorsUiEvent {
    data class UpdateSearchText(val text: String) : DoctorsUiEvent()
    data class Search(val all: Boolean) : DoctorsUiEvent()
    data class Sort(val sort: DoctorsSort) : DoctorsUiEvent()
}