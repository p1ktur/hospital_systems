package app.domain.uiState.shared

import app.domain.model.doctor.*
import app.domain.model.shared.appointment.*
import app.domain.model.shared.payment.*
import java.time.LocalDateTime

data class AppointmentsUiState(
    val editMode: Boolean = false,
    val appointments: List<Appointment> = emptyList(),
    val results: List<AppointmentResult> = emptyList(),
    val payments: List<Payment.Default> = emptyList(),
    val scheduleData: DoctorScheduleData? = null,
    val busyFutureDates: List<LocalDateTime> = emptyList(),
    val isLoading: Boolean = false,
    val errorCodes: List<Int> = emptyList(),
    val showInfoDialog: Boolean = false,
    val showDateTimePickerDialog: Boolean = false,
    val userWorkerIdForAppointment: Int? = null,
    val userClientIdForAppointment: Int? = null,
    val openId: Int? = null
)
