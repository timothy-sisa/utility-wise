package com.timothysisa.utilitywise.bills

import java.util.Locale

data class BillSummaryTemplates(
    val evenSplit: String,
    val unevenSplit: String,
    val singlePerson: String,
    val conjunction: String,
)

/** Formats shareable bill summaries without depending on Android framework types. */
object BillSummaryFormatter {

    /**
     * Builds a complete bill message from localized templates supplied by the caller.
     *
     * Amounts and dates are already formatted at the Android boundary so this stays deterministic
     * and straightforward to unit test.
     */
    fun format(
        utilityName: String,
        periodLabel: String,
        formattedAmount: String,
        formattedDueDate: String,
        housemateNames: List<String>,
        formattedShares: List<String>,
        templates: BillSummaryTemplates,
        locale: Locale = Locale.getDefault()
    ): String {
        require(housemateNames.isNotEmpty()) { "At least one housemate is required." }
        require(housemateNames.size == formattedShares.size) { "Every housemate needs a share." }

        return when {
            housemateNames.size == 1 -> String.format(
                locale,
                templates.singlePerson,
                utilityName,
                periodLabel,
                formattedAmount,
                formattedDueDate,
                housemateNames.single(),
                formattedShares.single()
            )
            formattedShares.distinct().size == 1 -> String.format(
                locale,
                templates.evenSplit,
                utilityName,
                periodLabel,
                formattedAmount,
                formattedDueDate,
                namesLabel(housemateNames, templates.conjunction),
                formattedShares.first()
            )
            else -> String.format(
                locale,
                templates.unevenSplit,
                utilityName,
                periodLabel,
                formattedAmount,
                formattedDueDate,
                namesLabel(housemateNames, templates.conjunction),
                formattedShares.first(),
                formattedShares.last()
            )
        }
    }

    private fun namesLabel(names: List<String>, conjunction: String): String = when (names.size) {
        1 -> names.single()
        2 -> names.joinToString(separator = " $conjunction ")
        else -> names.dropLast(1).joinToString() + " $conjunction ${names.last()}"
    }
}
