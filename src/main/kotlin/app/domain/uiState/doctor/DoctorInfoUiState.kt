package app.domain.uiState.doctor

import app.domain.model.shared.room.*

data class DoctorInfoUiState(
    val editMode: Boolean = false,
    val name: String = "",
    val surname: String = "",
    val fathersName: String = "",
    val age: String = "0",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val position: String = "",
    val salary: String = "0",
    val registrationDate: String = "",
    val designationName: String = "",
    val designationFloor: String = "0",
    val designationNumber: String = "0",
    val pendingAppointments: Int = 0,
    val finishedAppointments: Int = 0,
    val startTime: String = "",
    val endTime: String = "",
    val startDay: String = "",
    val endDay: String = "",
    val hoursForRest: String = "0",
    val isLoading: Boolean = false,
    val errorCodes: List<Int> = emptyList(),
    val preloadedRooms: List<Room> = emptyList(),
    val designationIndex: Int = -1
)