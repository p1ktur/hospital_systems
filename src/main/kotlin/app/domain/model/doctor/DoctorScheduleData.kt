package app.domain.model.doctor

data class DoctorScheduleData(
    val startTime: String = "",
    val endTime: String = "",
    val startDay: String = "",
    val endDay: String = "",
    val hoursForRest: Float = 0f
)
