package app_doctor.domain.viewModel

import app_doctor.data.*
import app_doctor.domain.model.*
import app_doctor.domain.uiEvent.*
import app_doctor.domain.uiState.*
import app_shared.domain.model.database.transactor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class DoctorScheduleViewModel(private val doctorScheduleRepository: DoctorScheduleRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<DoctorScheduleUiState> = MutableStateFlow(DoctorScheduleUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: DoctorScheduleUiEvent) {
        when (event) {
            is DoctorScheduleUiEvent.FetchInfo -> fetchInfo(event.userWorkerId)
        }
    }

    private fun fetchInfo(userWorkerId: Int) {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchInfoResult = doctorScheduleRepository.fetchInfo(userWorkerId)) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    val data = fetchInfoResult.data as DoctorScheduleData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        startTime = data.startTime,
                        endTime = data.endTime,
                        startDay = data.startDay,
                        endDay = data.endDay,
                        hoursForRest = data.hoursForRest
                    )
                }
            }
        }
    }
}