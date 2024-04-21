package app.domain.model.shared.statistics

import app.domain.util.time.*

data class AdditionalPaymentStatistics(
    val perDay: List<Int> = emptyList(),
    val perWeek: List<Int> = emptyList(),
    val perMonth: List<Int> = emptyList(),
    val perYear: List<Int> = emptyList()
) {
    fun containsData() = perDay.isNotEmpty()
            || perWeek.isNotEmpty()
            || perMonth.isNotEmpty()
            || perYear.isNotEmpty()

    fun moneyToChartTimeData(): List<ChartTimeData> {
        return listOf(
            ChartTimeData.Day(
                data = perDay,
                title = "Additional earnings"
            ),
            ChartTimeData.Week(
                data = perWeek,
                title = "Additional earnings"
            ),
            ChartTimeData.Month(
                data = perMonth,
                title = "Additional earnings"
            ),
            ChartTimeData.Year(
                data = perYear,
                title = "Additional earnings"
            )
        )
    }
}