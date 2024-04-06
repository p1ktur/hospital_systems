package app_shared.domain.model.transactor

import java.sql.*

interface ITransactor {

    fun checkStatus()

    fun <T> startTransaction(
        transaction: Connection.() -> TransactorResult.Success<T>,
        onSQLException: ((SQLException) -> Unit)? = null,
        onException: ((Exception) -> Unit)? = null
    ): TransactorResult
}