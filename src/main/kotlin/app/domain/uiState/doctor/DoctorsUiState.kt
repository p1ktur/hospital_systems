package app.domain.uiState.doctor

import app.domain.model.doctor.*

data class DoctorsUiState(
    val sort: DoctorsSort = DoctorsSort.NAME,
    val searchText: String = "",
    val doctorSearchData: List<DoctorSearchData> = emptyList(),
    val isLoading: Boolean = false
)