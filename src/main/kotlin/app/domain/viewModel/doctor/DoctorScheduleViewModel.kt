package app.domain.viewModel.doctor

import app.data.doctor.*
import app.domain.database.transactor.*
import app.domain.model.doctor.*
import app.domain.uiEvent.doctor.*
import app.domain.uiState.doctor.*
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
            when (val fetchInfoResult = doctorScheduleRepository.fetchInfoSafely(userWorkerId)) {
                is TransactorResult.Failure -> Unit
                is TransactorResult.Success<*> -> {
                    val data = fetchInfoResult.data as DoctorScheduleData
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        startTime = data.startTime,
                        endTime = data.endTime,
                        startDay = data.startDay,
                        endDay = data.endDay,
                        restStartTime = data.restStartTime,
                        restEndTime = data.restEndTime
                    )
                }
            }
        }
    }
}