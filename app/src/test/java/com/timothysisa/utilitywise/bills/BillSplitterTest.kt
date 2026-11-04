package com.timothysisa.utilitywise.bills

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.roundToLong

class BillSplitterTest {

    @Test
    fun `even split gives every person the same share`() {
        assertEquals(listOf(58.10, 58.10, 58.10, 58.10), BillSplitter.split(232.40, 4))
    }

    @Test
    fun `leftover cents are allocated one at a time from the first person`() {
        assertEquals(listOf(33.34, 33.33, 33.33), BillSplitter.split(100.00, 3))
    }

    @Test
    fun `one person receives the whole amount`() {
        assertEquals(listOf(77.46), BillSplitter.split(77.46, 1))
    }

    @Test
    fun `shares always add up to the original amount`() {
        val shares = BillSplitter.split(232.40, 3)

        assertEquals(23240L, shares.sumOf { (it * 100).roundToLong() })
    }
}
