package app.domain.model.shared.room

data class RoomSearchData(
    val id: Int,
    val name: String,
    val type: String,
    val floor: Int,
    val number: Int
)
