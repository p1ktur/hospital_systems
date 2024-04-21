package app.domain.util.time

data class UnrefinedStatistics(
    val perDay: List<Int> = emptyList(),
    val perWeek: List<Int> = emptyList(),
    val perMonth: List<Int> = emptyList(),
    val perYear: List<Int> = emptyList()
)