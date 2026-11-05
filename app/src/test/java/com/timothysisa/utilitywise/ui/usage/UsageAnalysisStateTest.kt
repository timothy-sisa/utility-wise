
package com.timothysisa.utilitywise.ui.usage

import com.timothysisa.utilitywise.model.UtilityType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.YearMonth

class UsageAnalysisStateTest {

    @Test
    fun `builds selected month total and change from previous month`() {
        val state = usageAnalysisStateFor(YearMonth.of(2026, 10))

        assertEquals(257.15, state.totalCost, 0.001)
        assertEquals(233.90, state.previousMonthTotal, 0.001)
        assertEquals(23.25, state.changeFromPreviousMonth, 0.001)
        assertNotNull(state.percentageChange)
    }

    @Test
    fun `breakdown contains a cost and monthly change for every utility`() {
        val state = usageAnalysisStateFor(YearMonth.of(2026, 10))

        assertEquals(
            UtilityType.entries,
            state.utilityBreakdown.map { it.type }
        )

        val electricity = state.utilityBreakdown.first {
            it.type == UtilityType.ELECTRICITY
        }

        assertEquals(115.50, electricity.cost, 0.001)
        assertEquals(103.95, electricity.previousMonthCost, 0.001)
        assertEquals(11.55, electricity.changeFromPreviousMonth, 0.001)

        val gas = state.utilityBreakdown.first {
            it.type == UtilityType.GAS
        }

        assertEquals(91.65, gas.cost, 0.001)
        assertEquals(79.95, gas.previousMonthCost, 0.001)
        assertEquals(11.70, gas.changeFromPreviousMonth, 0.001)

        val broadband = state.utilityBreakdown.first {
            it.type == UtilityType.BROADBAND
        }

        assertEquals(50.0, broadband.cost, 0.001)
        assertEquals(50.0, broadband.previousMonthCost, 0.001)
        assertEquals(0.0, broadband.changeFromPreviousMonth, 0.001)
    }

    @Test
    fun `monthly costs contain six months ordered oldest to newest`() {
        val state = usageAnalysisStateFor(YearMonth.of(2026, 10))

        assertEquals(
            listOf(
                YearMonth.of(2026, 5),
                YearMonth.of(2026, 6),
                YearMonth.of(2026, 7),
                YearMonth.of(2026, 8),
                YearMonth.of(2026, 9),
                YearMonth.of(2026, 10)
            ),
            state.monthlyCosts.map { it.month }
        )

        assertEquals(257.15, state.monthlyCosts.last().totalCost, 0.001)
    }

    @Test
    fun `percentage change is zero when costs are unchanged`() {
        val state = usageAnalysisStateFor(YearMonth.of(2026, 9))

        val broadband = state.utilityBreakdown.first {
            it.type == UtilityType.BROADBAND
        }

        assertEquals(0.0, broadband.percentageChange!!, 0.001)
    }

    @Test
    fun `percentage change is positive when monthly cost increases`() {
        val state = usageAnalysisStateFor(YearMonth.of(2026, 10))

        assertTrue(state.percentageChange!! > 0.0)
    }
}
