package app.domain.util.time

sealed interface ChartTimeData {

    val data: List<Int>
    val title: String?

    data class Hour(override val data: List<Int>, override val title: String? = null): ChartTimeData // last 24 hours
    data class Day(override val data: List<Int>, override val title: String? = null): ChartTimeData // last 30 days
    data class Week(override val data: List<Int>, override val title: String? = null): ChartTimeData // last 12 weeks
    data class Month(override val data: List<Int>, override val title: String? = null): ChartTimeData // last 12 months
    data class Year(override val data: List<Int>, override val title: String? = null): ChartTimeData // last 1 year
}