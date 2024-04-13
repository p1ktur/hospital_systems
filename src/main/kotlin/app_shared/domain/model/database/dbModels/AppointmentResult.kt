package app_shared.domain.model.database.dbModels

data class AppointmentResult(
    val id: Int,
    val paymentId: Int,
    val notes: String,
    val price: Float
)
