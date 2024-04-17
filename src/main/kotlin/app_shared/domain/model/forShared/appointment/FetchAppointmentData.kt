package app_shared.domain.model.forShared.appointment

import app_shared.domain.model.forShared.*

data class FetchAppointmentData(
    val appointments: List<Appointment> = emptyList(),
    val appointmentResults: List<AppointmentResult> = emptyList(),
    val payments: List<Payment> = emptyList()
)