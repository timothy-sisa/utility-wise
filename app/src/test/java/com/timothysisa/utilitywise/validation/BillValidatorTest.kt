package com.timothysisa.utilitywise.validation

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class BillValidatorTest {

    private val dueDate = LocalDate.of(2026, 11, 5)

    @Test
    fun `valid bill returns parsed values`() {
        val result = BillValidator.validate("232.40", "Sep and Oct", dueDate)

        assertEquals(BillValidationResult.Valid(232.40, "Sep and Oct", dueDate), result)
    }

    @Test
    fun `amount is required`() {
        assertEquals(
            BillFieldError.REQUIRED,
            invalid(amountText = "").amountError
        )
    }

    @Test
    fun `amount must be a number`() {
        assertEquals(
            BillFieldError.NOT_A_NUMBER,
            invalid(amountText = "twenty").amountError
        )
    }

    @Test
    fun `amount must be greater than zero`() {
        assertEquals(
            BillFieldError.NOT_POSITIVE,
            invalid(amountText = "0").amountError
        )
    }

    @Test
    fun `amount allows at most two decimal places`() {
        assertEquals(
            BillFieldError.TOO_MANY_DECIMAL_PLACES,
            invalid(amountText = "1.234").amountError
        )
    }

    @Test
    fun `period is required`() {
        assertEquals(BillFieldError.REQUIRED, invalid(periodLabel = "").periodError)
    }

    @Test
    fun `due date is required`() {
        assertEquals(BillFieldError.REQUIRED, invalid(dueDate = null).dueDateError)
    }

    private fun invalid(
        amountText: String = "232.40",
        periodLabel: String = "Sep and Oct",
        dueDate: LocalDate? = this.dueDate
    ): BillValidationResult.Invalid =
        BillValidator.validate(amountText, periodLabel, dueDate) as BillValidationResult.Invalid
}
