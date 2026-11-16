package com.timothysisa.utilitywise.sensor

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ShakeDetectorTest {

    private lateinit var detector: ShakeDetector

    // Matches the ~60 ms delivery rate of SENSOR_DELAY_UI.
    private val sampleIntervalMs = 60L

    // Comfortably above the threshold so the tests check timing, not the force boundary.
    private val spikeForce = (SPIKE_THRESHOLD_G * STANDARD_GRAVITY * 1.2).toFloat()

    @Before
    fun setUp() {
        detector = ShakeDetector()
    }

    @Test
    fun restingPhoneNeverTriggers() {
        val restingGravity = STANDARD_GRAVITY.toFloat()

        val triggered = (0L until 200L).any { sample ->
            detector.onAcceleration(0f, 0f, restingGravity, sample * sampleIntervalMs)
        }

        assertFalse(triggered)
    }

    @Test
    fun singleSpikeDoesNotTrigger() {
        assertFalse(spike(atMs = 1_000L))
    }

    @Test
    fun twoSpikesInsideWindowTrigger() {
        assertFalse(spike(atMs = 1_000L))
        assertTrue(spike(atMs = 1_000L + SHAKE_WINDOW_MS / 2))
    }

    @Test
    fun twoSpikesFurtherApartThanWindowDoNotTrigger() {
        assertFalse(spike(atMs = 1_000L))
        assertFalse(spike(atMs = 1_000L + SHAKE_WINDOW_MS + 1))
    }

    @Test
    fun secondShakeInsideCoolDownIsIgnored() {
        val firstShakeMs = 1_000L
        spike(atMs = firstShakeMs - sampleIntervalMs)
        assertTrue(spike(atMs = firstShakeMs))

        val secondShakeMs = firstShakeMs + SHAKE_COOL_DOWN_MS / 2

        assertFalse(spike(atMs = secondShakeMs - sampleIntervalMs))
        assertFalse(spike(atMs = secondShakeMs))
    }

    private fun spike(atMs: Long): Boolean =
        detector.onAcceleration(spikeForce, 0f, 0f, atMs)
}
