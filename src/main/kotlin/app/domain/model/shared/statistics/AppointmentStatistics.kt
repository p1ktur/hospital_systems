package app.domain.model.shared.statistics

import app.domain.util.time.*

data class AppointmentStatistics(
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
                title = "Appointments"
            ),
            ChartTimeData.Week(
                data = amountPerWeek,
                title = "Appointments"
            ),
            ChartTimeData.Month(
                data = amountPerMonth,
                title = "Appointments"
            ),
            ChartTimeData.Year(
                data = amountPerYear,
                title = "Appointments"
            )
        )
    }

    fun moneyToChartTimeData(): List<ChartTimeData> {
        return listOf(
            ChartTimeData.Day(
                data = moneyPerDay,
                title = "Appointments earnings"
            ),
            ChartTimeData.Week(
                data = moneyPerWeek,
                title = "Appointments earnings"
            ),
            ChartTimeData.Month(
                data = moneyPerMonth,
                title = "Appointments earnings"
            ),
            ChartTimeData.Year(
                data = moneyPerYear,
                title = "Appointments earnings"
            )
        )
    }
}