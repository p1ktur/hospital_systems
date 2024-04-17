package app_shared.domain.model.forShared.hospitalization

data class Hospitalization(
    val id: Int,
    val clientName: String,
    val clientLogin: String,
    val userClientId: Int,
    val reason: String,
    val startDate: String,
    val endDate: String?,
    val paymentId: Int,
    val price: Float
)
