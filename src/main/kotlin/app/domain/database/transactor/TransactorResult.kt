package app.domain.database.transactor

import java.lang.Exception

sealed class TransactorResult {
    data class Success<T>(val data: T) : TransactorResult()
    data class Failure(val exception: Exception? = null) : TransactorResult()
}