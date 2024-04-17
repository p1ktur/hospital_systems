package app.domain.model.shared.drug

data class Drug(
    val id: Int,
    val name: String,
    val appliances: String,
    val notes: String,
    val amount: Int
)
