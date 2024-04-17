package app.domain.model.shared

data class Payment(
    val id: Int,
    val payedAmount: Float,
    val payedAccount: String,
    val time: String
)
