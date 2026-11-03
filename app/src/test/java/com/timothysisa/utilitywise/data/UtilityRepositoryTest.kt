package com.timothysisa.utilitywise.data

import com.timothysisa.utilitywise.model.UtilityType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

// These tests only read the sample data, so sharing the repository object between them is safe.
class UtilityRepositoryTest {

    @Test
    fun `previous reading is the newest one on or before the date`() {
        val previous = UtilityRepository.previousReading(UtilityType.ELECTRICITY, LocalDate.of(2026, 11, 3))

        assertEquals(6L, previous?.id)
    }

    @Test
    fun `previous reading skips the reading being edited`() {
        val previous = UtilityRepository.previousReading(
            type = UtilityType.ELECTRICITY,
            date = LocalDate.of(2026, 10, 14),
            excludingId = 4
        )

        assertEquals(3L, previous?.id)
    }

    @Test
    fun `next reading is the oldest one after the date`() {
        val next = UtilityRepository.nextReading(
            type = UtilityType.ELECTRICITY,
            date = LocalDate.of(2026, 10, 14),
            excludingId = 4
        )

        assertEquals(5L, next?.id)
    }

    @Test
    fun `newest reading has no next reading`() {
        assertNull(UtilityRepository.nextReading(UtilityType.GAS, LocalDate.of(2026, 10, 25), excludingId = 9))
    }

    @Test
    fun `oldest reading has no previous reading`() {
        assertNull(UtilityRepository.previousReading(UtilityType.GAS, LocalDate.of(2026, 8, 31), excludingId = 7))
    }

    @Test
    fun `latest tariff comes from the newest reading of the type`() {
        assertEquals(0.13, UtilityRepository.latestTariff(UtilityType.GAS))
    }

    @Test
    fun `utility without readings has no latest tariff`() {
        assertNull(UtilityRepository.latestTariff(UtilityType.BROADBAND))
    }
}
