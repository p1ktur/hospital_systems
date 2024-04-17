package app_shared.domain.model.util.exceptions

class WrongCredentialsException(val code: Int, message: String? = null) : Exception(message)