package app.domain.uiEvent.doctor

sealed class DoctorScheduleUiEvent {
    data class FetchInfo(val userWorkerId: Int) : DoctorScheduleUiEvent()
}