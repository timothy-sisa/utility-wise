package com.timothysisa.utilitywise.validation

import java.time.LocalDate

object ReadingValidator {

    // Only plain decimals are accepted: toDoubleOrNull alone would also let through
    // "NaN", "Infinity" and exponents such as "1e5". A comma is accepted as the decimal
    // separator because many keyboards in Europe offer it instead of a point.
    private val decimalNumber = Regex("""\d+([.,]\d+)?""")

    /**
     * Checks the raw text of the reading form.
     *
     * [previousValue] and [nextValue] are the meter values of the readings of the same
     * utility either side of [date], or null when there is no such reading, as for the
     * first ever reading of a utility. [today] is passed in so the result does not depend
     * on the clock.
     */
    fun validate(
        meterText: String,
        date: LocalDate?,
        tariffText: String,
        previousValue: Double?,
        nextValue: Double?,
        today: LocalDate
    ): ReadingValidationResult {
        val meterError = meterError(meterText, previousValue, nextValue)
        val dateError = dateError(date, today)
        val tariffError = positiveNumberError(tariffText)
        val meterValue = parseNumber(meterText)
        val tariff = parseNumber(tariffText)
        return if (
            meterError == null && dateError == null && tariffError == null &&
            meterValue != null && date != null && tariff != null
        ) {
            ReadingValidationResult.Valid(meterValue = meterValue, date = date, tariff = tariff)
        } else {
            ReadingValidationResult.Invalid(
                meterError = meterError,
                dateError = dateError,
                tariffError = tariffError
            )
        }
    }

    /** Returns [text] as a number if it is a plain decimal, otherwise null. */
    fun parseNumber(text: String): Double? =
        text.trim()
            .takeIf { decimalNumber.matches(it) }
            ?.replace(',', '.')
            ?.toDouble()

    private fun meterError(text: String, previousValue: Double?, nextValue: Double?): ReadingError? {
        positiveNumberError(text)?.let { return it }
        val value = parseNumber(text) ?: return ReadingError.NotANumber
        return when {
            previousValue != null && value < previousValue -> ReadingError.BelowPrevious(previousValue)
            nextValue != null && value > nextValue -> ReadingError.AboveNext(nextValue)
            else -> null
        }
    }

    private fun dateError(date: LocalDate?, today: LocalDate): ReadingError? = when {
        date == null -> ReadingError.Required
        date.isAfter(today) -> ReadingError.InFuture
        else -> null
    }

    private fun positiveNumberError(text: String): ReadingError? {
        if (text.isBlank()) return ReadingError.Required
        val value = parseNumber(text) ?: return ReadingError.NotANumber
        return if (value > 0) null else ReadingError.NotPositive
    }
}
