package app.domain.model.shared.payment

data class FetchPaymentData(
    val payments: List<Payment.Default> = emptyList(),
    val subPayments: List<Payment.Sub> = emptyList()
)
