package app.domain.util.exceptions

class WrongCredentialsException(val code: Int, message: String? = null) : Exception(message)