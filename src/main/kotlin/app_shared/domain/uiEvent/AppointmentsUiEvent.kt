package app_shared.domain.uiEvent

import java.util.Date

sealed class AppointmentsUiEvent {
    data object FetchAppointmentsForAdmin : AppointmentsUiEvent()
    data class FetchAppointmentsForDoctor(val userWorkerId: Int) : AppointmentsUiEvent()
    data class FetchAppointmentsForClient(val userClientId: Int) : AppointmentsUiEvent()

    data class CreateAppointment(val selfUserWorkerId: Int, val userWorkerId: Int, val userClientId: Int, val date: Date) : AppointmentsUiEvent()
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