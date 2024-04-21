package app.data.shared

import app.domain.database.transactor.*
import app.domain.model.shared.payment.*
import java.sql.*
import java.time.format.*

class PaymentsRepository(private val transactor: ITransactor) {

    fun fetchPaymentsForDoctorOrAdmin() = transactor.startTransaction {
        val paymentsStatementA = prepareStatement("SELECT payment.id, payed_amount, payed_account, time, patient.name, login, user_client.id, appointment.id FROM appointment " +
                "INNER JOIN appointment_result ON appointment.result_id = appointment_result.id " +
                "INNER JOIN payment ON appointment_result.payment_id = payment.id " +
                "INNER JOIN medical_card ON appointment.medical_card_id = medical_card.id " +
                "INNER JOIN patient ON medical_card.patient_id = patient.id " +
                "INNER JOIN user_client ON user_client.medical_card_id = medical_card.id " +
                "INNER JOIN public.user ON user_client.user_id = public.user.id ")
        val paymentsResultA = paymentsStatementA.executeQuery()

        val paymentsStatementH = prepareStatement("SELECT payment.id, payed_amount, payed_account, time, patient.name, login, user_client.id, hospitalization.id FROM hospitalization " +
                "INNER JOIN payment ON hospitalization.payment_id = payment.id " +
                "INNER JOIN medical_card ON hospitalization.medical_card_id = medical_card.id " +
                "INNER JOIN patient ON medical_card.patient_id = patient.id " +
                "INNER JOIN user_client ON user_client.medical_card_id = medical_card.id " +
                "INNER JOIN public.user ON user_client.user_id = public.user.id")
        val paymentsResultH = paymentsStatementH.executeQuery()

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
        val payments = mutableListOf<Payment.Default>()

        while (paymentsResultA.next()) payments.add(
            Payment.Default(
                id = paymentsResultA.getInt(1),
                payedAmount = paymentsResultA.getFloat(2),
                payedAccount = paymentsResultA.getString(3),
                time = paymentsResultA.getTimestamp(4).toLocalDateTime().format(formatter),
                clientName = paymentsResultA.getString(5),
                clientLogin = paymentsResultA.getString(6),
                userClientId = paymentsResultA.getInt(7),
                helpIdType = 0,
                helpId = paymentsResultA.getInt(8)
            )
        )

        while (paymentsResultH.next()) payments.add(
            Payment.Default(
                id = paymentsResultH.getInt(1),
                payedAmount = paymentsResultH.getFloat(2),
                payedAccount = paymentsResultH.getString(3),
                time = paymentsResultH.getTimestamp(4).toLocalDateTime().format(formatter),
                clientName = paymentsResultH.getString(5),
                clientLogin = paymentsResultH.getString(6),
                userClientId = paymentsResultH.getInt(7),
                helpIdType = 1,
                helpId = paymentsResultH.getInt(8)
            )
        )

        val subPaymentsStatement = prepareStatement("SELECT sub_payment.id, payed_amount, payed_account, time, subject, to_pay_amount, sub_payment.medical_card_id, patient.name, login, user_client.id " +
                "FROM sub_payment " +
                "INNER JOIN medical_card ON sub_payment.medical_card_id = medical_card.id " +
                "INNER JOIN patient ON medical_card.patient_id = patient.id " +
                "INNER JOIN user_client ON user_client.medical_card_id = medical_card.id " +
                "INNER JOIN public.user ON user_client.user_id = public.user.id")
        val subPaymentsResult = subPaymentsStatement.executeQuery()

        val subPayments = buildList {
            while (subPaymentsResult.next()) add(
                Payment.Sub(
                    id = subPaymentsResult.getInt(1),
                    payedAmount = subPaymentsResult.getFloat(2),
                    payedAccount = subPaymentsResult.getString(3) ?: "",
                    time = subPaymentsResult.getTimestamp(4)?.toLocalDateTime()?.format(formatter) ?: "",
                    clientName = subPaymentsResult.getString(8),
                    clientLogin = subPaymentsResult.getString(9),
                    userClientId = subPaymentsResult.getInt(10),
                    medicalCardId = subPaymentsResult.getInt(7),
                    subject = subPaymentsResult.getString(5),
                    toPayAmount = subPaymentsResult.getFloat(6)
                )
            )
        }

        val fetchPaymentDate = FetchPaymentData(
            payments = payments,
            subPayments = subPayments
        )
        TransactorResult.Success(fetchPaymentDate)
    }

    fun fetchPaymentsForClient(userClientId: Int) = transactor.startTransaction {
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

        val paymentsStatementA = prepareStatement("SELECT payment.id, payed_amount, payed_account, time, appointment.id FROM appointment " +
                "INNER JOIN appointment_result ON appointment.result_id = appointment_result.id " +
                "INNER JOIN payment ON appointment_result.payment_id = payment.id " +
                "WHERE medical_card_id = ?")
        paymentsStatementA.setInt(1, medCardId)
        val paymentsResultA = paymentsStatementA.executeQuery()

        val paymentsStatementH = prepareStatement("SELECT payment.id, payed_amount, payed_account, time, hospitalization.id FROM hospitalization " +
                "INNER JOIN payment ON hospitalization.payment_id = payment.id " +
                "WHERE medical_card_id = ?")
        paymentsStatementH.setInt(1, medCardId)
        val paymentsResultH = paymentsStatementH.executeQuery()

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
        val payments = mutableListOf<Payment.Default>()

        while (paymentsResultA.next()) payments.add(
            Payment.Default(
                id = paymentsResultA.getInt(1),
                payedAmount = paymentsResultA.getFloat(2),
                payedAccount = paymentsResultA.getString(3),
                time = paymentsResultA.getTimestamp(4).toLocalDateTime().format(formatter),
                clientName = clientName,
                clientLogin = clientLogin,
                userClientId = userClientId,
                helpIdType = 0,
                helpId = paymentsResultA.getInt(5)
            )
        )

        while (paymentsResultH.next()) payments.add(
            Payment.Default(
                id = paymentsResultH.getInt(1),
                payedAmount = paymentsResultH.getFloat(2),
                payedAccount = paymentsResultH.getString(3),
                time = paymentsResultH.getTimestamp(4).toLocalDateTime().format(formatter),
                clientName = clientName,
                clientLogin = clientLogin,
                userClientId = userClientId,
                helpIdType = 1,
                helpId = paymentsResultH.getInt(5)
            )
        )

        val subPaymentsStatement = prepareStatement("SELECT id, payed_amount, payed_account, time, subject, to_pay_amount FROM sub_payment " +
                "WHERE medical_card_id = ?")
        subPaymentsStatement.setInt(1, medCardId)
        val subPaymentsResult = subPaymentsStatement.executeQuery()

        val subPayments = buildList {
            while (subPaymentsResult.next()) add(
                Payment.Sub(
                    id = subPaymentsResult.getInt(1),
                    payedAmount = subPaymentsResult.getFloat(2),
                    payedAccount = subPaymentsResult.getString(3) ?: "",
                    time = subPaymentsResult.getTimestamp(4)?.toLocalDateTime()?.format(formatter) ?: "",
                    clientName = clientName,
                    clientLogin = clientLogin,
                    userClientId = userClientId,
                    medicalCardId = medCardId,
                    subject = subPaymentsResult.getString(5),
                    toPayAmount = subPaymentsResult.getFloat(6)
                )
            )
        }

        val fetchPaymentDate = FetchPaymentData(
            payments = payments,
            subPayments = subPayments
        )
        TransactorResult.Success(fetchPaymentDate)
    }

    fun createSubPayment(userClientId: Int, subject: String, amount: Float) = transactor.startTransaction {
        val medCardStatement = createStatement()
        val medCardResult = medCardStatement.executeQuery("SELECT medical_card.id FROM user_client " +
                "INNER JOIN medical_card ON medical_card.id = user_client.medical_card_id " +
                "WHERE user_client.id = $userClientId")
        medCardResult.next()

        val insertStatement = prepareStatement("INSERT INTO sub_payment (medical_card_id, subject, to_pay_amount) VALUES (?, ?, ?)")
        insertStatement.setInt(1, medCardResult.getInt(1))
        insertStatement.setString(2, subject)
        insertStatement.setFloat(3, amount)
        insertStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun payForSubPayment(subPaymentId: Int, payedAmount: Float, payedAccount: String) = transactor.startTransaction {
        val insertStatement = prepareStatement("UPDATE sub_payment SET payed_amount = ?, payed_account = ?, time = ? WHERE id = ?")
        insertStatement.setFloat(1, payedAmount)
        insertStatement.setString(2, payedAccount)
        insertStatement.setTimestamp(3, Timestamp(System.currentTimeMillis()))
        insertStatement.setInt(4, subPaymentId)
        insertStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun updateSubPayment(subPaymentId: Int, subject: String, amount: Float) = transactor.startTransaction {
        val updateStatement = prepareStatement("UPDATE sub_payment SET subject = ?, to_pay_amount = ? WHERE id = ?")
        updateStatement.setString(1, subject)
        updateStatement.setFloat(2, amount)
        updateStatement.setInt(3, subPaymentId)
        updateStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun deleteSubPayment(subPaymentId: Int) = transactor.startTransaction {
        val deleteStatement = createStatement()
        deleteStatement.executeUpdate("DELETE FROM sub_payment WHERE id = $subPaymentId")

        TransactorResult.Success("Success")
    }
}