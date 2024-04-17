package app.domain.model.shared.appointment

data class Appointment(
    val id: Int,
    val clientName: String,
    val clientLogin: String,
    val doctorName: String,
    val userClientId: Int,
    val userDoctorId: Int,
    val resultId: Int,
    val date: String
)
