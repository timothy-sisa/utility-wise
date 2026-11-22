package com.timothysisa.utilitywise.location

import com.timothysisa.utilitywise.model.House
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NearestHouseTest {

    private val dublin = House(1, "Dublin house share", 53.3498, -6.2603, emptyMap())
    private val cork = House(2, "Cork family home", 51.8985, -8.4756, emptyMap())

    @Test
    fun pointAtHouseReturnsThatHouse() {
        assertEquals(cork, nearestHouse(cork.latitude, cork.longitude, listOf(dublin, cork)))
    }

    @Test
    fun pointJustInsideLimitReturnsHouse() {
        // 0.0017 degrees of latitude is about 189 m.
        assertEquals(dublin, nearestHouse(dublin.latitude + 0.0017, dublin.longitude, listOf(dublin)))
    }

    @Test
    fun pointJustOutsideLimitReturnsNull() {
        // 0.0019 degrees of latitude is about 211 m.
        assertNull(nearestHouse(dublin.latitude + 0.0019, dublin.longitude, listOf(dublin)))
    }

    @Test
    fun nearerOfTwoHousesIsReturned() {
        val tenKmFromCork = cork.latitude + 0.09
        assertEquals(cork, nearestHouse(tenKmFromCork, cork.longitude, listOf(dublin, cork), maxMetres = 50_000.0))
    }

    @Test
    fun emptyListReturnsNull() {
        assertNull(nearestHouse(dublin.latitude, dublin.longitude, emptyList()))
    }
}
