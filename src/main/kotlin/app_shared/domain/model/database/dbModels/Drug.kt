package app_shared.domain.model.database.dbModels

data class Drug(
    val id: Int,
    val name: String,
    val appliances: String,
    val notes: String,
    val amount: Int
)
