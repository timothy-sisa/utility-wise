package com.timothysisa.utilitywise.validation

import java.time.LocalDate

/** Validates the raw values entered in the add-bill form without any Android dependencies. */
object BillValidator {

    private val decimalAmount = Regex("""\d+([.,]\d+)?""")

    /** Returns parsed bill values when all required fields have valid values. */
    fun validate(
        amountText: String,
        periodLabel: String,
        dueDate: LocalDate?
    ): BillValidationResult {
        val amountError = amountError(amountText)
        val periodError = if (periodLabel.isBlank()) BillFieldError.REQUIRED else null
        val dueDateError = if (dueDate == null) BillFieldError.REQUIRED else null
        val amount = parseAmount(amountText)
        return if (
            amountError == null && periodError == null && dueDateError == null &&
            amount != null && dueDate != null
        ) {
            BillValidationResult.Valid(amount, periodLabel.trim(), dueDate)
        } else {
            BillValidationResult.Invalid(amountError, periodError, dueDateError)
        }
    }

    /** Returns a plain decimal amount, accepting either a decimal point or comma. */
    fun parseAmount(text: String): Double? =
        text.trim()
            .takeIf { decimalAmount.matches(it) }
            ?.replace(',', '.')
            ?.toDouble()

    private fun amountError(text: String): BillFieldError? {
        if (text.isBlank()) return BillFieldError.REQUIRED
        val trimmedText = text.trim()
        if (!decimalAmount.matches(trimmedText)) return BillFieldError.NOT_A_NUMBER
        val decimalPlaces = trimmedText.substringAfterLast('.', trimmedText.substringAfterLast(',', "")).length
        if (decimalPlaces > 2) return BillFieldError.TOO_MANY_DECIMAL_PLACES
        return if ((parseAmount(trimmedText) ?: 0.0) > 0.0) null else BillFieldError.NOT_POSITIVE
    }
}
