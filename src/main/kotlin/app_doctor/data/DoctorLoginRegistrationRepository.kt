package app_doctor.data

import app_shared.domain.model.exceptions.*
import app_shared.domain.model.transactor.*

class DoctorLoginRegistrationRepository(private val transactor: ITransactor) {

    fun login(
        login: String,
        password: String
    ): TransactorResult = transactor.startTransaction(
        transaction = {
            val checkStatement = prepareStatement("SELECT user_doctor.id FROM public.user INNER JOIN user_doctor ON public.user.id = user_doctor.user_id WHERE public.user.login = ? AND public.user.password = ?")
            checkStatement.setString(1, login)
            checkStatement.setString(2, password)

            val checkResult = checkStatement.executeQuery()

            if (checkResult.next()) {
                TransactorResult.Success(checkResult.getInt(1))
            } else {
                TransactorResult.Failure(WrongCredentialsException(1013, "Wrong credentials"))
            }
        }
    )
}