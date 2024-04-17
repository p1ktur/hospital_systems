package app_shared.domain.model.forShared.appointment

data class AppointmentResult(
    val id: Int,
    val paymentId: Int,
    val notes: String,
    val price: Float
)
