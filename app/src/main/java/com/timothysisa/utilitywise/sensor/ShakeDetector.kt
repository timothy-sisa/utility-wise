package com.timothysisa.utilitywise.sensor

import kotlin.math.sqrt

// Standard gravity in m/s², so dividing by it turns a phone at rest into a reading of about 1 g.
internal const val STANDARD_GRAVITY = 9.81

// Walking and setting the phone down stay under ~2 g; a deliberate wrist shake clears 2.5 g.
internal const val SPIKE_THRESHOLD_G = 2.7

// One jolt is easy to cause by accident; a back-and-forth motion produces at least two.
internal const val SPIKES_PER_SHAKE = 2

// A single back-and-forth flick takes well under half a second.
internal const val SHAKE_WINDOW_MS = 500L

// People keep shaking for a moment after the first one lands; this turns that into one refresh.
internal const val SHAKE_COOL_DOWN_MS = 1_000L

/**
 * Decides whether a stream of accelerometer samples contains a deliberate shake. It holds no
 * Android types so the timing rules can be tested on the JVM with hand-made samples.
 */
class ShakeDetector {

    private var windowStartMs: Long? = null
    private var spikesInWindow = 0
    private var lastShakeMs: Long? = null

    /**
     * Feeds one sample, with [x], [y] and [z] in m/s², and returns true only for the sample that
     * completes a shake. [timestampMs] must come from a monotonic clock.
     */
    fun onAcceleration(x: Float, y: Float, z: Float, timestampMs: Long): Boolean {
        val gForce = sqrt((x * x + y * y + z * z).toDouble()) / STANDARD_GRAVITY
        if (gForce <= SPIKE_THRESHOLD_G) return false

        // Spikes during the cool-down are dropped rather than counted, otherwise the tail of one
        // shake would seed the window of the next.
        val lastShake = lastShakeMs
        if (lastShake != null && timestampMs - lastShake < SHAKE_COOL_DOWN_MS) return false

        val windowStart = windowStartMs
        if (windowStart == null || timestampMs - windowStart > SHAKE_WINDOW_MS) {
            windowStartMs = timestampMs
            spikesInWindow = 1
            return false
        }

        spikesInWindow++
        if (spikesInWindow < SPIKES_PER_SHAKE) return false

        lastShakeMs = timestampMs
        windowStartMs = null
        spikesInWindow = 0
        return true
    }
}
