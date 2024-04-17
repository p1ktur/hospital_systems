package app.data.shared

import app.domain.database.transactor.*
import app.domain.model.shared.equipment.*
import app.domain.model.shared.room.*

class EquipmentsRepository(
    private val transactor: ITransactor,
    private val roomsRepository: RoomsRepository
) {

    fun search(): TransactorResult = transactor.startTransaction {
        val searchStatement = prepareStatement("SELECT id, room_id, name, notes FROM equipment")
        val searchResult = searchStatement.executeQuery()

        val equipmentSearchDataList = buildList {
            while (searchResult.next()) {
                val roomId = searchResult.getInt(2)
                add(
                    EquipmentSearchData(
                        id = searchResult.getInt(1),
                        roomId = roomId,
                        name = searchResult.getString(3),
                        notes = searchResult.getString(4),
                        room = when (val getResult = roomsRepository.getById(roomId)) {
                            is TransactorResult.Failure -> null
                            is TransactorResult.Success<*> -> getResult.data as? Room
                        }
                    )
                )
            }
        }

        TransactorResult.Success(equipmentSearchDataList.sortedBy { it.name })
    }

    fun create(equipment: EquipmentSearchData): TransactorResult = transactor.startTransaction {
        val createStatement = prepareStatement("INSERT INTO equipment (name, room_id, notes) VALUES (?, ?, ?);")
        createStatement.setString(1, equipment.name)
        createStatement.setInt(2, equipment.roomId)
        createStatement.setString(3, equipment.notes)
        createStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun update(equipment: EquipmentSearchData): TransactorResult = transactor.startTransaction {
        val updateStatement = prepareStatement("UPDATE equipment SET name = ?, room_id = ?, notes = ? WHERE id = ?")
        updateStatement.setString(1, equipment.name)
        updateStatement.setInt(2, equipment.roomId)
        updateStatement.setString(3, equipment.notes)
        updateStatement.setInt(4, equipment.id)
        updateStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun delete(equipment: EquipmentSearchData): TransactorResult = transactor.startTransaction {
        val deleteStatement = prepareStatement("DELETE FROM equipment WHERE id = ?")
        deleteStatement.setInt(1, equipment.id)
        deleteStatement.executeUpdate()

        TransactorResult.Success("Success")
    }
}