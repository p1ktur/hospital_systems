package app_doctor.data

import app_doctor.domain.model.*
import app_shared.domain.model.transactor.*

class FindDoctorRepository(private val transactor: ITransactor) {

    // Obsolete from client
//    fun search(searchText: String): TransactorResult = transactor.startTransaction {
//        val searchStatement = prepareStatement("SELECT name, surname, age, phone, login, user_client.id FROM patient " +
//                "INNER JOIN medical_card ON patient.id = medical_card.patient_id " +
//                "INNER JOIN user_client ON medical_card.id = user_client.medical_card_id " +
//                "INNER JOIN public.user on public.user.id = user_client.user_id " +
//                "WHERE LOWER(name) LIKE LOWER(?) OR LOWER(surname) LIKE LOWER(?) OR LOWER(phone) LIKE LOWER(?) OR LOWER(login) LIKE LOWER(?)")
//        searchStatement.setString(1, "%$searchText%")
//        searchStatement.setString(2, "%$searchText%")
//        searchStatement.setString(3, "%$searchText%")
//        searchStatement.setString(4, "%$searchText%")
//
//        val searchResult = searchStatement.executeQuery()
//
//        val clientSearchDataList = buildList {
//            while (searchResult.next()) add(
//                ClientSearchData(
//                    name = searchResult.getString(1),
//                    surname = searchResult.getString(2),
//                    age = searchResult.getInt(3),
//                    phone = searchResult.getString(4),
//                    login = searchResult.getString(5),
//                    userClientId = searchResult.getInt(6)
//                )
//            )
//        }
//
//        TransactorResult.Success(clientSearchDataList)
//    }

    fun search(): TransactorResult = transactor.startTransaction {
        val searchStatement = prepareStatement("SELECT name, surname, age, phone, position, salary, login, user_doctor.id FROM worker " +
                "LEFT JOIN user_doctor ON user_doctor.worker_id = worker.id " +
                "LEFT JOIN public.user ON public.user.id = user_doctor.user_id ")

        val searchResult = searchStatement.executeQuery()

        val doctorSearchDataList = buildList {
            while (searchResult.next()) add(
                DoctorSearchData(
                    name = searchResult.getString(1),
                    surname = searchResult.getString(2),
                    age = searchResult.getInt(3),
                    phone = searchResult.getString(4),
                    position = searchResult.getString(5),
                    salary = searchResult.getFloat(6),
                    login = searchResult.getString(7) ?: "",
                    userWorkerId = searchResult.getInt(8)
                )
            )
        }

        TransactorResult.Success(doctorSearchDataList.sortedBy { it.name })
    }
}