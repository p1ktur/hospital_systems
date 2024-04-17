package app_doctor.domain.uiEvent

import app_doctor.domain.model.*

sealed class DoctorsUiEvent {
    data class UpdateSearchText(val text: String) : DoctorsUiEvent()
    data class Search(val all: Boolean) : DoctorsUiEvent()
    data class Sort(val sort: DoctorsSort) : DoctorsUiEvent()
}