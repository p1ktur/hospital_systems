package app.domain.model.shared.payment

data class Payment(
    val id: Int,
    val payedAmount: Float,
    val payedAccount: String,
    val time: String
)
