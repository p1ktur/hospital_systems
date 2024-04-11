package app_doctor.domain.uiState

data class DoctorScheduleUiState(
    val startTime: String = "",
    val endTime: String = "",
    val startDay: String = "",
    val endDay: String = "",
    val hoursForRest: Float = 0f,
    val isLoading: Boolean = false,
    val errorCodes: List<Int> = emptyList(),
)