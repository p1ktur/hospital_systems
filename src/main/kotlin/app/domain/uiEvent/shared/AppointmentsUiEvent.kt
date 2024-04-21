package app.domain.uiEvent.shared

import java.time.LocalDateTime

sealed class AppointmentsUiEvent {
    data class FetchAppointmentsForAdmin(val openId: Int?) : AppointmentsUiEvent()
    data class FetchAppointmentsForDoctor(val userWorkerId: Int, val openId: Int?) : AppointmentsUiEvent()
    data class FetchAppointmentsForClient(val userClientId: Int, val openId: Int?) : AppointmentsUiEvent()

    data class CreateAppointment(val selfUserWorkerId: Int, val userWorkerId: Int, val userClientId: Int, val localDateTime: LocalDateTime) : AppointmentsUiEvent()
    data class CreateAppointmentResult(val userWorkerId: Int, val appointmentId: Int, val price: Float, val notes: String) : AppointmentsUiEvent()
    data class PayForAppointment(val userClientId: Int, val appointmentResultId: Int, val payedAmount: Float, val payedAccount: String) : AppointmentsUiEvent()
    data class DeleteAppointment(val userWorkerId: Int, val appointmentId: Int) : AppointmentsUiEvent()
    data class DeleteAppointmentWithResult(val userWorkerId: Int, val appointmentId: Int, val resultId: Int) : AppointmentsUiEvent()
    data class ToggleEditMode(val userWorkerId: Int, val resultId: Int, val price: Float, val notes: String) : AppointmentsUiEvent()
    data object DisableEditMode : AppointmentsUiEvent()

    data object ShowInfoDialog : AppointmentsUiEvent()
    data object HideInfoDialog : AppointmentsUiEvent()
    data class ShowDateTimePickerDialog(val userWorkerId: Int, val userClientId: Int) : AppointmentsUiEvent()
    data object HideDateTimePickerDialog : AppointmentsUiEvent()
}