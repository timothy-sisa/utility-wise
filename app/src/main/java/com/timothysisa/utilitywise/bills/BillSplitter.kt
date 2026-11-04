package com.timothysisa.utilitywise.bills

import kotlin.math.roundToLong

/**
 * Splits an amount equally between a specified number of people.
 *
 * The app stores money as [Double], but this converts to integer cents only at the calculation
 * boundary so every returned share adds up exactly to the original amount. Any remainder cent is
 * allocated to the first people in the returned list.
 */
object BillSplitter {

    /** Returns each person's share, with the returned values adding up exactly to [amount]. */
    fun split(amount: Double, peopleCount: Int): List<Double> {
        require(amount >= 0) { "Amount must not be negative." }
        require(peopleCount > 0) { "At least one person is required." }

        val totalCents = (amount * CENTS_PER_EURO).roundToLong()
        val baseShare = totalCents / peopleCount
        val remainder = (totalCents % peopleCount).toInt()
        return List(peopleCount) { index ->
            (baseShare + if (index < remainder) 1 else 0).toDouble() / CENTS_PER_EURO
        }
    }

    private const val CENTS_PER_EURO = 100.0
}
