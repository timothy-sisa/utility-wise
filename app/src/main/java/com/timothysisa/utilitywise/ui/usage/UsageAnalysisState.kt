
package com.timothysisa.utilitywise.ui.usage

import com.timothysisa.utilitywise.data.UtilityRepository
import com.timothysisa.utilitywise.model.UtilityType
import java.time.YearMonth

private const val MONTHS_IN_TREND = 6

/** Cost for one month, used as a data point in the monthly cost chart. */
data class MonthlyCost(
    val month: YearMonth,
    val totalCost: Double
)

/** Cost and month-on-month change for one utility. */
data class UtilityCostBreakdown(
    val type: UtilityType,
    val cost: Double,
    val previousMonthCost: Double,
    val changeFromPreviousMonth: Double,
    val percentageChange: Double?
)

/** All values required to render the Usage Analysis screen. */
data class UsageAnalysisState(
    val selectedMonth: YearMonth,
    val totalCost: Double,
    val previousMonthTotal: Double,
    val changeFromPreviousMonth: Double,
    val percentageChange: Double?,
    val monthlyCosts: List<MonthlyCost>,
    val utilityBreakdown: List<UtilityCostBreakdown>
)

/**
 * Builds the Usage Analysis figures for [selectedMonth].
 *
 * The trend contains the selected month and the five months before it,
 * ordered from oldest to newest.
 */
fun usageAnalysisStateFor(selectedMonth: YearMonth): UsageAnalysisState {
    val previousMonth = selectedMonth.minusMonths(1)

    val totalCost = UtilityRepository.monthlyTotal(selectedMonth)
    val previousMonthTotal = UtilityRepository.monthlyTotal(previousMonth)

    val monthlyCosts = (MONTHS_IN_TREND - 1 downTo 0).map { monthsAgo ->
        val month = selectedMonth.minusMonths(monthsAgo.toLong())

        MonthlyCost(
            month = month,
            totalCost = UtilityRepository.monthlyTotal(month)
        )
    }

    val utilityBreakdown = UtilityType.entries.map { type ->
        val cost = UtilityRepository.monthlyTotalFor(type, selectedMonth)
        val previousMonthCost =
            UtilityRepository.monthlyTotalFor(type, previousMonth)

        val change = cost - previousMonthCost

        UtilityCostBreakdown(
            type = type,
            cost = cost,
            previousMonthCost = previousMonthCost,
            changeFromPreviousMonth = change,
            percentageChange = percentageChange(cost, previousMonthCost)
        )
    }

    val change = totalCost - previousMonthTotal

    return UsageAnalysisState(
        selectedMonth = selectedMonth,
        totalCost = totalCost,
        previousMonthTotal = previousMonthTotal,
        changeFromPreviousMonth = change,
        percentageChange = percentageChange(totalCost, previousMonthTotal),
        monthlyCosts = monthlyCosts,
        utilityBreakdown = utilityBreakdown
    )
}

private fun percentageChange(current: Double, previous: Double): Double? =
    if (previous == 0.0) {
        null
    } else {
        ((current - previous) / previous) * 100.0
    }
