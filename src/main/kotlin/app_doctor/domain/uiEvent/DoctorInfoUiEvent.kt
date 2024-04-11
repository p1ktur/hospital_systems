package app_doctor.domain.uiEvent

sealed class DoctorInfoUiEvent {
    data class FetchInfo(val userWorkerId: Int) : DoctorInfoUiEvent()
    data class SaveChanges(val userWorkerId: Int) : DoctorInfoUiEvent()
    data object ToggleEditMode : DoctorInfoUiEvent()

    data class UpdateName(val name: String) : DoctorInfoUiEvent()
    data class UpdateSurname(val surname: String) : DoctorInfoUiEvent()
    data class UpdateFathersName(val fathersName: String) : DoctorInfoUiEvent()
    data class UpdateAge(val age: String) : DoctorInfoUiEvent()
    data class UpdateAddress(val address: String) : DoctorInfoUiEvent()
    data class UpdatePhone(val phone: String) : DoctorInfoUiEvent()
    data class UpdateEmail(val email: String) : DoctorInfoUiEvent()
    data class UpdatePosition(val position: String) : DoctorInfoUiEvent()
    data class UpdateSalary(val salary: String) : DoctorInfoUiEvent()
    data class UpdateDesignationName(val designationName: String) : DoctorInfoUiEvent()
    data class UpdateDesignationFloor(val designationFloor: String) : DoctorInfoUiEvent()
    data class UpdateDesignationNumber(val designationNumber: String) : DoctorInfoUiEvent()
    data class UpdateStartDay(val startDay: String) : DoctorInfoUiEvent()
    data class UpdateEndDay(val endDay: String) : DoctorInfoUiEvent()
    data class UpdateStartTime(val startTime: String) : DoctorInfoUiEvent()
    data class UpdateEndTime(val endTime: String) : DoctorInfoUiEvent()
    data class UpdateRestHours(val restHours: String) : DoctorInfoUiEvent()
    data class UpdateDesignationIndex(val index: Int) : DoctorInfoUiEvent()
}