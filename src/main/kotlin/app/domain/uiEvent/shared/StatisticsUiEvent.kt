package app.domain.uiEvent.shared

sealed class StatisticsUiEvent {
    data object FetchStatisticsForAdmin : StatisticsUiEvent()

    data class UpdateDisplayMode(val mode: Int): StatisticsUiEvent()
}