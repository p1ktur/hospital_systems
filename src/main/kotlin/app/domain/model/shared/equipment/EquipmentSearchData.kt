package app.domain.model.shared.equipment

import app.domain.model.shared.room.*

data class EquipmentSearchData(
    val id: Int,
    val roomId: Int,
    val name: String,
    val notes: String,
    val room: Room?
)
