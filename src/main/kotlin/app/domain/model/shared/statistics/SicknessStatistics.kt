package app.domain.model.shared.statistics

import app.domain.util.time.*

data class SicknessStatistics(
    val sicknessOccurrencesPerYear: Map<String, Int> = mapOf()
) {

    fun containsData() = sicknessOccurrencesPerYear.isNotEmpty()

    fun names() = sicknessOccurrencesPerYear.map { it.key.run { if (this.length <= 20) this else this.substring(0, 21) + "..." } }

    fun toChartTimeData(): List<ChartTimeData> {
        return listOf(
            ChartTimeData.Year(
                data = sicknessOccurrencesPerYear.map { it.value },
                title = "Sickness occurrences"
            )
        )
    }
}