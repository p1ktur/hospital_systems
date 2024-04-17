package app_admin.data

import app.domain.database.transactor.*
import app.domain.util.exceptions.*

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
        canReceiveAppointments: Boolean,
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

            val registerStatement = prepareStatement("SELECT registerWorker(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
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
            registerStatement.setBoolean(12, canReceiveAppointments)
            val registerResult = registerStatement.executeQuery()
            registerResult.next()

            TransactorResult.Success(registerResult.getInt(1))
        }
    )
}