package com.timothysisa.utilitywise.location

import com.timothysisa.utilitywise.model.House
import com.timothysisa.utilitywise.model.UtilityType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HouseTariffTest {

    private val house = House(1, "Dublin house share", 53.3498, -6.2603, mapOf(UtilityType.GAS to 0.12))

    @Test
    fun houseWithTariffReturnsIt() {
        assertEquals(0.12, tariffFor(house, UtilityType.GAS)!!, 0.0)
    }

    @Test
    fun houseWithoutTariffForUtilityReturnsNull() {
        assertNull(tariffFor(house, UtilityType.ELECTRICITY))
    }

    @Test
    fun noHouseReturnsNull() {
        assertNull(tariffFor(null, UtilityType.GAS))
    }
}
