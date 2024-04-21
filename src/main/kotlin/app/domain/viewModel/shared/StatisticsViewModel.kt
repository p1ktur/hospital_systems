package app.domain.viewModel.shared

import app.data.shared.*
import app.domain.database.transactor.*
import app.domain.model.shared.statistics.*
import app.domain.uiEvent.shared.*
import app.domain.uiState.shared.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.*

class StatisticsViewModel(
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<StatisticsUiState> = MutableStateFlow(StatisticsUiState())
    val uiState = _uiState.asStateFlow()

    fun onUiEvent(event: StatisticsUiEvent) {
        when (event) {
            StatisticsUiEvent.FetchStatisticsForAdmin -> fetchStatisticsForAdmin()
            is StatisticsUiEvent.UpdateDisplayMode -> updateDisplayMode(event.mode)
        }
    }

    private fun fetchStatisticsForAdmin() {
        if (uiState.value.isLoading) return

        _uiState.value = uiState.value.copy(
            isLoading = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val fetchResult = statisticsRepository.fetchStatisticsData(viewModelScope)) {
                is TransactorResult.Failure -> {
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = listOf(1003)
                    )
                }
                is TransactorResult.Success<*> -> {
                    val data = fetchResult.data as StatisticsFetchData

                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorCodes = emptyList(),
                        appointmentStatistics = data.appointmentStatistics,
                        hospitalizationStatistics = data.hospitalizationStatistics,
                        additionalPaymentStatistics = data.additionalPaymentStatistics,
                        salaryStatistics = data.salaryStatistics,
                        totalMoneyStatistics = data.totalMoneyStatistics,
                        registrationStatistics = data.registrationStatistics,
                        bestDoctorsByAppointments = data.bestDoctorsByAppointments,
                        roomDataForStatistics = data.roomDataForStatistics,
                        totalWorkers = data.totalWorkers,
                        totalPatients = data.totalPatients
                    )
                }
            }
        }
    }

    private fun updateDisplayMode(mode: Int) {
        _uiState.value = uiState.value.copy(
            displayMode = mode
        )
    }
}