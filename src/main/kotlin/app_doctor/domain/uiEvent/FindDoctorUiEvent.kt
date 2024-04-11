package app_doctor.domain.uiEvent

import app_doctor.domain.model.*

sealed class FindDoctorUiEvent {
    data class UpdateSearchText(val text: String) : FindDoctorUiEvent()
    data object Search : FindDoctorUiEvent()
    data class Sort(val sort: FindDoctorSort) : FindDoctorUiEvent()
}