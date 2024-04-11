package app_doctor.domain.uiState

import app_doctor.domain.model.*

data class FindDoctorUiState(
    val searchText: String = "",
    val doctorSearchData: List<DoctorSearchData> = emptyList(),
    val isLoading: Boolean = false
)