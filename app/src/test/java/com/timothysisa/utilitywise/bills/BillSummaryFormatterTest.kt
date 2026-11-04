package com.timothysisa.utilitywise.bills

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class BillSummaryFormatterTest {

    private val templates = BillSummaryTemplates(
        evenSplit = "%1\$s bill for %2\$s: %3\$s, due %4\$s.\nSplit between %5\$s: %6\$s.\nSent from Utility-Wise",
        unevenSplit = "%1\$s bill for %2\$s: %3\$s, due %4\$s.\nSplit between %5\$s: %6\$s for one and %7\$s for the others.\nSent from Utility-Wise",
        singlePerson = "%1\$s bill for %2\$s: %3\$s, due %4\$s.\n%5\$s pays %6\$s.\nSent from Utility-Wise",
        conjunction = "and"
    )

    @Test
    fun `formats an even split for the house`() {
        assertEquals(
            "Electricity bill for Sep and Oct: €232.40, due Thu 5 Nov.\n" +
                "Split between Timothy, Faith, Brian and Wanjiru: €58.10.\nSent from Utility-Wise",
            format(
                names = listOf("Timothy", "Faith", "Brian", "Wanjiru"),
                shares = listOf("€58.10", "€58.10", "€58.10", "€58.10")
            )
        )
    }

    @Test
    fun `formats uneven shares`() {
        assertEquals(
            "Electricity bill for Sep and Oct: €232.40, due Thu 5 Nov.\n" +
                "Split between Timothy, Faith and Brian: €77.47 for one and €77.46 for the others.\nSent from Utility-Wise",
            format(
                names = listOf("Timothy", "Faith", "Brian"),
                shares = listOf("€77.47", "€77.46", "€77.46")
            )
        )
    }

    @Test
    fun `formats a single housemate sensibly`() {
        assertEquals(
            "Electricity bill for Sep and Oct: €232.40, due Thu 5 Nov.\n" +
                "Timothy pays €232.40.\nSent from Utility-Wise",
            format(names = listOf("Timothy"), shares = listOf("€232.40"))
        )
    }

    private fun format(names: List<String>, shares: List<String>): String =
        BillSummaryFormatter.format(
            utilityName = "Electricity",
            periodLabel = "Sep and Oct",
            formattedAmount = "€232.40",
            formattedDueDate = "Thu 5 Nov",
            housemateNames = names,
            formattedShares = shares,
            templates = templates,
            locale = Locale.UK
        )
}
