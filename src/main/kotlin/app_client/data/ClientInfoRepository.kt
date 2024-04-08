package app_client.data

import app_client.domain.model.*
import app_shared.domain.model.transactor.*
import java.time.format.*

class ClientInfoRepository(private val transactor: ITransactor) {

    fun fetchInfo(userClientId: Int): TransactorResult = transactor.startTransaction {
        val userClientStatement = createStatement()
        val userClientResult = userClientStatement.executeQuery("SELECT user_id, medical_card_id FROM user_client WHERE id = $userClientId")
        userClientResult.next()

        val medCardStatement = prepareStatement("SELECT creation_date, hospitalization_id FROM medical_card WHERE id = ?")
        medCardStatement.setInt(1, userClientResult.getInt(2))
        val medCardResult = medCardStatement.executeQuery()
        medCardResult.next()

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")
        val registrationDate = medCardResult.getTimestamp(1).toLocalDateTime().format(formatter)

        val isHospitalized = medCardResult.getIntOrNull(2).run { this != null }

        val pendingAppointmentsStatement = prepareStatement("SELECT COUNT(*) FROM appointment " +
                "LEFT JOIN medical_card ON appointment.medical_card_id = medical_card.id " +
                "WHERE medical_card.id = ? AND appointment.result_id IS NULL")
        pendingAppointmentsStatement.setInt(1, userClientResult.getInt(2))
        val pendingAppointmentsResult = pendingAppointmentsStatement.executeQuery()
        pendingAppointmentsResult.next()
        val pendingAppointments = pendingAppointmentsResult.getInt(1)

        val visitedAppointmentsStatement = prepareStatement("SELECT COUNT(*) FROM appointment " +
                "LEFT JOIN medical_card ON appointment.medical_card_id = medical_card.id" +
                " WHERE medical_card.id = ? AND appointment.result_id IS NOT NULL")
        visitedAppointmentsStatement.setInt(1, userClientResult.getInt(2))
        val visitedAppointmentsResult = pendingAppointmentsStatement.executeQuery()
        visitedAppointmentsResult.next()
        val visitedAppointments = visitedAppointmentsResult.getInt(1)

        val pendingPaymentsStatement = prepareStatement("SELECT COUNT(*) FROM appointment " +
                "LEFT JOIN medical_card ON appointment.medical_card_id = medical_card.id " +
                "LEFT JOIN appointment_result ON appointment.result_id = appointment_result.id " +
                "LEFT JOIN payment ON appointment_result.payment_id = payment.id " +
                "WHERE medical_card.id = ? AND payment.payed_amount IS NULL OR payment.payed_amount = 0")
        pendingPaymentsStatement.setInt(1, userClientResult.getInt(2))
        val pendingPaymentsResult = pendingAppointmentsStatement.executeQuery()
        pendingPaymentsResult.next()
        val pendingPayments = pendingPaymentsResult.getInt(1)

        val payedPaymentsStatement = prepareStatement("SELECT COUNT(*) FROM appointment " +
                "LEFT JOIN medical_card ON appointment.medical_card_id = medical_card.id " +
                "LEFT JOIN appointment_result ON appointment.result_id = appointment_result.id " +
                "LEFT JOIN payment ON appointment_result.payment_id = payment.id " +
                "WHERE medical_card.id = ? AND payment.payed_amount IS NOT NULL AND payment.payed_amount > 0")
        payedPaymentsStatement.setInt(1, userClientResult.getInt(2))
        val payedPaymentsResult = pendingAppointmentsStatement.executeQuery()
        payedPaymentsResult.next()
        val payedPayments = payedPaymentsResult.getInt(1)

        val patientInfoStatement = prepareStatement("SELECT name, surname, fathers_name, age, address, phone, email FROM patient " +
                "LEFT JOIN medical_card ON medical_card.patient_id = patient.id " +
                "WHERE medical_card.id = ?")
        patientInfoStatement.setInt(1, userClientResult.getInt(2))
        val patientInfoResult = patientInfoStatement.executeQuery()
        patientInfoResult.next()

        val clientInfoData = ClientInfoData(
            name = patientInfoResult.getString(1),
            surname = patientInfoResult.getString(2),
            fathersName = patientInfoResult.getString(3),
            age = patientInfoResult.getInt(4),
            address = patientInfoResult.getString(5),
            phone = patientInfoResult.getString(6),
            email = patientInfoResult.getString(7),
            registrationDate = registrationDate,
            pendingAppointments = pendingAppointments,
            visitedAppointments = visitedAppointments,
            isHospitalized = isHospitalized,
            pendingPayments = pendingPayments,
            payedPayments = payedPayments
        )
        TransactorResult.Success(clientInfoData)
    }
}