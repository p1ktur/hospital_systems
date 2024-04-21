package app.domain.model.shared.statistics

import app.domain.util.time.*

data class RegistrationStatistics(
    val workersPerDay: List<Int> = emptyList(),
    val workersPerWeek: List<Int> = emptyList(),
    val workersPerMonth: List<Int> = emptyList(),
    val workersPerYear: List<Int> = emptyList(),
    val clientsPerDay: List<Int> = emptyList(),
    val clientsPerWeek: List<Int> = emptyList(),
    val clientsPerMonth: List<Int> = emptyList(),
    val clientsPerYear: List<Int> = emptyList()
) {

    fun containsData() = workersPerDay.isNotEmpty()
            || workersPerWeek.isNotEmpty()
            || workersPerMonth.isNotEmpty()
            || workersPerYear.isNotEmpty()
            || clientsPerDay.isNotEmpty()
            || clientsPerWeek.isNotEmpty()
            || clientsPerMonth.isNotEmpty()
            || clientsPerYear.isNotEmpty()

    fun workersToChartTimeData(): List<ChartTimeData> {
        return listOf(
            ChartTimeData.Day(
                data = workersPerDay,
                title = "Workers"
            ),
            ChartTimeData.Week(
                data = workersPerWeek,
                title = "Workers"
            ),
            ChartTimeData.Month(
                data = workersPerMonth,
                title = "Workers"
            ),
            ChartTimeData.Year(
                data = workersPerYear,
                title = "Workers"
            )
        )
    }

    fun clientsToChartTimeData(): List<ChartTimeData> {
        return listOf(
            ChartTimeData.Day(
                data = clientsPerDay,
                title = "Clients"
            ),
            ChartTimeData.Week(
                data = clientsPerWeek,
                title = "Clients"
            ),
            ChartTimeData.Month(
                data = clientsPerMonth,
                title = "Clients"
            ),
            ChartTimeData.Year(
                data = clientsPerYear,
                title = "Clients"
            )
        )
    }
}