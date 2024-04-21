package app.data.doctor

import app.domain.database.transactor.*
import app.domain.model.doctor.*
import app.domain.model.shared.room.*
import java.time.format.*

class DoctorInfoRepository(private val transactor: ITransactor) {

    fun fetchInfo(userWorkerId: Int): TransactorResult = transactor.startTransaction {
        val userWorkerStatement = createStatement()
        val userWorkerResult = userWorkerStatement.executeQuery("SELECT user_id, worker_id FROM user_doctor WHERE id = $userWorkerId")
        userWorkerResult.next()

        val pendingAppointmentsStatement = prepareStatement("SELECT COUNT(*) FROM appointment WHERE doctor_id = ? AND appointment.result_id IS NULL")
        pendingAppointmentsStatement.setInt(1, userWorkerResult.getInt(2))
        val pendingAppointmentsResult = pendingAppointmentsStatement.executeQuery()
        pendingAppointmentsResult.next()
        val pendingAppointments = pendingAppointmentsResult.getInt(1)

        val finishedAppointmentsStatement = prepareStatement("SELECT COUNT(*) FROM appointment WHERE doctor_id = ? AND appointment.result_id IS NOT NULL")
        finishedAppointmentsStatement.setInt(1, userWorkerResult.getInt(2))
        val finishedAppointmentsResult = finishedAppointmentsStatement.executeQuery()
        finishedAppointmentsResult.next()
        val finishedAppointments = finishedAppointmentsResult.getInt(1)

        val doctorInfoStatement = prepareStatement("SELECT name, surname, fathers_name, age, address, phone, email, position, salary, room_id, creation_date FROM worker " +
                "WHERE worker.id = ?")
        doctorInfoStatement.setInt(1, userWorkerResult.getInt(2))
        val doctorInfoResult = doctorInfoStatement.executeQuery()
        doctorInfoResult.next()

        val designationStatement = prepareStatement("SELECT room.name, floor, number FROM room " +
                "INNER JOIN worker ON worker.room_id = room.id " +
                "INNER JOIN room_location ON room.location_id = room_location.id " +
                "INNER JOIN room_type ON room_location.type_id = room_type.id " +
                "WHERE worker.id = ?")
        designationStatement.setInt(1, userWorkerResult.getInt(2))
        val designationResult = designationStatement.executeQuery()
        val isThereADesignation = designationResult.next()

        val roomPreloadStatement = prepareStatement("SELECT room.id, name, floor, number FROM room LEFT JOIN room_location ON room.location_id = room_location.id")
        val roomPreloadResult = roomPreloadStatement.executeQuery()

        val preloadedRooms = buildList {
            while (roomPreloadResult.next()) add(
                Room(
                    id = roomPreloadResult.getInt(1),
                    name = roomPreloadResult.getString(2),
                    floor = roomPreloadResult.getInt(3),
                    number = roomPreloadResult.getInt(4)
                )
            )
        }

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")

        val doctorInfoData = DoctorInfoData(
            name = doctorInfoResult.getString(1),
            surname = doctorInfoResult.getString(2),
            fathersName = doctorInfoResult.getString(3),
            age = doctorInfoResult.getInt(4),
            address = doctorInfoResult.getString(5),
            phone = doctorInfoResult.getString(6),
            email = doctorInfoResult.getString(7),
            position = doctorInfoResult.getString(8),
            salary = doctorInfoResult.getFloat(9),
            registrationDate = doctorInfoResult.getTimestamp(11).toLocalDateTime().format(formatter),
            designationName = if (isThereADesignation) designationResult.getString(1) else "",
            designationFloor = if (isThereADesignation) designationResult.getInt(2) else 0,
            designationNumber = if (isThereADesignation) designationResult.getInt(3) else 0,
            pendingAppointments = pendingAppointments,
            finishedAppointments = finishedAppointments,
            preloadedRooms = preloadedRooms.sortedBy { it.id },
            designationIndex = doctorInfoResult.getInt(10)
        )
        TransactorResult.Success(doctorInfoData)
    }

    fun saveChanges(
        userDoctorId: Int,
        name: String,
        surname: String,
        fathersName: String,
        age: String,
        address: String,
        phone: String,
        email: String,
        position: String,
        salary: String,
        designationId: Int
    ): TransactorResult = transactor.startTransaction {
        val updateStatement = if (designationId >= 0) {
            prepareStatement("UPDATE worker SET name = ?, surname = ?, fathers_name = ?, age = ?, address = ?, phone = ?, email = ?, position = ?, salary = ?, room_id = ? " +
                    "FROM user_doctor " +
                    "WHERE user_doctor.id = ? AND user_doctor.worker_id = worker.id")
        } else {
            prepareStatement("UPDATE worker SET name = ?, surname = ?, fathers_name = ?, age = ?, address = ?, phone = ?, email = ?, position = ?, salary = ? " +
                    "FROM user_doctor " +
                    "WHERE user_doctor.id = ? AND user_doctor.worker_id = worker.id")
        }
        updateStatement.setString(1, name)
        updateStatement.setString(2, surname)
        updateStatement.setString(3, fathersName)
        updateStatement.setInt(4, age.toInt())
        updateStatement.setString(5, address)
        updateStatement.setString(6, phone)
        updateStatement.setString(7, email)
        updateStatement.setString(8, position)
        updateStatement.setFloat(9, salary.toFloat())
        if (designationId >= 0) {
            updateStatement.setInt(10, designationId)
            updateStatement.setInt(11, userDoctorId)
        } else {
            updateStatement.setInt(10, userDoctorId)
        }
        updateStatement.executeUpdate()

        TransactorResult.Success(userDoctorId)
    }
}