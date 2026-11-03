package com.timothysisa.utilitywise.ui.addreading

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.timothysisa.utilitywise.model.Reading
import com.timothysisa.utilitywise.model.UtilityType
import java.time.LocalDate
import kotlin.math.roundToLong

/**
 * The raw contents of the reading form, kept as text so that what the user typed survives
 * even when it is not a valid number yet.
 */
data class ReadingFormState(
    val type: UtilityType,
    val meterText: String,
    val date: LocalDate?,
    val tariffText: String,
    val meterFieldLeft: Boolean = false,
    val tariffFieldLeft: Boolean = false,
    val saveAttempted: Boolean = false
) {
    // Errors wait until the user leaves a field or presses save, so they are not told
    // off while typing the first digit.
    val showMeterError: Boolean
        get() = meterFieldLeft || saveAttempted

    val showTariffError: Boolean
        get() = tariffFieldLeft || saveAttempted

    val showDateError: Boolean
        get() = saveAttempted

    /** Switches to [newType] and prices it at that utility's [latestTariff]. */
    fun withType(newType: UtilityType, latestTariff: Double?): ReadingFormState = copy(
        type = newType,
        tariffText = tariffText(latestTariff),
        tariffFieldLeft = false
    )

    companion object {

        /** An empty form for a new reading of [type] dated [today]. */
        fun forNewReading(type: UtilityType, today: LocalDate, latestTariff: Double?) =
            ReadingFormState(
                type = type,
                meterText = "",
                date = today,
                tariffText = tariffText(latestTariff)
            )

        /** A form filled in with an existing [reading], for editing it. */
        fun forReading(reading: Reading) = ReadingFormState(
            type = reading.type,
            meterText = reading.value.roundToLong().toString(),
            date = reading.date,
            tariffText = tariffText(reading.tariff)
        )

        // Stores plain values so the form fits in a Bundle across rotation and process death.
        val Saver: Saver<ReadingFormState, Any> = listSaver(
            save = { form ->
                listOf(
                    form.type.name,
                    form.meterText,
                    form.date?.toEpochDay(),
                    form.tariffText,
                    form.meterFieldLeft,
                    form.tariffFieldLeft,
                    form.saveAttempted
                )
            },
            restore = { values ->
                ReadingFormState(
                    type = UtilityType.valueOf(values[0] as String),
                    meterText = values[1] as String,
                    date = (values[2] as Long?)?.let(LocalDate::ofEpochDay),
                    tariffText = values[3] as String,
                    meterFieldLeft = values[4] as Boolean,
                    tariffFieldLeft = values[5] as Boolean,
                    saveAttempted = values[6] as Boolean
                )
            }
        )

        private fun tariffText(tariff: Double?): String =
            tariff?.toBigDecimal()?.stripTrailingZeros()?.toPlainString().orEmpty()
    }
}
