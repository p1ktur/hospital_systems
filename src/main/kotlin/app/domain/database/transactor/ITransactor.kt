package app.domain.database.transactor

import java.sql.*

interface ITransactor {

    fun checkStatus()

    fun startTransaction(
        onSQLException: ((SQLException) -> Unit)? = null,
        onException: ((Exception) -> Unit)? = null,
        transaction: Connection.() -> TransactorResult
    ): TransactorResult

    suspend fun startSuspendTransaction(
        onSQLException: (suspend (SQLException) -> Unit)? = null,
        onException: (suspend (Exception) -> Unit)? = null,
        transaction: suspend Connection.() -> TransactorResult
    ): TransactorResult
}