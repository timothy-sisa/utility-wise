package com.timothysisa.utilitywise.validation

import java.time.LocalDate

sealed interface BillValidationResult {

    /** Every form field is valid and ready to be used to create a bill. */
    data class Valid(
        val amount: Double,
        val periodLabel: String,
        val dueDate: LocalDate,
    ) : BillValidationResult

    /** One or more form fields are invalid. A null error means that field is valid. */
    data class Invalid(
        val amountError: BillFieldError?,
        val periodError: BillFieldError?,
        val dueDateError: BillFieldError?
    ) : BillValidationResult
}

enum class BillFieldError {
    REQUIRED,
    NOT_A_NUMBER,
    NOT_POSITIVE,
    TOO_MANY_DECIMAL_PLACES
}
