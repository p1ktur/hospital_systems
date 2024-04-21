package app.domain.model.shared.statistics

import app.domain.util.time.*
import kotlin.math.*

data class SalaryStatistics(
    val perDay: List<Int> = emptyList(),
    val perWeek: List<Int> = emptyList(),
    val perMonth: List<Int> = emptyList(),
    val perYear: List<Int> = emptyList()
) {
    fun containsData() = perDay.isNotEmpty()
            || perWeek.isNotEmpty()
            || perMonth.isNotEmpty()
            || perYear.isNotEmpty()

    fun amountToChartTimeData(): List<ChartTimeData> {
        return listOf(
            ChartTimeData.Day(
                data = perDay,
                title = "Salary"
            ),
            ChartTimeData.Week(
                data = perWeek,
                title = "Salary"
            ),
            ChartTimeData.Month(
                data = perMonth,
                title = "Salary"
            ),
            ChartTimeData.Year(
                data = perYear,
                title = "Salary"
            )
        )
    }

    fun amountPerMoneyEarnedToChartTimeData(totalMoneyStatistics: TotalMoneyStatistics): List<ChartTimeData> {
        return listOf(
            ChartTimeData.Day(
                data = perDay.mapIndexed { index, it ->
                    if (totalMoneyStatistics.perDay[index] == 0) {
                        0
                    } else {
                        (it / totalMoneyStatistics.perDay[index].toFloat()).roundToInt()
                    }
                },
                title = "Salary"
            ),
            ChartTimeData.Week(
                data = perWeek.mapIndexed { index, it ->
                    if (totalMoneyStatistics.perWeek[index] == 0) {
                        0
                    } else {
                        (it / totalMoneyStatistics.perWeek[index].toFloat()).roundToInt()
                    }
                },
                title = "Salary"
            ),
            ChartTimeData.Month(
                data = perMonth.mapIndexed { index, it ->
                    if (totalMoneyStatistics.perMonth[index] == 0) {
                        0
                    } else {
                        (it / totalMoneyStatistics.perMonth[index].toFloat()).roundToInt()
                    }
                },
                title = "Salary"
            ),
            ChartTimeData.Year(
                data = perYear.mapIndexed { index, it ->
                    if (totalMoneyStatistics.perYear[index] == 0) {
                        0
                    } else {
                        (it / totalMoneyStatistics.perYear[index].toFloat()).roundToInt()
                    }
                },
                title = "Salary"
            )
        )
    }
}