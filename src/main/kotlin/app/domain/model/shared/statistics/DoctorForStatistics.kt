package app.domain.model.shared.statistics

data class DoctorForStatistics(
    val userDoctorId: Int = -1,
    val name: String = "",
    val surname: String = "",
    val login: String = "",
    val appointments: Int = 0,
    val earnedMoney: Int = 0
)
