package app_doctor.domain.uiState

import app_doctor.domain.model.*

data class DoctorsUiState(
    val sort: DoctorsSort = DoctorsSort.NAME,
    val searchText: String = "",
    val doctorSearchData: List<DoctorSearchData> = emptyList(),
    val isLoading: Boolean = false
)