package app_shared.domain.model.forShared.room

data class RoomSearchData(
    val id: Int,
    val name: String,
    val type: String,
    val floor: Int,
    val number: Int
)
