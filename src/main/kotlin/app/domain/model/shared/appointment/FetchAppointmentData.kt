package app.domain.model.shared.appointment

import app.domain.model.shared.*

data class FetchAppointmentData(
    val appointments: List<Appointment> = emptyList(),
    val appointmentResults: List<AppointmentResult> = emptyList(),
    val payments: List<Payment> = emptyList()
)