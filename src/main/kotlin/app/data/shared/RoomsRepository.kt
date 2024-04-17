package app.data.shared

import app.domain.database.transactor.*
import app.domain.model.shared.room.*
import app.domain.util.exceptions.*

class RoomsRepository(private val transactor: ITransactor) {

    fun preloadTypes(): TransactorResult = transactor.startTransaction {
        val typesStatement = createStatement()
        val typesResult = typesStatement.executeQuery("SELECT id, name FROM room_type")

        val typesList = buildList {
            while (typesResult.next()) add(
                typesResult.getInt(1) to typesResult.getString(2)
            )
        }

        TransactorResult.Success(typesList)
    }

    fun search(all: Boolean): TransactorResult = transactor.startTransaction {
        val searchStatement = if (all) {
            prepareStatement("SELECT room.id, room.name, room_type.name, floor, number FROM room " +
                    "INNER JOIN room_location ON room.location_id = room_location.id " +
                    "INNER JOIN room_type ON room_location.type_id = room_type.id ")
        } else {
            prepareStatement("SELECT room.id, room.name, room_type.name, floor, number FROM room " +
                    "INNER JOIN room_location ON room.location_id = room_location.id " +
                    "INNER JOIN room_type ON room_location.type_id = room_type.id " +
                    "WHERE room_type.id = 2")
        }
        val searchResult = searchStatement.executeQuery()

        val roomSearchDataList = buildList {
            while (searchResult.next()) add(
                RoomSearchData(
                    id = searchResult.getInt(1),
                    name = searchResult.getString(2),
                    type = searchResult.getString(3),
                    floor = searchResult.getInt(4),
                    number = searchResult.getInt(5)
                )
            )
        }

        TransactorResult.Success(roomSearchDataList.sortedBy { it.name })
    }

    fun create(room: RoomSearchData): TransactorResult = transactor.startTransaction {
        val typeIdStatement = prepareStatement("SELECT id FROM room_type WHERE name = ?")
        typeIdStatement.setString(1, room.type)
        val typeIdResult = typeIdStatement.executeQuery()
        typeIdResult.next()
        val typeId = typeIdResult.getInt(1)

        val locationCheckStatement = prepareStatement("SELECT * FROM room_location WHERE floor = ? AND number = ?")
        locationCheckStatement.setInt(1, room.floor)
        locationCheckStatement.setInt(2, room.number)
        val locationCheckResult = locationCheckStatement.executeQuery()
        if (locationCheckResult.next()) {
            return@startTransaction TransactorResult.Failure(AlreadyExistsException(1101, "Room at this location exists."))
        }

        val createLocationStatement = prepareStatement("INSERT INTO room_location (type_id, floor, number) VALUES (?, ?, ?) RETURNING id;")
        createLocationStatement.setInt(1, typeId)
        createLocationStatement.setInt(2, room.floor)
        createLocationStatement.setInt(3, room.number)
        val createLocationResult = createLocationStatement.executeQuery()
        createLocationResult.next()
        val locationId = createLocationResult.getInt(1)

        val createStatement = prepareStatement("INSERT INTO room (location_id, name) VALUES (?, ?);")
        createStatement.setInt(1, locationId)
        createStatement.setString(2, room.name)
        createStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun update(room: RoomSearchData): TransactorResult = transactor.startTransaction {
        val typeIdStatement = prepareStatement("SELECT id FROM room_type WHERE name = ?")
        typeIdStatement.setString(1, room.type)
        val typeIdResult = typeIdStatement.executeQuery()
        typeIdResult.next()
        val typeId = typeIdResult.getInt(1)

        val updateLocationStatement = prepareStatement("UPDATE room_location SET type_id = ? WHERE floor = ? AND number = ? RETURNING id;")
        updateLocationStatement.setInt(1, typeId)
        updateLocationStatement.setInt(2, room.floor)
        updateLocationStatement.setInt(3, room.number)
        val updateLocationResult = updateLocationStatement.executeQuery()
        updateLocationResult.next()
        val locationId = updateLocationResult.getInt(1)

        val updateStatement = prepareStatement("UPDATE room SET name = ? WHERE location_id = ?")
        updateStatement.setString(1, room.name)
        updateStatement.setInt(2, locationId)
        updateStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun delete(room: RoomSearchData): TransactorResult = transactor.startTransaction {
        val deleteLocationStatement = prepareStatement("DELETE FROM room_location WHERE floor = ? AND number = ? RETURNING id;")
        deleteLocationStatement.setInt(1, room.floor)
        deleteLocationStatement.setInt(2, room.number)
        val updateLocationResult = deleteLocationStatement.executeQuery()
        updateLocationResult.next()
        val locationId = updateLocationResult.getInt(1)

        val deleteStatement = prepareStatement("DELETE FROM room WHERE location_id = ?")
        deleteStatement.setInt(1, locationId)
        deleteStatement.executeUpdate()

        TransactorResult.Success("Success")
    }
}