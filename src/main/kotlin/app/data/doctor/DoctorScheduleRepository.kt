package app.data.doctor

import app.domain.database.transactor.*
import app.domain.model.doctor.*
import java.sql.*
import java.time.format.*

class DoctorScheduleRepository(private val transactor: ITransactor) {

    fun fetchInfoSafely(userWorkerId: Int): TransactorResult = transactor.startTransaction {
        val userWorkerStatement = createStatement()
        val userWorkerResult = userWorkerStatement.executeQuery("SELECT user_id, worker_id FROM user_doctor WHERE id = $userWorkerId")
        userWorkerResult.next()

        val scheduleStatement = prepareStatement("SELECT start_time, end_time, start_day, end_day, rest_start_time, rest_end_time, name FROM schedule, worker " +
                "WHERE worker.id = ? AND worker.schedule_id = schedule.id")
        scheduleStatement.setInt(1, userWorkerResult.getInt(2))
        val scheduleResult = scheduleStatement.executeQuery()

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val doctorScheduleData = if (scheduleResult.next()) {
            DoctorScheduleData(
                doctorName = scheduleResult.getString(7),
                startTime = scheduleResult.getTime(1)?.toLocalTime()?.format(formatter) ?: "",
                endTime = scheduleResult.getTime(2)?.toLocalTime()?.format(formatter) ?: "",
                startDay = scheduleResult.getString(3),
                endDay = scheduleResult.getString(4),
                restStartTime = scheduleResult.getTime(5)?.toLocalTime()?.format(formatter) ?: "",
                restEndTime = scheduleResult.getTime(6)?.toLocalTime()?.format(formatter) ?: ""
            )
        } else DoctorScheduleData()

        TransactorResult.Success(doctorScheduleData)
    }

    fun fetchInfo(userWorkerId: Int): TransactorResult = transactor.startTransaction {
        val userWorkerStatement = createStatement()
        val userWorkerResult = userWorkerStatement.executeQuery("SELECT user_id, worker_id FROM user_doctor WHERE id = $userWorkerId")
        userWorkerResult.next()

        val scheduleStatement = prepareStatement("SELECT start_time, end_time, start_day, end_day, rest_start_time, rest_end_time, name FROM schedule, worker " +
                "WHERE worker.id = ? AND worker.schedule_id = schedule.id")
        scheduleStatement.setInt(1, userWorkerResult.getInt(2))
        val scheduleResult = scheduleStatement.executeQuery()

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        if (scheduleResult.next()) {
            val doctorScheduleData = DoctorScheduleData(
                doctorName = scheduleResult.getString(7),
                startTime = scheduleResult.getTime(1)?.toLocalTime()?.format(formatter) ?: "",
                endTime = scheduleResult.getTime(2)?.toLocalTime()?.format(formatter) ?: "",
                startDay = scheduleResult.getString(3),
                endDay = scheduleResult.getString(4),
                restStartTime = scheduleResult.getTime(5)?.toLocalTime()?.format(formatter) ?: "",
                restEndTime = scheduleResult.getTime(6)?.toLocalTime()?.format(formatter) ?: ""
            )

            TransactorResult.Success(doctorScheduleData)
        } else {
            TransactorResult.Failure(Exception())
        }
    }

    fun saveChanges(
        userDoctorId: Int,
        startTime: String,
        endTime: String,
        startDay: String,
        endDay: String,
        restStartTime: String,
        restEndTime: String,
    ): TransactorResult = transactor.startTransaction {
        val checkStatement = prepareStatement("SELECT schedule.id FROM schedule, user_doctor, worker " +
                "WHERE user_doctor.id = ? AND user_doctor.worker_id = worker.id AND worker.schedule_id = schedule.id")
        checkStatement.setInt(1, userDoctorId)
        val checkResult = checkStatement.executeQuery()

        if (checkResult.next()) {
            val updateStatement = prepareStatement("UPDATE schedule SET start_time = ?, end_time = ?, start_day = ?, end_day = ?, rest_start_time = ?, rest_end_time = ? " +
                    "FROM user_doctor, worker " +
                    "WHERE user_doctor.id = ? AND user_doctor.worker_id = worker.id AND worker.schedule_id = schedule.id")
            updateStatement.setTime(1, Time.valueOf("$startTime:00"))
            updateStatement.setTime(2, Time.valueOf("$endTime:00"))
            updateStatement.setString(3, startDay)
            updateStatement.setString(4, endDay)
            updateStatement.setTime(5, Time.valueOf("$restStartTime:00"))
            updateStatement.setTime(6, Time.valueOf("$restEndTime:00"))
            updateStatement.setInt(7, userDoctorId)
            updateStatement.executeUpdate()
        } else {
            val updateStatement = prepareStatement("INSERT INTO schedule (start_time, end_time, start_day, end_day, rest_start_time, rest_end_time) VALUES (?, ?, ?, ?, ?, ?) ")
            updateStatement.setTime(1, Time.valueOf("$startTime:00"))
            updateStatement.setTime(2, Time.valueOf("$endTime:00"))
            updateStatement.setString(3, startDay)
            updateStatement.setString(4, endDay)
            updateStatement.setTime(5, Time.valueOf("$restStartTime:00"))
            updateStatement.setTime(6, Time.valueOf("$restEndTime:00"))
            updateStatement.executeUpdate()

            val addedScheduleIdStatement = createStatement()
            val addedScheduleIdResult = addedScheduleIdStatement.executeQuery("SELECT MAX(id) FROM schedule")
            addedScheduleIdResult.next()

            val updateDoctorStatement = prepareStatement("UPDATE worker SET schedule_id = ? " +
                    "FROM user_doctor " +
                    "WHERE user_doctor.id = ? AND user_doctor.worker_id = worker.id")
            updateDoctorStatement.setInt(1, addedScheduleIdResult.getInt(1))
            updateDoctorStatement.setInt(2, userDoctorId)
            updateDoctorStatement.executeUpdate()
        }

        TransactorResult.Success(userDoctorId)
    }
}