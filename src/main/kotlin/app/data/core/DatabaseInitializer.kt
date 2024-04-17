package app.data.core

import app.domain.database.transactor.*
import app.domain.model.shared.*
import app.domain.util.vocabulary.*
import com.opencsv.*
import kotlin.random.*

class DatabaseInitializer(
    private val transactor: ITransactor,
    private val vocabulary: Vocabulary
) {

    fun initDatabaseWithData(): TransactorResult = transactor.startTransaction(onSQLException = { deleteInitData() }) {
        val random = Random(System.currentTimeMillis())

        //init db: room, room_location, room_type, drugs, medical_equipment

        //room type names: Doctor, Staff
        //room locations: floors: 5, number: 101 - 120, 1-2 floors are staff, 3-5 floors are doctor
        //room for each location if staff -> ward, if doctor -> position name

        val roomTypeStatement = createStatement()
        roomTypeStatement.executeUpdate("INSERT INTO room_type (id, name) VALUES (1, 'Doctor'), (2, 'Staff') ON CONFLICT DO NOTHING")

        val roomLocationStatement = prepareStatement("INSERT INTO room_location (id, type_id, floor, number) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")
        val roomStatement = prepareStatement("INSERT INTO room (id, location_id, name) VALUES (?, ?, ?) ON CONFLICT DO NOTHING")

        // total floors = 5
        var roomLocationIndex = 0
        var patientRoomsCounter = 0

        val patientNumbers = 20 // floors 1-2
        val doctorNumbers = 10 // floors 3-5

        for (i in 1..2) {
            for (j in 1..patientNumbers) {
                roomLocationStatement.setInt(1, ++roomLocationIndex)
                roomLocationStatement.setInt(2, 2)
                roomLocationStatement.setInt(3, i)
                roomLocationStatement.setInt(4, 100 * i + j)

                roomStatement.setInt(1, roomLocationIndex)
                roomStatement.setInt(2, roomLocationIndex)
                roomStatement.setString(3, "Ward ${++patientRoomsCounter}")

                roomLocationStatement.executeUpdate()
                roomStatement.executeUpdate()
            }
        }

        for (i in 3..5) {
            for (j in 1..doctorNumbers) {
                roomLocationStatement.setInt(1, ++roomLocationIndex)
                roomLocationStatement.setInt(2, 1)
                roomLocationStatement.setInt(3, i)
                roomLocationStatement.setInt(4, 100 * i + j)

                roomStatement.setInt(1, roomLocationIndex)
                roomStatement.setInt(2, roomLocationIndex)
                roomStatement.setString(3, vocabulary.doctorPositions[10 * (i - 3) + j - 1].second)

                roomLocationStatement.executeUpdate()
                roomStatement.executeUpdate()
            }
        }

        val drugStatement = prepareStatement("INSERT INTO drug (id, name, appliances, notes, amount) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING")

        parseDrugsCSV(random).forEach { drug ->
            drugStatement.setInt(1, drug.id)
            drugStatement.setString(2, drug.name)
            drugStatement.setString(3, drug.appliances)
            drugStatement.setString(4, drug.notes)
            drugStatement.setInt(5, drug.amount)

            drugStatement.executeUpdate()
        }

        val equipmentStatement = prepareStatement("INSERT INTO equipment (id, room_id, name, notes) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")

        var equipmentLocationIndex = 0
        for (i in 1..roomLocationIndex) {
            for (j in 0 until random.nextInt(3, 6)) {
                equipmentStatement.setInt(1, ++equipmentLocationIndex)
                equipmentStatement.setInt(2, i)
                equipmentStatement.setString(
                    3,
                    if (i <= patientRoomsCounter) {
                        vocabulary.wardEquipment.random(random)
                    } else {
                        vocabulary.officeEquipment.random(random)
                    }
                )
                equipmentStatement.setString(4, "")

                equipmentStatement.executeUpdate()
            }
        }

        TransactorResult.Success("Success")
    }

    private fun deleteInitData(): TransactorResult = transactor.startTransaction {
        val deleteStatement = createStatement()
        deleteStatement.executeUpdate("DELETE FROM equipment WHERE TRUE; " +
                "DELETE FROM room WHERE TRUE; " +
                "DELETE FROM room_location WHERE TRUE; " +
                "DELETE FROM room_type WHERE TRUE; " +
                "DELETE FROM drug WHERE TRUE; ")

        TransactorResult.Success("Success")
    }

    private fun parseDrugsCSV(random: Random): List<Drug> {
        return try {
            val drugsList = mutableListOf<Drug>()
            val file = javaClass.classLoader.getResourceAsStream("drugs_data.csv") ?: return emptyList()
            val reader = CSVReader(file.reader())

            val namesCache = mutableListOf<String>()
            var idCounter = 0
            var line: Array<String>? = reader.readNext()
            while (!line.isNullOrEmpty()) {
                if (!namesCache.contains(line[0]) && line[7].length <= 256) {
                    namesCache.add(line[0])
                    drugsList.add(
                        Drug(
                            id = ++idCounter,
                            name = line[0],
                            appliances = line[7],
                            notes = "",
                            amount = random.nextInt(100, 1000)
                        )
                    )
                }

                line = reader.readNext()
            }

            drugsList
        } catch (e: Exception) {
            println(e.message)
            emptyList()
        }
    }
}