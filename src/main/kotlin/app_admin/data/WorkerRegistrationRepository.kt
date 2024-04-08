package app_admin.data

import app_shared.domain.model.exceptions.*
import app_shared.domain.model.transactor.*

class WorkerRegistrationRepository(private val transactor: ITransactor) {

    fun register(
        name: String,
        surname: String,
        fathersName: String,
        age: String,
        address: String,
        phone: String,
        position: String,
        salary: String,
        email: String,
        login: String,
        password: String
    ): TransactorResult = transactor.startTransaction(
        transaction = {
            val checkStatement = prepareStatement("SELECT COUNT(login) FROM public.user WHERE login = ?")
            checkStatement.setString(1, login)

            val checkResult = checkStatement.executeQuery()
            checkResult.next()
            if (checkResult.getInt(1) != 0) {
                return@startTransaction TransactorResult.Failure(AlreadyExistsException(1012, "Login is occupied"))
            }

            val registerStatement = prepareStatement("SELECT registerWorker(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
            registerStatement.setString(1, name)
            registerStatement.setString(2, surname)
            registerStatement.setString(3, fathersName)
            registerStatement.setInt(4, age.toInt())
            registerStatement.setString(5, address)
            registerStatement.setString(6, phone)
            registerStatement.setString(7, email)
            registerStatement.setString(8, position)
            registerStatement.setFloat(9, if (salary.isBlank()) 0f else salary.toFloat())
            registerStatement.setString(10, login)
            registerStatement.setString(11, password)
            registerStatement.executeQuery()

            TransactorResult.Success("Success")
        }
    )

    fun register(
        name: String,
        surname: String,
        fathersName: String,
        age: String,
        address: String,
        phone: String,
        position: String,
        salary: String,
        email: String
    ): TransactorResult = transactor.startTransaction(
        transaction = {
            val registerStatement = prepareStatement("INSERT INTO worker (name, surname, fathers_name, age, address, phone, email, position, salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
            registerStatement.setString(1, name)
            registerStatement.setString(2, surname)
            registerStatement.setString(3, fathersName)
            registerStatement.setInt(4, age.toInt())
            registerStatement.setString(5, address)
            registerStatement.setString(6, phone)
            registerStatement.setString(7, email)
            registerStatement.setString(8, position)
            registerStatement.setFloat(9, if (salary.isBlank()) 0f else salary.toFloat())
            registerStatement.executeUpdate()

            TransactorResult.Success("Success")
        }
    )
}