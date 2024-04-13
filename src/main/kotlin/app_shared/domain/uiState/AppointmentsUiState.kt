package app_shared.domain.uiState

import app_shared.domain.model.database.dbModels.*

data class AppointmentsUiState(
    val editMode: Boolean = false,
    val appointments: List<Appointment> = emptyList(),
    val results: List<AppointmentResult> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val isLoading: Boolean = false,
    val errorCodes: List<Int> = emptyList(),
    val showInfoDialog: Boolean = false,
    val showDateTimePickerDialog: Boolean = false,
    val userWorkerIdForAppointment: Int? = null,
    val userClientIdForAppointment: Int? = null
)
