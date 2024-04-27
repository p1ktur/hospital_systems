package app.domain.model.doctor

data class DoctorScheduleData(
    val doctorName: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val restStartTime: String = "",
    val restEndTime: String = "",
    val startDay: String = "",
    val endDay: String = ""
)
