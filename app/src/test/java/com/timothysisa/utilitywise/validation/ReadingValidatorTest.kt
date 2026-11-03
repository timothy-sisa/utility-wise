package com.timothysisa.utilitywise.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ReadingValidatorTest {

    private val today = LocalDate.of(2026, 11, 3)

    private fun validate(
        meterText: String = "24054",
        date: LocalDate? = today,
        tariffText: String = "0.35",
        previousValue: Double? = 23936.0,
        nextValue: Double? = null
    ) = ReadingValidator.validate(
        meterText = meterText,
        date = date,
        tariffText = tariffText,
        previousValue = previousValue,
        nextValue = nextValue,
        today = today
    )

    private fun ReadingValidationResult.asInvalid() = this as ReadingValidationResult.Invalid

    @Test
    fun `valid form returns the parsed values`() {
        val result = validate()

        assertEquals(ReadingValidationResult.Valid(meterValue = 24054.0, date = today, tariff = 0.35), result)
    }

    @Test
    fun `blank meter reading is required`() {
        assertEquals(ReadingError.Required, validate(meterText = " ").asInvalid().meterError)
    }

    @Test
    fun `meter reading that is not a number is rejected`() {
        assertEquals(ReadingError.NotANumber, validate(meterText = "24a54").asInvalid().meterError)
    }

    @Test
    fun `meter reading written as NaN is not a number`() {
        assertEquals(ReadingError.NotANumber, validate(meterText = "NaN").asInvalid().meterError)
    }

    @Test
    fun `meter reading of zero is rejected`() {
        val result = validate(meterText = "0", previousValue = null).asInvalid()

        assertEquals(ReadingError.NotPositive, result.meterError)
    }

    @Test
    fun `meter reading lower than the previous reading is rejected`() {
        val result = validate(meterText = "23935").asInvalid()

        assertEquals(ReadingError.BelowPrevious(23936.0), result.meterError)
    }

    @Test
    fun `meter reading equal to the previous reading is valid`() {
        assertEquals(ReadingValidationResult.Valid::class, validate(meterText = "23936")::class)
    }

    @Test
    fun `first ever reading of a utility has no lower limit`() {
        val result = validate(meterText = "5", previousValue = null)

        assertEquals(ReadingValidationResult.Valid(meterValue = 5.0, date = today, tariff = 0.35), result)
    }

    @Test
    fun `editing a reading in the middle rejects a value above the next reading`() {
        val result = validate(meterText = "23900", previousValue = 23606.0, nextValue = 23845.0).asInvalid()

        assertEquals(ReadingError.AboveNext(23845.0), result.meterError)
    }

    @Test
    fun `editing a reading in the middle rejects a value below the previous reading`() {
        val result = validate(meterText = "23600", previousValue = 23606.0, nextValue = 23845.0).asInvalid()

        assertEquals(ReadingError.BelowPrevious(23606.0), result.meterError)
    }

    @Test
    fun `editing a reading in the middle accepts a value between its neighbours`() {
        val result = validate(meterText = "23718", previousValue = 23606.0, nextValue = 23845.0)

        assertEquals(ReadingValidationResult.Valid(meterValue = 23718.0, date = today, tariff = 0.35), result)
    }

    @Test
    fun `missing date is required`() {
        assertEquals(ReadingError.Required, validate(date = null).asInvalid().dateError)
    }

    @Test
    fun `date in the future is rejected`() {
        assertEquals(ReadingError.InFuture, validate(date = today.plusDays(1)).asInvalid().dateError)
    }

    @Test
    fun `date in the past is valid`() {
        val result = validate(date = today.minusDays(30))

        assertEquals(ReadingValidationResult.Valid(meterValue = 24054.0, date = today.minusDays(30), tariff = 0.35), result)
    }

    @Test
    fun `blank tariff is required`() {
        assertEquals(ReadingError.Required, validate(tariffText = "").asInvalid().tariffError)
    }

    @Test
    fun `tariff that is not a number is rejected`() {
        assertEquals(ReadingError.NotANumber, validate(tariffText = "abc").asInvalid().tariffError)
    }

    @Test
    fun `negative tariff is not a plain number`() {
        assertEquals(ReadingError.NotANumber, validate(tariffText = "-0.35").asInvalid().tariffError)
    }

    @Test
    fun `tariff of zero is rejected`() {
        assertEquals(ReadingError.NotPositive, validate(tariffText = "0.00").asInvalid().tariffError)
    }

    @Test
    fun `tariff with a decimal comma is accepted`() {
        val result = validate(tariffText = "0,35")

        assertEquals(ReadingValidationResult.Valid(meterValue = 24054.0, date = today, tariff = 0.35), result)
    }

    @Test
    fun `every invalid field is reported at once`() {
        val result = validate(meterText = "", date = today.plusDays(1), tariffText = "x").asInvalid()

        assertEquals(ReadingError.Required, result.meterError)
        assertEquals(ReadingError.InFuture, result.dateError)
        assertEquals(ReadingError.NotANumber, result.tariffError)
    }

    @Test
    fun `fields that are valid have no error when another field fails`() {
        val result = validate(tariffText = "").asInvalid()

        assertNull(result.meterError)
        assertNull(result.dateError)
    }
}
