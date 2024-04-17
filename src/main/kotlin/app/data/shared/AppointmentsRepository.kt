package app.data.shared

import app.domain.database.transactor.*
import app.domain.model.shared.appointment.*
import app.domain.model.shared.payment.*
import java.sql.*
import java.time.format.*

class AppointmentsRepository(private val transactor: ITransactor) {

    fun fetchAppointmentsForAdmin(): TransactorResult = transactor.startTransaction {
        val userWorkersStatement = createStatement()
        val userWorkersResult = userWorkersStatement.executeQuery("SELECT id FROM user_doctor")

        val appointments = mutableListOf<Appointment>()
        val appointmentResults = mutableListOf<AppointmentResult>()
        val payments = mutableListOf<Payment>()

        while (userWorkersResult.next()) {
            when (val fetchResult = fetchAppointmentsForDoctor(userWorkersResult.getInt(1))) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    val data = fetchResult.data as FetchAppointmentData

                    appointments.addAll(data.appointments)
                    appointmentResults.addAll(data.appointmentResults)
                    payments.addAll(data.payments)
                }
            }
        }

        val fetchAppointmentData = FetchAppointmentData(
            appointments = appointments,
            appointmentResults = appointmentResults,
            payments = payments
        )
        TransactorResult.Success(fetchAppointmentData)
    }

    fun fetchAppointmentsForDoctor(userWorkerId: Int) = transactor.startTransaction {
        val nameStatement = createStatement()
        val nameResult = nameStatement.executeQuery("SELECT name, worker.id FROM user_doctor " +
                "INNER JOIN worker ON worker.id = user_doctor.worker_id " +
                "WHERE user_doctor.id = $userWorkerId")
        nameResult.next()

        val doctorName = nameResult.getString(1)
        val doctorId = nameResult.getInt(2)

        val appointmentsStatement = prepareStatement("SELECT appointment.id, patient.name, user_client.id, result_id, date, public.user.login FROM appointment " +
                "INNER JOIN medical_card ON appointment.medical_card_id = medical_card.id " +
                "INNER JOIN user_client ON user_client.medical_card_id = medical_card.id " +
                "INNER JOIN patient ON patient.id = medical_card.patient_id " +
                "INNER JOIN public.user ON public.user.id = user_client.user_id " +
                "WHERE doctor_id = ?")
        appointmentsStatement.setInt(1, doctorId)
        val appointmentsResult = appointmentsStatement.executeQuery()

        val appointments = buildList {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
            while (appointmentsResult.next()) add(
                Appointment(
                    id = appointmentsResult.getInt(1),
                    clientName = appointmentsResult.getString(2),
                    clientLogin = appointmentsResult.getString(6),
                    doctorName = doctorName,
                    userClientId = appointmentsResult.getInt(3),
                    userDoctorId = userWorkerId,
                    resultId = appointmentsResult.getInt(4),
                    date = appointmentsResult.getTimestamp(5).toLocalDateTime().format(formatter)
                )
            )
        }

        val appointmentResultsStatement = prepareStatement("SELECT appointment_result.id, payment_id, notes, price FROM appointment " +
                "INNER JOIN appointment_result ON appointment.result_id = appointment_result.id " +
                "WHERE doctor_id = ?")
        appointmentResultsStatement.setInt(1, doctorId)
        val appointmentResultsResult = appointmentResultsStatement.executeQuery()

        val appointmentResults = buildList {
            while (appointmentResultsResult.next()) add(
                AppointmentResult(
                    id = appointmentResultsResult.getInt(1),
                    paymentId = appointmentResultsResult.getInt(2),
                    notes = appointmentResultsResult.getString(3),
                    price = appointmentResultsResult.getFloat(4)
                )
            )
        }

        val paymentsStatement = prepareStatement("SELECT payment.id, payed_amount, payed_account, time FROM appointment " +
                "INNER JOIN appointment_result ON appointment.result_id = appointment_result.id " +
                "INNER JOIN payment ON appointment_result.payment_id = payment.id " +
                "WHERE doctor_id = ?")
        paymentsStatement.setInt(1, doctorId)
        val paymentsResult = paymentsStatement.executeQuery()

        val payments = buildList {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            while (paymentsResult.next()) add(
                Payment(
                    id = paymentsResult.getInt(1),
                    payedAmount = paymentsResult.getFloat(2),
                    payedAccount = paymentsResult.getString(3),
                    time = paymentsResult.getTime(4).toLocalTime().format(formatter)
                )
            )
        }

        val fetchAppointmentData = FetchAppointmentData(
            appointments = appointments.sortedByDescending { it.id },
            appointmentResults = appointmentResults,
            payments = payments
        )
        TransactorResult.Success(fetchAppointmentData)
    }

    fun fetchAppointmentsForClient(userClientId: Int) = transactor.startTransaction {
        val nameStatement = createStatement()
        val nameResult = nameStatement.executeQuery("SELECT patient.name, medical_card.id, public.user.login FROM user_client " +
                "INNER JOIN medical_card ON user_client.medical_card_id = medical_card.id " +
                "INNER JOIN patient ON medical_card.patient_id = patient.id " +
                "INNER JOIN public.user ON public.user.id = user_client.user_id " +
                "WHERE user_client.id = $userClientId")
        nameResult.next()

        val clientName = nameResult.getString(1)
        val clientLogin = nameResult.getString(3)
        val medCardId = nameResult.getInt(2)

        val appointmentsStatement = prepareStatement("SELECT appointment.id, worker.name, user_doctor.id, result_id, date FROM appointment " +
                "INNER JOIN medical_card ON appointment.medical_card_id = medical_card.id " +
                "INNER JOIN worker ON appointment.doctor_id = worker.id " +
                "INNER JOIN user_client ON user_client.medical_card_id = medical_card.id " +
                "INNER JOIN user_doctor ON user_doctor.worker_id = worker.id " +
                "WHERE appointment.medical_card_id = ?")
        appointmentsStatement.setInt(1, medCardId)
        val appointmentsResult = appointmentsStatement.executeQuery()

        val appointments = buildList {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
            while (appointmentsResult.next()) add(
                Appointment(
                    id = appointmentsResult.getInt(1),
                    clientName = clientName,
                    clientLogin = clientLogin,
                    doctorName = appointmentsResult.getString(2),
                    userClientId = userClientId,
                    userDoctorId = appointmentsResult.getInt(3),
                    resultId = appointmentsResult.getInt(4),
                    date = appointmentsResult.getTimestamp(5).toLocalDateTime().format(formatter)
                )
            )
        }

        val appointmentResultsStatement = prepareStatement("SELECT appointment_result.id, payment_id, notes, price FROM appointment " +
                "INNER JOIN appointment_result ON appointment.result_id = appointment_result.id " +
                "WHERE medical_card_id = ?")
        appointmentResultsStatement.setInt(1, medCardId)
        val appointmentResultsResult = appointmentResultsStatement.executeQuery()

        val appointmentResults = buildList {
            while (appointmentResultsResult.next()) add(
                AppointmentResult(
                    id = appointmentResultsResult.getInt(1),
                    paymentId = appointmentResultsResult.getInt(2),
                    notes = appointmentResultsResult.getString(3),
                    price = appointmentResultsResult.getFloat(4)
                )
            )
        }

        val paymentsStatement = prepareStatement("SELECT payment.id, payed_amount, payed_account, time FROM appointment " +
                "INNER JOIN appointment_result ON appointment.result_id = appointment_result.id " +
                "INNER JOIN payment ON appointment_result.payment_id = payment.id " +
                "WHERE medical_card_id = ?")
        paymentsStatement.setInt(1, medCardId)
        val paymentsResult = paymentsStatement.executeQuery()

        val payments = buildList {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            while (paymentsResult.next()) add(
                Payment(
                    id = paymentsResult.getInt(1),
                    payedAmount = paymentsResult.getFloat(2),
                    payedAccount = paymentsResult.getString(3),
                    time = paymentsResult.getTime(4).toLocalTime().format(formatter)
                )
            )
        }

        val fetchAppointmentData = FetchAppointmentData(
            appointments = appointments.sortedByDescending { it.id },
            appointmentResults = appointmentResults,
            payments = payments
        )
        TransactorResult.Success(fetchAppointmentData)
    }

    fun createAppointment(userWorkerId: Int, userClientId: Int, date: java.util.Date) = transactor.startTransaction {
        val medCardStatement = createStatement()
        val medCardResult = medCardStatement.executeQuery("SELECT medical_card.id FROM user_client " +
                "INNER JOIN medical_card ON medical_card.id = user_client.medical_card_id " +
                "WHERE user_client.id = $userClientId")
        medCardResult.next()

        val workerStatement = createStatement()
        val workerResult = workerStatement.executeQuery("SELECT worker.id FROM user_doctor " +
                "INNER JOIN worker ON worker.id = user_doctor.worker_id " +
                "WHERE user_doctor.id = $userWorkerId")
        workerResult.next()

        val insertStatement = prepareStatement("INSERT INTO appointment (medical_card_id, doctor_id, date) VALUES (?, ?, ?)")
        insertStatement.setInt(1, medCardResult.getInt(1))
        insertStatement.setInt(2, workerResult.getInt(1))
        insertStatement.setTimestamp(3, Timestamp(date.time))
        insertStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun createAppointmentResult(appointmentId: Int, price: Float, notes: String) = transactor.startTransaction {
        val insertStatement = prepareStatement("INSERT INTO appointment_result (notes, price) VALUES (?, ?) RETURNING id")
        insertStatement.setString(1, notes)
        insertStatement.setFloat(2, price)
        val insertResult = insertStatement.executeQuery()
        insertResult.next()

        val updateStatement = prepareStatement("UPDATE appointment SET result_id = ? WHERE id = ?")
        updateStatement.setInt(1, insertResult.getInt(1))
        updateStatement.setInt(2, appointmentId)
        updateStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun payForAppointment(appointmentResultId: Int, payedAmount: Float, payedAccount: String) = transactor.startTransaction {
        val insertStatement = prepareStatement("INSERT INTO payment (payed_amount, payed_account, time) VALUES (?, ?, ?) RETURNING id")
        insertStatement.setFloat(1, payedAmount)
        insertStatement.setString(2, payedAccount)
        insertStatement.setTimestamp(3, Timestamp(System.currentTimeMillis()))
        val insertResult = insertStatement.executeQuery()
        insertResult.next()

        val updateStatement = prepareStatement("UPDATE appointment_result SET payment_id = ? WHERE id = ?")
        updateStatement.setInt(1, insertResult.getInt(1))
        updateStatement.setInt(2, appointmentResultId)
        updateStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun updateAppointmentResult(resultId: Int, price: Float, notes: String) = transactor.startTransaction {
        val updateStatement = prepareStatement("UPDATE appointment_result SET price = ?, notes = ? WHERE id = ?")
        updateStatement.setFloat(1, price)
        updateStatement.setString(2, notes)
        updateStatement.setInt(3, resultId)
        updateStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun deleteAppointmentAndResult(appointmentId: Int, resultId: Int) = transactor.startTransaction {
        val deleteStatement1 = createStatement()
        deleteStatement1.executeUpdate("DELETE FROM appointment WHERE id = $appointmentId")

        val deleteStatement2 = createStatement()
        deleteStatement2.executeUpdate("DELETE FROM appointment_result WHERE id = $resultId")

        TransactorResult.Success("Success")
    }

    fun deleteAppointment(appointmentId: Int) = transactor.startTransaction {
        val deleteStatement1 = createStatement()
        deleteStatement1.executeUpdate("DELETE FROM appointment WHERE id = $appointmentId")

        TransactorResult.Success("Success")
    }
}