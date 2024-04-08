package app_shared.domain.model.transactor

import java.sql.*

interface ITransactor {

    fun checkStatus()

    fun startTransaction(
        onSQLException: ((SQLException) -> Unit)? = null,
        onException: ((Exception) -> Unit)? = null,
        transaction: Connection.() -> TransactorResult
    ): TransactorResult
}