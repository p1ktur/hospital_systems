package app_doctor.domain.uiEvent

sealed class DoctorScheduleUiEvent {
    data class FetchInfo(val userWorkerId: Int) : DoctorScheduleUiEvent()
}