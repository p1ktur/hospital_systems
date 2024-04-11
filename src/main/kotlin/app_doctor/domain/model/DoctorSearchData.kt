package app_doctor.domain.model

data class DoctorSearchData(
    val name: String = "",
    val surname: String = "",
    val age: Int = 0,
    val phone: String = "",
    val position: String = "",
    val salary: Float = 0f,
    val login: String = "",
    val userWorkerId: Int = -1
)
