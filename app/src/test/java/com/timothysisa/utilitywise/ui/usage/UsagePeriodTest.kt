package com.timothysisa.utilitywise.ui.usage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.YearMonth

class UsagePeriodTest {

    @Test
    fun `period survives an encode and decode round trip`() {
        val period = YearMonth.of(2026, 10)

        assertEquals(period, decodeUsagePeriod(encodeUsagePeriod(period)))
    }

    @Test
    fun `missing period decodes to null`() {
        assertNull(decodeUsagePeriod(null))
    }

    @Test
    fun `malformed period decodes to null`() {
        assertNull(decodeUsagePeriod("October"))
        assertNull(decodeUsagePeriod("2026-13"))
        assertNull(decodeUsagePeriod(""))
    }
}
