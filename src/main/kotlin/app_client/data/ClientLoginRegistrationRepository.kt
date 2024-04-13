package app_client.data

import app_shared.domain.model.exceptions.*
import app_shared.domain.model.database.transactor.*

class ClientLoginRegistrationRepository(private val transactor: ITransactor) {

    fun register(
        name: String,
        surname: String,
        fathersName: String,
        age: String,
        address: String,
        phone: String,
        email: String,
        login: String,
        password: String
    ): TransactorResult = transactor.startTransaction {
        val checkStatement = prepareStatement("SELECT COUNT(login) FROM public.user WHERE login = ?")
        checkStatement.setString(1, login)

        val checkResult = checkStatement.executeQuery()
        checkResult.next()
        if (checkResult.getInt(1) != 0) {
            return@startTransaction TransactorResult.Failure(AlreadyExistsException(1012, "Login is occupied"))
        }

        val registerStatement = prepareStatement("SELECT registerPatient(?, ?, ?, ?, ?, ?, ?, ?, ?)")
        registerStatement.setString(1, name)
        registerStatement.setString(2, surname)
        registerStatement.setString(3, fathersName)
        registerStatement.setInt(4, age.toInt())
        registerStatement.setString(5, address)
        registerStatement.setString(6, phone)
        registerStatement.setString(7, email)
        registerStatement.setString(8, login)
        registerStatement.setString(9, password)
        val registerResult = registerStatement.executeQuery()
        registerResult.next()

        TransactorResult.Success(registerResult.getInt(1))
    }


    fun login(login: String, password: String): TransactorResult = transactor.startTransaction {
        val checkStatement = prepareStatement("SELECT user_client.id FROM public.user INNER JOIN user_client ON public.user.id = user_client.user_id WHERE public.user.login = ? AND public.user.password = ?")
        checkStatement.setString(1, login)
        checkStatement.setString(2, password)

        val checkResult = checkStatement.executeQuery()

        if (checkResult.next()) {
            TransactorResult.Success(checkResult.getInt(1))
        } else {
            TransactorResult.Failure(WrongCredentialsException(1013, "Wrong credentials"))
        }
    }
}