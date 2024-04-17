package app.domain.model.shared.appointment

data class AppointmentResult(
    val id: Int,
    val paymentId: Int,
    val notes: String,
    val price: Float
)
