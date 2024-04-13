package app_shared.domain.model

import app_shared.domain.model.database.dbModels.*

data class FetchAppointmentData(
    val appointments: List<Appointment> = emptyList(),
    val appointmentResults: List<AppointmentResult> = emptyList(),
    val payments: List<Payment> = emptyList()
)