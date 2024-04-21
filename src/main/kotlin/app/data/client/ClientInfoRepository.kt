package app.data.client

import app.domain.database.transactor.*
import app.domain.model.client.*
import java.time.format.*

class ClientInfoRepository(private val transactor: ITransactor) {

    fun fetchInfo(userClientId: Int): TransactorResult = transactor.startTransaction {
        val userClientStatement = createStatement()
        val userClientResult = userClientStatement.executeQuery("SELECT user_id, medical_card_id FROM user_client WHERE id = $userClientId")
        userClientResult.next()

        val medCardStatement = prepareStatement("SELECT creation_date FROM medical_card WHERE id = ?")
        medCardStatement.setInt(1, userClientResult.getInt(2))
        val medCardResult = medCardStatement.executeQuery()
        medCardResult.next()

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
        val registrationDate = medCardResult.getTimestamp(1).toLocalDateTime().format(formatter)

        val hospitalizationStatement = prepareStatement("SELECT COUNT(end_date) FROM hospitalization WHERE medical_card_id = ? AND end_date IS NULL")
        hospitalizationStatement.setInt(1, userClientResult.getInt(2))
        val hospitalizationResult = hospitalizationStatement.executeQuery()

        val isHospitalized = if (hospitalizationResult.next()) {
            hospitalizationResult.getInt(1).run { this > 0 }
        } else {
            false
        }

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
        val visitedAppointmentsResult = visitedAppointmentsStatement.executeQuery()
        visitedAppointmentsResult.next()
        val visitedAppointments = visitedAppointmentsResult.getInt(1)

        val pendingPaymentsStatementA = prepareStatement("SELECT COUNT(*) FROM appointment " +
                "LEFT JOIN medical_card ON appointment.medical_card_id = medical_card.id " +
                "LEFT JOIN appointment_result ON appointment.result_id = appointment_result.id " +
                "LEFT JOIN payment ON appointment_result.payment_id = payment.id " +
                "WHERE medical_card.id = ? AND payment.payed_amount IS NULL OR payment.payed_amount = 0")
        pendingPaymentsStatementA.setInt(1, userClientResult.getInt(2))
        val pendingPaymentsResultA = pendingPaymentsStatementA.executeQuery()
        pendingPaymentsResultA.next()
        val pendingPaymentsA = pendingPaymentsResultA.getInt(1)

        val pendingPaymentsStatementH = prepareStatement("SELECT COUNT(*) FROM hospitalization " +
                "LEFT JOIN medical_card ON hospitalization.medical_card_id = medical_card.id " +
                "LEFT JOIN payment ON hospitalization.payment_id = payment.id " +
                "WHERE medical_card.id = ? AND payment.payed_amount IS NULL OR payment.payed_amount = 0")
        pendingPaymentsStatementH.setInt(1, userClientResult.getInt(2))
        val pendingPaymentsResultH = pendingPaymentsStatementH.executeQuery()
        pendingPaymentsResultH.next()
        val pendingPaymentsH = pendingPaymentsResultH.getInt(1)

        val pendingPaymentsStatement = prepareStatement("SELECT COUNT(*) FROM sub_payment " +
                "LEFT JOIN medical_card ON sub_payment.medical_card_id = medical_card.id " +
                "WHERE medical_card.id = ? AND payed_amount IS NULL OR payed_amount = 0")
        pendingPaymentsStatement.setInt(1, userClientResult.getInt(2))
        val pendingPaymentsResult = pendingPaymentsStatement.executeQuery()
        pendingPaymentsResult.next()
        val pendingPayments = pendingPaymentsResult.getInt(1)

        val payedPaymentsStatementA = prepareStatement("SELECT COUNT(*) FROM appointment " +
                "LEFT JOIN medical_card ON appointment.medical_card_id = medical_card.id " +
                "LEFT JOIN appointment_result ON appointment.result_id = appointment_result.id " +
                "LEFT JOIN payment ON appointment_result.payment_id = payment.id " +
                "WHERE medical_card.id = ? AND payment.payed_amount IS NOT NULL AND payment.payed_amount > 0")
        payedPaymentsStatementA.setInt(1, userClientResult.getInt(2))
        val payedPaymentsResultA = payedPaymentsStatementA.executeQuery()
        payedPaymentsResultA.next()
        val payedPaymentsA = payedPaymentsResultA.getInt(1)

        val payedPaymentsStatementH = prepareStatement("SELECT COUNT(*) FROM hospitalization " +
                "LEFT JOIN medical_card ON hospitalization.medical_card_id = medical_card.id " +
                "LEFT JOIN payment ON hospitalization.payment_id = payment.id " +
                "WHERE medical_card.id = ? AND payment.payed_amount IS NOT NULL AND payment.payed_amount > 0")
        payedPaymentsStatementH.setInt(1, userClientResult.getInt(2))
        val payedPaymentsResultH = payedPaymentsStatementH.executeQuery()
        payedPaymentsResultH.next()
        val payedPaymentsH = payedPaymentsResultH.getInt(1)

        val payedPaymentsStatement = prepareStatement("SELECT COUNT(*) FROM sub_payment " +
                "LEFT JOIN medical_card ON sub_payment.medical_card_id = medical_card.id " +
                "WHERE medical_card.id = ? AND payed_amount IS NOT NULL AND payed_amount > 0")
        payedPaymentsStatement.setInt(1, userClientResult.getInt(2))
        val payedPaymentsResult = payedPaymentsStatement.executeQuery()
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
            pendingPayments = pendingPaymentsA + pendingPaymentsH + pendingPayments,
            payedPayments = payedPaymentsA + payedPaymentsH + payedPayments
        )
        TransactorResult.Success(clientInfoData)
    }

    fun saveChanges(
        userClientId: Int,
        name: String,
        surname: String,
        fathersName: String,
        age: String,
        address: String,
        phone: String,
        email: String
    ): TransactorResult = transactor.startTransaction {
        val updateStatement = prepareStatement("UPDATE patient SET name = ?, surname = ?, fathers_name = ?, age = ?, address = ?, phone = ?, email = ? " +
                "FROM medical_card, user_client " +
                "WHERE user_client.id = ? AND user_client.medical_card_id = medical_card.id AND medical_card.patient_id = patient.id")
        updateStatement.setString(1, name)
        updateStatement.setString(2, surname)
        updateStatement.setString(3, fathersName)
        updateStatement.setInt(4, age.toInt())
        updateStatement.setString(5, address)
        updateStatement.setString(6, phone)
        updateStatement.setString(7, email)
        updateStatement.setInt(8, userClientId)
        updateStatement.executeUpdate()

        TransactorResult.Success(userClientId)
    }
}