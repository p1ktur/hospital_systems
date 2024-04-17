package app_shared.domain.model.util.exceptions

class AlreadyExistsException(val code: Int, message: String? = null) : Exception(message)