package app.domain.uiState.doctor

data class DoctorScheduleUiState(
    val startTime: String = "",
    val endTime: String = "",
    val startDay: String = "",
    val endDay: String = "",
    val restStartTime: String = "",
    val restEndTime: String = "",
    val isLoading: Boolean = false,
    val errorCodes: List<Int> = emptyList(),
)