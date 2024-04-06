package app_shared.data

import app_shared.domain.model.args.*
import app_shared.domain.model.transactor.*
import java.sql.*

class HospitalDatabase : AutoCloseable, ITransactor {

    private lateinit var connection: Connection
    private var connectionInitializationException: SQLException? = null

    fun init(appArgs: AppArgs) {
        val url = "jdbc:postgresql://localhost:5433/Hospital"
        val (user, password) = when (appArgs) {
            AppArgs.CLIENT -> "client" to "client"
            AppArgs.DOCTOR -> "doctor" to "doctor"
            AppArgs.ADMIN -> "admin" to "admin"
        }

        try {
            connection = DriverManager.getConnection(url, user, password)
            connection.autoCommit = false
            connection.transactionIsolation = Connection.TRANSACTION_SERIALIZABLE
        } catch (e: SQLException) {
            connectionInitializationException = e
        }
    }

    override fun close() {
        connection.close()
    }

    override fun checkStatus() {
        if (connectionInitializationException != null) {
            println("Error during initialization: ${connectionInitializationException?.message}")
        } else {
            println("Connection is initialized")
        }
    }

    override fun <T> startTransaction(
        transaction: Connection.() -> TransactorResult.Success<T>,
        onSQLException: ((SQLException) -> Unit)?,
        onException: ((Exception) -> Unit)?
    ): TransactorResult = try {
        connection.transaction()
    } catch (e: SQLException) {
        e.printStackTrace()
        onSQLException?.invoke(e)

        try {
            connection.rollback()
        } catch (e: SQLException) {
            onSQLException?.invoke(e)
        }

        TransactorResult.Failure(e)
    } catch (e: Exception) {
        e.printStackTrace()
        onException?.invoke(e)

        TransactorResult.Failure(e)
    }
}