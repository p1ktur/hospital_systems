package app.domain.model.shared.statistics

import app.domain.util.time.*

data class TotalMoneyStatistics(
    val perDay: List<Int> = emptyList(),
    val perWeek: List<Int> = emptyList(),
    val perMonth: List<Int> = emptyList(),
    val perYear: List<Int> = emptyList()
) {

    companion object {
        fun fromAllToChartTimeData(
            appointmentStatistics: AppointmentStatistics,
            hospitalizationStatistics: HospitalizationStatistics,
            additionalPaymentStatistics: AdditionalPaymentStatistics
        ): TotalMoneyStatistics {
            return TotalMoneyStatistics(
                perDay = buildList {
                    for (i in 0 until 30) {
                        add(appointmentStatistics.moneyPerDay[i] + hospitalizationStatistics.moneyPerDay[i] + additionalPaymentStatistics.perDay[i])
                    }
                },
                perWeek = buildList {
                    for (i in 0 until 12) {
                        add(appointmentStatistics.moneyPerDay[i] + hospitalizationStatistics.moneyPerDay[i] + additionalPaymentStatistics.perDay[i])
                    }
                },
                perMonth = buildList {
                    for (i in 0 until 12) {
                        add(appointmentStatistics.moneyPerDay[i] + hospitalizationStatistics.moneyPerDay[i] + additionalPaymentStatistics.perDay[i])
                    }
                },
                perYear = buildList {
                    for (i in 0 until 1) {
                        add(appointmentStatistics.moneyPerDay[i] + hospitalizationStatistics.moneyPerDay[i] + additionalPaymentStatistics.perDay[i])
                    }
                }
            )
        }
    }

    fun containsData() = perDay.isNotEmpty()
            || perWeek.isNotEmpty()
            || perMonth.isNotEmpty()
            || perYear.isNotEmpty()

    fun moneyToChartTimeData(): List<ChartTimeData> {
        return listOf(
            ChartTimeData.Day(
                data = perDay,
                title = "Total earnings"
            ),
            ChartTimeData.Week(
                data = perWeek,
                title = "Total earnings"
            ),
            ChartTimeData.Month(
                data = perMonth,
                title = "Total earnings"
            ),
            ChartTimeData.Year(
                data = perYear,
                title = "Total earnings"
            )
        )
    }
}