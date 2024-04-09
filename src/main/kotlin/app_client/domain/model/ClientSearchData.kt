package app_client.domain.model

data class ClientSearchData(
    val name: String = "",
    val surname: String = "",
    val age: Int = 0,
    val phone: String = "",
    val login: String = "",
    val userClientId: Int = -1
)
