package app_client.domain.uiState

data class ClientInfoUiState(
    val name: String = "",
    val surname: String = "",
    val fathersName: String = "",
    val age: Int = 0,
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val registrationDate: String = "",
    val pendingAppointments: Int = 0,
    val visitedAppointments: Int = 0,
    val isHospitalized: Boolean = false,
    val pendingPayments: Int = 0,
    val payedPayments: Int = 0,
    val isLoading: Boolean = false
)