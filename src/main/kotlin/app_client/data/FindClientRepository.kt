package app_client.data

import app_client.domain.model.*
import app_shared.domain.model.transactor.*

class FindClientRepository(private val transactor: ITransactor) {

    fun search(): TransactorResult = transactor.startTransaction {
        val searchStatement = prepareStatement("SELECT name, surname, age, phone, login, user_client.id FROM patient " +
                "INNER JOIN medical_card ON patient.id = medical_card.patient_id " +
                "INNER JOIN user_client ON medical_card.id = user_client.medical_card_id " +
                "INNER JOIN public.user on public.user.id = user_client.user_id")

        val searchResult = searchStatement.executeQuery()

        val clientSearchDataList = buildList {
            while (searchResult.next()) add(
                ClientSearchData(
                    name = searchResult.getString(1),
                    surname = searchResult.getString(2),
                    age = searchResult.getInt(3),
                    phone = searchResult.getString(4),
                    login = searchResult.getString(5),
                    userClientId = searchResult.getInt(6)
                )
            )
        }

        TransactorResult.Success(clientSearchDataList.sortedBy { it.name })
    }
}