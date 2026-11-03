package com.timothysisa.utilitywise.ui.dashboard

import com.timothysisa.utilitywise.data.UtilityRepository
import com.timothysisa.utilitywise.model.Housemate
import com.timothysisa.utilitywise.model.UtilityType
import java.time.LocalDate
import java.time.YearMonth

private const val LATEST_READINGS_COUNT = 3

data class DashboardState(
    val month: YearMonth,
    val monthlyTotal: Double,
    val changeFromPreviousMonth: Double,
    val utilitySummaries: List<UtilitySummary>,
    val latestReadings: List<ReadingSummary>,
    val housemates: List<Housemate>,
    val unpaidBillsTotal: Double
)

data class UtilitySummary(
    val type: UtilityType,
    val cost: Double
)

data class ReadingSummary(
    val id: Long,
    val type: UtilityType,
    val unitsUsed: Double,
    val date: LocalDate,
    val cost: Double
)

/**
 * Returns the month the dashboard reports on: the month of the newest reading, or the
 * calendar month when there are no readings yet. Following the calendar alone would show
 * an empty month whenever the latest readings are from the month before.
 */
fun currentReportingMonth(): YearMonth =
    UtilityRepository.latestReadings(1).firstOrNull()
        ?.let { YearMonth.from(it.date) }
        ?: YearMonth.now()

/**
 * Builds the dashboard figures for [month] from [UtilityRepository]. Calling this during
 * composition makes the caller recompose whenever the repository's readings change.
 */
fun dashboardStateFor(month: YearMonth): DashboardState {
    val monthlyTotal = UtilityRepository.monthlyTotal(month)
    val previousMonthTotal = UtilityRepository.monthlyTotal(month.minusMonths(1))
    return DashboardState(
        month = month,
        monthlyTotal = monthlyTotal,
        changeFromPreviousMonth = monthlyTotal - previousMonthTotal,
        utilitySummaries = UtilityType.entries.map { type ->
            UtilitySummary(
                type = type,
                cost = UtilityRepository.monthlyTotalFor(type, month)
            )
        },
        latestReadings = UtilityRepository.latestReadings(LATEST_READINGS_COUNT).map { reading ->
            ReadingSummary(
                id = reading.id,
                type = reading.type,
                unitsUsed = UtilityRepository.unitsUsed(reading),
                date = reading.date,
                cost = UtilityRepository.costOf(reading)
            )
        },
        housemates = UtilityRepository.housemates.toList(),
        unpaidBillsTotal = UtilityRepository.unpaidBillsTotal()
    )
}
