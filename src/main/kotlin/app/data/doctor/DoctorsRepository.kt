package app.data.doctor

import app.domain.database.transactor.*
import app.domain.model.doctor.*

class DoctorsRepository(private val transactor: ITransactor) {

    fun search(all: Boolean): TransactorResult = transactor.startTransaction {
        val searchStatement = if (all) {
            prepareStatement("SELECT name, surname, age, phone, position, salary, login, user_doctor.id FROM worker " +
                    "LEFT JOIN user_doctor ON user_doctor.worker_id = worker.id " +
                    "LEFT JOIN public.user ON public.user.id = user_doctor.user_id")
        } else {
            prepareStatement("SELECT name, surname, age, phone, position, salary, login, user_doctor.id FROM worker " +
                    "LEFT JOIN user_doctor ON user_doctor.worker_id = worker.id " +
                    "LEFT JOIN public.user ON public.user.id = user_doctor.user_id " +
                    "WHERE can_receive_appointments = TRUE")
        }
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