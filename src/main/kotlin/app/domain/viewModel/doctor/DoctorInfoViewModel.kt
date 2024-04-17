package app.domain.viewModel.doctor

import app.data.doctor.*
import app.domain.database.transactor.*
import app.domain.model.doctor.*
import app.domain.uiEvent.doctor.*
import app.domain.uiState.doctor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class DoctorInfoViewModel(
    private val doctorInfoRepository: DoctorInfoRepository,
    private val doctorScheduleRepository: DoctorScheduleRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<DoctorInfoUiState> = MutableStateFlow(DoctorInfoUiState())
    val uiState = _uiState.asStateFlow()

    private var beforeEditingUiStateCopy: DoctorInfoUiState? = null

    fun onUiEvent(event: DoctorInfoUiEvent) {
        when (event) {
            is DoctorInfoUiEvent.FetchInfo -> fetchInfo(event.userWorkerId)
            is DoctorInfoUiEvent.SaveChanges -> saveChanges(event.userWorkerId)
            DoctorInfoUiEvent.ToggleEditMode -> toggleEditMode()

            is DoctorInfoUiEvent.UpdateAddress -> updateAddress(event.address)
            is DoctorInfoUiEvent.UpdateAge -> updateAge(event.age)
            is DoctorInfoUiEvent.UpdateEmail -> updateEmail(event.email)
            is DoctorInfoUiEvent.UpdateFathersName -> updateFathersName(event.fathersName)
            is DoctorInfoUiEvent.UpdateName -> updateName(event.name)
            is DoctorInfoUiEvent.UpdatePhone -> updatePhone(event.phone)
            is DoctorInfoUiEvent.UpdateSurname -> updateSurname(event.surname)
            is DoctorInfoUiEvent.UpdateDesignationFloor -> updateDesignationFloor(event.designationFloor)
            is DoctorInfoUiEvent.UpdateDesignationNumber -> updateDesignationNumber(event.designationNumber)
            is DoctorInfoUiEvent.UpdateDesignationName -> updateDesignationName(event.designationName)
            is DoctorInfoUiEvent.UpdatePosition -> updatePosition(event.position)
            is DoctorInfoUiEvent.UpdateSalary -> updateSalary(event.salary)
            is DoctorInfoUiEvent.UpdateStartDay -> updateStartDay(event.startDay)
            is DoctorInfoUiEvent.UpdateEndDay -> updateEndDay(event.endDay)
            is DoctorInfoUiEvent.UpdateStartTime -> updateStartTime(event.startTime)
            is DoctorInfoUiEvent.UpdateEndTime -> updateEndTime(event.endTime)
            is DoctorInfoUiEvent.UpdateRestHours -> updateRestHours(event.restHours)
            is DoctorInfoUiEvent.UpdateDesignationIndex -> updateDesignationIndex(event.index)
        }
    }

    private fun fetchInfo(userWorkerId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchInfoResult = doctorInfoRepository.fetchInfo(userWorkerId)) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    val data = fetchInfoResult.data as DoctorInfoData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        name = data.name,
                        surname = data.surname,
                        fathersName = data.fathersName,
                        age = data.age.toString(),
                        address = data.address,
                        phone = data.phone,
                        email = data.email,
                        position = data.position,
                        salary = data.salary.toString(),
                        designationName = data.designationName,
                        designationFloor = data.designationFloor.toString(),
                        designationNumber = data.designationNumber.toString(),
                        pendingAppointments = data.pendingAppointments,
                        finishedAppointments = data.finishedAppointments,
                        preloadedRooms = data.preloadedRooms
                    )

                    when (val fetchScheduleResult = doctorScheduleRepository.fetchInfo(userWorkerId)) {
                        is TransactorResult.Failure -> Unit
                        is TransactorResult.Success<*> -> {
                            val data = fetchScheduleResult.data as DoctorScheduleData
                            _uiState.value = uiState.value.copy(
                                startTime = data.startTime,
                                endTime = data.endTime,
                                startDay = data.startDay,
                                endDay = data.endDay,
                                hoursForRest = data.hoursForRest.toString()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun saveChanges(userWorkerId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        val commonCond = (uiState.value.startTime + uiState.value.endTime + uiState.value.startDay + uiState.value.endDay + uiState.value.hoursForRest).trim().isNotEmpty()
        val commonCond2 = uiState.value.startTime.isEmpty() || uiState.value.endTime.isEmpty() || uiState.value.startDay.isEmpty() || uiState.value.endDay.isEmpty() || uiState.value.hoursForRest.isEmpty()

        if (commonCond && commonCond2) {
            _uiState.value = uiState.value.copy(
                isLoading = false,
                errorCodes = listOf(1002)
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            var saveChangesResult: TransactorResult? = null
            var saveChangesForScheduleResult: TransactorResult? = null

            awaitAll(
                async {
                    saveChangesResult = doctorInfoRepository.saveChanges(
                        userDoctorId = userWorkerId,
                        name = uiState.value.name,
                        surname = uiState.value.surname,
                        fathersName = uiState.value.fathersName,
                        age = uiState.value.age,
                        address = uiState.value.address,
                        phone = uiState.value.phone,
                        email = uiState.value.email,
                        position = uiState.value.position,
                        salary = uiState.value.salary,
                        designationId = if (uiState.value.designationIndex >= 0) {
                            uiState.value.preloadedRooms[uiState.value.designationIndex].id
                        } else -1
                    )
                },
                async {
                    saveChangesForScheduleResult = doctorScheduleRepository.saveChanges(
                        userDoctorId = userWorkerId,
                        startTime = uiState.value.startTime,
                        endTime = uiState.value.endTime,
                        startDay = uiState.value.startDay,
                        endDay = uiState.value.endDay,
                        hoursForRest = uiState.value.hoursForRest
                    )
                }
            )

            if (saveChangesResult is TransactorResult.Success<*> && saveChangesForScheduleResult is TransactorResult.Success<*>) {
                _uiState.value = uiState.value.copy(
                    editMode = false,
                    isLoading = false,
                    errorCodes = emptyList()
                )
            } else {
                _uiState.value = uiState.value.copy(
                    editMode = false,
                    isLoading = false,
                    errorCodes = listOf(1003)
                )
            }
        }
    }

    private fun toggleEditMode() {
        if (!uiState.value.editMode) beforeEditingUiStateCopy = uiState.value

        _uiState.value = uiState.value.copy(
            editMode = !uiState.value.editMode
        )

        if (!uiState.value.editMode) beforeEditingUiStateCopy?.let {
            _uiState.value = it
        }
    }

    private fun updateSurname(surname: String) = _uiState.update { it.copy(surname = surname) }

    private fun updatePhone(phone: String) = _uiState.update { it.copy(phone = phone) }

    private fun updateName(name: String) = _uiState.update { it.copy(name = name) }

    private fun updateFathersName(fathersName: String) = _uiState.update { it.copy(fathersName = fathersName) }

    private fun updateEmail(email: String) = _uiState.update { it.copy(email = email) }

    private fun updateAge(age: String) = _uiState.update { it.copy(age = age) }

    private fun updateAddress(address: String) = _uiState.update { it.copy(address = address) }

    private fun updatePosition(position: String) = _uiState.update { it.copy(position = position) }

    private fun updateSalary(salary: String) = _uiState.update { it.copy(salary = salary) }

    private fun updateDesignationName(designationName: String) = _uiState.update { it.copy(designationName = designationName) }

    private fun updateDesignationNumber(designationNumber: String) = _uiState.update { it.copy(designationNumber = designationNumber) }

    private fun updateDesignationFloor(designationFloor: String) = _uiState.update { it.copy(designationFloor = designationFloor) }

    private fun updateStartDay(startDay: String) = _uiState.update { it.copy(startDay = startDay) }

    private fun updateEndDay(endDay: String) = _uiState.update { it.copy(endDay = endDay) }

    private fun updateStartTime(startTime: String) = _uiState.update { it.copy(startTime = startTime) }

    private fun updateEndTime(endTime: String) = _uiState.update { it.copy(endTime = endTime) }

    private fun updateRestHours(restHours: String) = _uiState.update { it.copy(hoursForRest = restHours) }

    private fun updateDesignationIndex(index: Int) = _uiState.update { it.copy(designationIndex = index) }
}