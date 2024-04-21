package app.domain.util.time

import java.time.*
import java.time.temporal.ChronoUnit
import kotlin.math.*

object DataToStatisticsParser {

    private val baseDayList = List(30) { 0 }
    private val baseWeekList = List(12) { 0 }
    private val baseMonthList = List(12) { 0 }
    private val baseYearList = listOf(0)

    fun parseForStatisticsAndCount(data: List<Pair<LocalDate, Int>>): Pair<UnrefinedStatistics, UnrefinedStatistics> {
        val now = LocalDate.now()

        val dayStart = now.minusDays(baseDayList.size.toLong() - 1)
        val weekStart = now.minusWeeks(baseWeekList.size.toLong() - 1)
        val monthStart = now.minusMonths(baseMonthList.size.toLong() - 1)
        val yearStart = now.minusYears(baseYearList.size.toLong())

        val dayData = data.filter { it.first.isAfter(dayStart.minusDays(1)) && it.first.isBefore(now.plusDays(1))}
        val weekData = data.filter { it.first.isAfter(weekStart.minusDays(1)) && it.first.isBefore(now.plusDays(1))}
        val monthData = data.filter { it.first.isAfter(monthStart.minusDays(1)) && it.first.isBefore(now.plusDays(1))}
        val yearData = data.filter { it.first.isAfter(yearStart) && it.first.isBefore(now.plusDays(1))}

        val amountPerDay = baseDayList.toMutableList().apply {
            dayData.forEach { item ->
                val dayDifference = ChronoUnit.DAYS.between(dayStart, item.first).absoluteValue.toInt()
                set(dayDifference, get(dayDifference) + 1)
            }
        }
        val amountPerWeek = baseWeekList.toMutableList().apply {
            weekData.forEach { item ->
                val weekDifference = ChronoUnit.WEEKS.between(weekStart, item.first).absoluteValue.toInt()
                set(weekDifference, get(weekDifference) + 1)
            }
        }
        val amountPerMonth = baseMonthList.toMutableList().apply {
            monthData.forEach { item ->
                val monthDifference = ChronoUnit.MONTHS.between(monthStart, item.first).absoluteValue.toInt()
                set(monthDifference, get(monthDifference) + 1)
            }
        }
        val amountPerYear = baseYearList.toMutableList().apply {
            yearData.forEach { item ->
                val yearDifference = ChronoUnit.YEARS.between(yearStart, item.first).absoluteValue.toInt()
                if (yearDifference == 0) set(0, get(yearDifference) + 1)
            }
        }
        val moneyPerDay = baseDayList.toMutableList().apply {
            dayData.forEach { item ->
                val dayDifference = ChronoUnit.DAYS.between(dayStart, item.first).absoluteValue.toInt()
                set(dayDifference, get(dayDifference) + item.second)
            }
        }
        val moneyPerWeek = baseWeekList.toMutableList().apply {
            weekData.forEach { item ->
                val weekDifference = ChronoUnit.WEEKS.between(weekStart, item.first).absoluteValue.toInt()
                set(weekDifference, get(weekDifference) + item.second)
            }
        }
        val moneyPerMonth = baseMonthList.toMutableList().apply {
            monthData.forEach { item ->
                val monthDifference = ChronoUnit.MONTHS.between(monthStart, item.first).absoluteValue.toInt()
                set(monthDifference, get(monthDifference) + item.second)
            }
        }
        val moneyPerYear = baseYearList.toMutableList().apply {
            yearData.forEach { item ->
                val yearDifference = ChronoUnit.YEARS.between(yearStart, item.first).absoluteValue.toInt()
                if (yearDifference == 0) set(0, get(yearDifference) + item.second)
            }
        }

        return UnrefinedStatistics(
            perDay = amountPerDay,
            perWeek = amountPerWeek,
            perMonth = amountPerMonth,
            perYear = amountPerYear
        ) to UnrefinedStatistics(
            perDay = moneyPerDay,
            perWeek = moneyPerWeek,
            perMonth = moneyPerMonth,
            perYear = moneyPerYear
        )
    }

    fun parseForStatistics(data: List<Pair<LocalDate, Int>>): UnrefinedStatistics {
        val now = LocalDate.now()

        val dayStart = now.minusDays(baseDayList.size.toLong() - 1)
        val weekStart = now.minusWeeks(baseWeekList.size.toLong() - 1)
        val monthStart = now.minusMonths(baseMonthList.size.toLong() - 1)
        val yearStart = now.minusYears(baseYearList.size.toLong())

        val dayData = data.filter { it.first.isAfter(dayStart.minusDays(1)) && it.first.isBefore(now.plusDays(1))}
        val weekData = data.filter { it.first.isAfter(weekStart.minusDays(1)) && it.first.isBefore(now.plusDays(1))}
        val monthData = data.filter { it.first.isAfter(monthStart.minusDays(1)) && it.first.isBefore(now.plusDays(1))}
        val yearData = data.filter { it.first.isAfter(yearStart) && it.first.isBefore(now.plusDays(1))}

        val moneyPerDay = baseDayList.toMutableList().apply {
            dayData.forEach { item ->
                val dayDifference = ChronoUnit.DAYS.between(dayStart, item.first).absoluteValue.toInt()
                set(dayDifference, get(dayDifference) + item.second)
            }
        }
        val moneyPerWeek = baseWeekList.toMutableList().apply {
            weekData.forEach { item ->
                val weekDifference = ChronoUnit.WEEKS.between(weekStart, item.first).absoluteValue.toInt()
                set(weekDifference, get(weekDifference) + item.second)
            }
        }
        val moneyPerMonth = baseMonthList.toMutableList().apply {
            monthData.forEach { item ->
                val monthDifference = ChronoUnit.MONTHS.between(monthStart, item.first).absoluteValue.toInt()
                set(monthDifference, get(monthDifference) + item.second)
            }
        }
        val moneyPerYear = baseYearList.toMutableList().apply {
            yearData.forEach { item ->
                val yearDifference = ChronoUnit.YEARS.between(yearStart, item.first).absoluteValue.toInt()
                if (yearDifference == 0) set(0, get(yearDifference) + item.second)
            }
        }

        return UnrefinedStatistics(
            perDay = moneyPerDay,
            perWeek = moneyPerWeek,
            perMonth = moneyPerMonth,
            perYear = moneyPerYear
        )
    }

    fun parseForStatisticsAggregating(data: List<Pair<LocalDate, Int>>): UnrefinedStatistics {
        val now = LocalDate.now()

        val dayStart = now.minusDays(baseDayList.size.toLong() - 1)
        val weekStart = now.minusWeeks(baseWeekList.size.toLong() - 1)
        val monthStart = now.minusMonths(baseMonthList.size.toLong() - 1)
        val yearStart = now.minusYears(baseYearList.size.toLong())

        val dayData = data.filter { it.first.isAfter(dayStart.minusDays(1)) && it.first.isBefore(now.plusDays(1))}
        val weekData = data.filter { it.first.isAfter(weekStart.minusDays(1)) && it.first.isBefore(now.plusDays(1))}
        val monthData = data.filter { it.first.isAfter(monthStart.minusDays(1)) && it.first.isBefore(now.plusDays(1))}
        val yearData = data.filter { it.first.isAfter(yearStart) && it.first.isBefore(now.plusDays(1))}

        val moneyPerDay = baseDayList.toMutableList().apply {
            dayData.forEach { item ->
                val dayDifference = ChronoUnit.DAYS.between(dayStart, item.first).absoluteValue.toInt()
                set(dayDifference, get(dayDifference) + item.second)
            }

            for (i in 1 until baseDayList.size) {
                set(i, get(i - 1) + get(i))
            }
        }
        val moneyPerWeek = baseWeekList.toMutableList().apply {
            weekData.forEach { item ->
                val weekDifference = ChronoUnit.WEEKS.between(weekStart, item.first).absoluteValue.toInt()
                set(weekDifference, get(weekDifference) + item.second)
            }

            for (i in 1 until baseWeekList.size) {
                set(i, get(i - 1) + get(i))
            }
        }
        val moneyPerMonth = baseMonthList.toMutableList().apply {
            monthData.forEach { item ->
                val monthDifference = ChronoUnit.MONTHS.between(monthStart, item.first).absoluteValue.toInt()
                set(monthDifference, get(monthDifference) + item.second)
            }

            for (i in 1 until baseMonthList.size) {
                set(i, get(i - 1) + get(i))
            }
        }
        val moneyPerYear = baseYearList.toMutableList().apply {
            yearData.forEach { item ->
                val yearDifference = ChronoUnit.YEARS.between(yearStart, item.first).absoluteValue.toInt()
                if (yearDifference == 0) set(0, get(yearDifference) + item.second)
            }

            for (i in 1 until baseYearList.size) {
                set(i, get(i - 1) + get(i))
            }
        }

        return UnrefinedStatistics(
            perDay = moneyPerDay,
            perWeek = moneyPerWeek,
            perMonth = moneyPerMonth,
            perYear = moneyPerYear
        )
    }

    fun countForStatistics(data: List<LocalDate>): UnrefinedStatistics {
        val now = LocalDate.now()

        val dayStart = now.minusDays(baseDayList.size.toLong() - 1)
        val weekStart = now.minusWeeks(baseWeekList.size.toLong() - 1)
        val monthStart = now.minusMonths(baseMonthList.size.toLong() - 1)
        val yearStart = now.minusYears(baseYearList.size.toLong())

        val dayData = data.filter { it.isAfter(dayStart.minusDays(1)) && it.isBefore(now.plusDays(1))}
        val weekData = data.filter { it.isAfter(weekStart.minusDays(1)) && it.isBefore(now.plusDays(1))}
        val monthData = data.filter { it.isAfter(monthStart.minusDays(1)) && it.isBefore(now.plusDays(1))}
        val yearData = data.filter { it.isAfter(yearStart) && it.isBefore(now.plusDays(1))}

        val amountPerDay = baseDayList.toMutableList().apply {
            dayData.forEach { item ->
                val dayDifference = ChronoUnit.DAYS.between(dayStart, item).absoluteValue.toInt()
                set(dayDifference, get(dayDifference) + 1)
            }
        }
        val amountPerWeek = baseWeekList.toMutableList().apply {
            weekData.forEach { item ->
                val weekDifference = ChronoUnit.WEEKS.between(weekStart, item).absoluteValue.toInt()
                set(weekDifference, get(weekDifference) + 1)
            }
        }
        val amountPerMonth = baseMonthList.toMutableList().apply {
            monthData.forEach { item ->
                val monthDifference = ChronoUnit.MONTHS.between(monthStart, item).absoluteValue.toInt()
                set(monthDifference, get(monthDifference) + 1)
            }
        }
        val amountPerYear = baseYearList.toMutableList().apply {
            yearData.forEach { item ->
                val yearDifference = ChronoUnit.YEARS.between(yearStart, item).absoluteValue.toInt()
                if (yearDifference == 0) set(0, get(yearDifference) + 1)
            }
        }

        return UnrefinedStatistics(
            perDay = amountPerDay,
            perWeek = amountPerWeek,
            perMonth = amountPerMonth,
            perYear = amountPerYear
        )
    }
}