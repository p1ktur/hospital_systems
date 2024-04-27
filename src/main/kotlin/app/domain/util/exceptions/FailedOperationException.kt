package app.domain.util.exceptions

class FailedOperationException(val code: Int, message: String? = null) : Exception(message)