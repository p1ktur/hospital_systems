package app_shared.domain.model.exceptions

class AlreadyExistsException(val code: Int, message: String? = null) : Exception(message)