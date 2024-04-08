package app_shared.domain.model.exceptions

class WrongCredentialsException(val code: Int, message: String? = null) : Exception(message)