package app_shared.domain.model.database.dbModels

data class Payment(
    val id: Int,
    val payedAmount: Float,
    val payedAccount: String,
    val time: String
)
