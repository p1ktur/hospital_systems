package app_shared.domain.model.database.dbModels

data class Room(
    val id: Int,
    val name: String,
    val floor: Int,
    val number: Int
)
