package app.data.shared

import app.domain.database.transactor.*
import app.domain.model.shared.hospitalization.*
import app.domain.model.shared.payment.*
import java.sql.*
import java.time.format.*

class HospitalizationsRepository(private val transactor: ITransactor) {

    fun fetchHospitalizationsForDoctorOrAdmin() = transactor.startTransaction {
        val hospitalizationsStatement = prepareStatement("SELECT hospitalization.id, reason, price, start_date, end_date, payment_id, patient.name, login, user_client.id " +
                "FROM hospitalization " +
                "INNER JOIN medical_card ON hospitalization.medical_card_id = medical_card.id " +
                "INNER JOIN patient ON medical_card.patient_id = patient.id " +
                "INNER JOIN user_client ON user_client.medical_card_id = medical_card.id " +
                "INNER JOIN public.user ON user_client.user_id = public.user.id ")
        val hospitalizationsResult = hospitalizationsStatement.executeQuery()

        val hospitalizations = buildList {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
            while (hospitalizationsResult.next()) add(
                Hospitalization(
                    id = hospitalizationsResult.getInt(1),
                    clientName = hospitalizationsResult.getString(7),
                    clientLogin = hospitalizationsResult.getString(8),
                    userClientId = hospitalizationsResult.getInt(9),
                    reason = hospitalizationsResult.getString(2),
                    startDate = hospitalizationsResult.getTimestamp(4).toLocalDateTime().format(formatter),
                    endDate = hospitalizationsResult.getTimestamp(5)?.toLocalDateTime()?.format(formatter),
                    paymentId = hospitalizationsResult.getInt(6),
                    price = hospitalizationsResult.getFloat(3),
                )
            )
        }

        val paymentsStatement = prepareStatement("SELECT payment.id, payed_amount, payed_account, time FROM hospitalization " +
                "INNER JOIN payment ON hospitalization.payment_id = payment.id ")
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

        val fetchHospitalizationDate = FetchHospitalizationData(
            hospitalizations = hospitalizations.sortedByDescending { it.id },
            payments = payments
        )
        TransactorResult.Success(fetchHospitalizationDate)
    }

    fun fetchHospitalizationsForClient(userClientId: Int) = transactor.startTransaction {
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

        val hospitalizationsStatement = prepareStatement("SELECT hospitalization.id, reason, price, start_date, end_date, payment_id FROM hospitalization " +
                "INNER JOIN medical_card ON hospitalization.medical_card_id = medical_card.id " +
                "INNER JOIN user_client ON user_client.medical_card_id = medical_card.id " +
                "WHERE hospitalization.medical_card_id = ?")
        hospitalizationsStatement.setInt(1, medCardId)
        val hospitalizationsResult = hospitalizationsStatement.executeQuery()

        val hospitalizations = buildList {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
            while (hospitalizationsResult.next()) add(
                Hospitalization(
                    id = hospitalizationsResult.getInt(1),
                    clientName = clientName,
                    clientLogin = clientLogin,
                    userClientId = userClientId,
                    reason = hospitalizationsResult.getString(2),
                    startDate = hospitalizationsResult.getTimestamp(4).toLocalDateTime().format(formatter),
                    endDate = hospitalizationsResult.getTimestamp(5)?.toLocalDateTime()?.format(formatter),
                    paymentId = hospitalizationsResult.getInt(6),
                    price = hospitalizationsResult.getFloat(3),
                )
            )
        }

        val paymentsStatement = prepareStatement("SELECT payment.id, payed_amount, payed_account, time FROM hospitalization " +
                "INNER JOIN payment ON hospitalization.payment_id = payment.id " +
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

        val fetchHospitalizationDate = FetchHospitalizationData(
            hospitalizations = hospitalizations.sortedByDescending { it.id },
            payments = payments
        )
        TransactorResult.Success(fetchHospitalizationDate)
    }

    fun createHospitalization(userClientId: Int, roomId: Int, reason: String, price: Float) = transactor.startTransaction {
        val medCardStatement = createStatement()
        val medCardResult = medCardStatement.executeQuery("SELECT medical_card.id FROM user_client " +
                "INNER JOIN medical_card ON medical_card.id = user_client.medical_card_id " +
                "WHERE user_client.id = $userClientId")
        medCardResult.next()

        val insertStatement = prepareStatement("INSERT INTO hospitalization (medical_card_id, room_id, reason, price, start_date) VALUES (?, ?, ?, ?, ?)")
        insertStatement.setInt(1, medCardResult.getInt(1))
        insertStatement.setInt(2, roomId)
        insertStatement.setString(3, reason)
        insertStatement.setFloat(4, price)
        insertStatement.setTimestamp(5, Timestamp(System.currentTimeMillis()))
        insertStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun payForHospitalization(hospitalizationId: Int, payedAmount: Float, payedAccount: String) = transactor.startTransaction {
        val insertStatement = prepareStatement("INSERT INTO payment (payed_amount, payed_account, time) VALUES (?, ?, ?) RETURNING id")
        insertStatement.setFloat(1, payedAmount)
        insertStatement.setString(2, payedAccount)
        insertStatement.setTimestamp(3, Timestamp(System.currentTimeMillis()))
        val insertResult = insertStatement.executeQuery()
        insertResult.next()

        val updateStatement = prepareStatement("UPDATE hospitalization SET payment_id = ? WHERE id = ?")
        updateStatement.setInt(1, insertResult.getInt(1))
        updateStatement.setInt(2, hospitalizationId)
        updateStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun updateHospitalization(hospitalizationId: Int, reason: String, price: Float) = transactor.startTransaction {
        val updateStatement = prepareStatement("UPDATE hospitalization SET reason = ?, price = ? WHERE id = ?")
        updateStatement.setString(1, reason)
        updateStatement.setFloat(2, price)
        updateStatement.setInt(3, hospitalizationId)
        updateStatement.executeUpdate()

        TransactorResult.Success("Success")
    }

    fun endHospitalization(hospitalizationId: Int) = transactor.startTransaction {
        val updateStatement = createStatement()
        updateStatement.executeUpdate("UPDATE hospitalization SET end_date = NOW() WHERE id = $hospitalizationId")

        TransactorResult.Success("Success")
    }

    fun deleteHospitalization(hospitalizationId: Int) = transactor.startTransaction {
        val deleteStatement = createStatement()
        deleteStatement.executeUpdate("DELETE FROM hospitalization WHERE id = $hospitalizationId")

        TransactorResult.Success("Success")
    }
}