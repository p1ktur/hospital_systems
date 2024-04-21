package app.domain.model.shared.statistics

import app.domain.util.time.*

data class HospitalizationStatistics(
    val amountPerDay: List<Int> = emptyList(),
    val amountPerWeek: List<Int> = emptyList(),
    val amountPerMonth: List<Int> = emptyList(),
    val amountPerYear: List<Int> = emptyList(),
    val moneyPerDay: List<Int> = emptyList(),
    val moneyPerWeek: List<Int> = emptyList(),
    val moneyPerMonth: List<Int> = emptyList(),
    val moneyPerYear: List<Int> = emptyList()
) {

    fun containsData() = amountPerDay.isNotEmpty()
            || amountPerWeek.isNotEmpty()
            || amountPerMonth.isNotEmpty()
            || amountPerYear.isNotEmpty()
            || moneyPerDay.isNotEmpty()
            || moneyPerWeek.isNotEmpty()
            || moneyPerMonth.isNotEmpty()
            || moneyPerYear.isNotEmpty()

    fun amountToChartTimeData(): List<ChartTimeData> {
        return listOf(
            ChartTimeData.Day(
                data = amountPerDay,
                title = "Hospitalizations"
            ),
            ChartTimeData.Week(
                data = amountPerWeek,
                title = "Hospitalizations"
            ),
            ChartTimeData.Month(
                data = amountPerMonth,
                title = "Hospitalizations"
            ),
            ChartTimeData.Year(
                data = amountPerYear,
                title = "Hospitalizations"
            )
        )
    }

    fun moneyToChartTimeData(): List<ChartTimeData> {
        return listOf(
            ChartTimeData.Day(
                data = moneyPerDay,
                title = "Hospitalizations earnings"
            ),
            ChartTimeData.Week(
                data = moneyPerWeek,
                title = "Hospitalizations earnings"
            ),
            ChartTimeData.Month(
                data = moneyPerMonth,
                title = "Hospitalizations earnings"
            ),
            ChartTimeData.Year(
                data = moneyPerYear,
                title = "Hospitalizations earnings"
            )
        )
    }
}