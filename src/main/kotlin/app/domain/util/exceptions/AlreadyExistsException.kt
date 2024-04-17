package app.domain.util.exceptions

class AlreadyExistsException(val code: Int, message: String? = null) : Exception(message)