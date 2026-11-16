package com.timothysisa.utilitywise.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.concurrent.TimeUnit

/**
 * Listens to the accelerometer and calls [onShake] on the main thread for each deliberate shake.
 * Call [start] when the screen becomes visible and [stop] when it is hidden, so the sensor does
 * not drain the battery in the background. On a device without an accelerometer both are no-ops.
 */
class ShakeSensor(context: Context, private val onShake: () -> Unit) : SensorEventListener {

    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val detector = ShakeDetector()

    fun start() {
        accelerometer?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val (x, y, z) = event.values
        // SensorEvent.timestamp is nanoseconds on a monotonic clock, unlike wall-clock time,
        // which can jump and break the detector's time windows.
        val timestampMs = TimeUnit.NANOSECONDS.toMillis(event.timestamp)
        if (detector.onAcceleration(x, y, z, timestampMs)) onShake()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
}
