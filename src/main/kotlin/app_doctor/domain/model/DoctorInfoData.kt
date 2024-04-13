package app_doctor.domain.model

import app_shared.domain.model.database.dbModels.*

data class DoctorInfoData(
    val name: String = "",
    val surname: String = "",
    val fathersName: String = "",
    val age: Int = 0,
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val position: String = "",
    val salary: Float = 0f,
    val designationName: String = "",
    val designationFloor: Int = 0,
    val designationNumber: Int = 0,
    val pendingAppointments: Int = 0,
    val finishedAppointments: Int = 0,
    val preloadedRooms: List<Room> = emptyList(),
    val designationIndex: Int = 0
)
