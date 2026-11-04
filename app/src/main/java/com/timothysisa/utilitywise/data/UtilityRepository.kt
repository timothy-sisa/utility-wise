package com.timothysisa.utilitywise.data

import androidx.compose.runtime.mutableStateListOf
import com.timothysisa.utilitywise.model.Bill
import com.timothysisa.utilitywise.model.Housemate
import com.timothysisa.utilitywise.model.Reading
import com.timothysisa.utilitywise.model.UtilityType
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

object UtilityRepository {

    const val BROADBAND_MONTHLY_CHARGE = 50.0

    private const val ELECTRICITY_TARIFF = 0.35
    private const val GAS_TARIFF = 0.13
    private const val SAMPLE_YEAR = 2026

    // A meter only counts up, so on the same date the higher value was read later. The id
    // breaks exact ties so that two equal readings never both count as each other's previous.
    private val chronologicalOrder = compareBy<Reading>({ it.date }, { it.value }, { it.id })

    // Snapshot state lists let any composable that reads them recompose on change.
    private val mutableReadings = mutableStateListOf(
        electricityReading(id = 1, value = 23309.0, month = Month.AUGUST, day = 31),
        electricityReading(id = 2, value = 23452.0, month = Month.SEPTEMBER, day = 14),
        electricityReading(id = 3, value = 23606.0, month = Month.SEPTEMBER, day = 30),
        electricityReading(id = 4, value = 23718.0, month = Month.OCTOBER, day = 14),
        electricityReading(id = 5, value = 23845.0, month = Month.OCTOBER, day = 28),
        electricityReading(id = 6, value = 23936.0, month = Month.OCTOBER, day = 31),
        gasReading(id = 7, value = 11200.0, month = Month.AUGUST, day = 31),
        gasReading(id = 8, value = 11815.0, month = Month.SEPTEMBER, day = 25),
        gasReading(id = 9, value = 12520.0, month = Month.OCTOBER, day = 25)
    )

    private val mutableHousemates = mutableStateListOf(
        Housemate(id = 1, name = "Timothy"),
        Housemate(id = 2, name = "Faith"),
        Housemate(id = 3, name = "Brian"),
        Housemate(id = 4, name = "Wanjiru")
    )

    private val mutableBills = mutableStateListOf(
        Bill(
            id = 1,
            type = UtilityType.ELECTRICITY,
            amount = 232.40,
            periodLabel = "Sep and Oct",
            dueDate = LocalDate.of(SAMPLE_YEAR, Month.NOVEMBER, 5),
            isPaid = false,
            sharedWith = mutableHousemates.mapTo(linkedSetOf()) { it.id }
        ),
        Bill(
            id = 2,
            type = UtilityType.GAS,
            amount = 171.60,
            periodLabel = "Sep and Oct",
            dueDate = LocalDate.of(SAMPLE_YEAR, Month.NOVEMBER, 8),
            isPaid = false,
            sharedWith = mutableHousemates.mapTo(linkedSetOf()) { it.id }
        ),
        Bill(
            id = 3,
            type = UtilityType.BROADBAND,
            amount = BROADBAND_MONTHLY_CHARGE,
            periodLabel = "November",
            dueDate = LocalDate.of(SAMPLE_YEAR, Month.NOVEMBER, 1),
            isPaid = true,
            sharedWith = mutableHousemates.mapTo(linkedSetOf()) { it.id }
        )
    )

    val readings: List<Reading> = mutableReadings

    val bills: List<Bill> = mutableBills

    val housemates: List<Housemate> = mutableHousemates

    /** Returns the reading with the given [id], or null if there is none. */
    fun getReadingById(id: Long): Reading? = mutableReadings.firstOrNull { it.id == id }

    /** Stores a new [reading]. Use [nextReadingId] to give it a unique id. */
    fun addReading(reading: Reading) {
        mutableReadings.add(reading)
    }

    /**
     * Replaces the stored reading that has the same id as [reading].
     *
     * @throws IllegalArgumentException if no reading with that id exists.
     */
    fun updateReading(reading: Reading) {
        val index = mutableReadings.indexOfFirst { it.id == reading.id }
        require(index >= 0) { "No reading with id ${reading.id}" }
        mutableReadings[index] = reading
    }

    /** Returns an id that no stored reading uses yet. */
    fun nextReadingId(): Long = (mutableReadings.maxOfOrNull { it.id } ?: 0L) + 1

    /**
     * Returns the units consumed since the previous reading of the same utility type.
     *
     * The earliest reading of a type is the baseline the others are measured from,
     * so it returns 0.
     */
    fun unitsUsed(reading: Reading): Double {
        val previousReading = readingsOf(reading.type, excludingId = reading.id)
            .filter { chronologicalOrder.compare(it, reading) < 0 }
            .maxWithOrNull(chronologicalOrder)
            ?: return 0.0
        return reading.value - previousReading.value
    }

    /**
     * Returns the newest reading of [type] taken on or before [date], or null if there is
     * none. Pass the id of a reading being edited as [excludingId] so it is not compared
     * with itself.
     */
    fun previousReading(type: UtilityType, date: LocalDate, excludingId: Long? = null): Reading? =
        readingsOf(type, excludingId)
            .filter { it.date <= date }
            .maxWithOrNull(chronologicalOrder)

    /**
     * Returns the oldest reading of [type] taken after [date], or null if there is none.
     * Pass the id of a reading being edited as [excludingId] so it is not compared with
     * itself.
     */
    fun nextReading(type: UtilityType, date: LocalDate, excludingId: Long? = null): Reading? =
        readingsOf(type, excludingId)
            .filter { it.date > date }
            .minWithOrNull(chronologicalOrder)

    /** Returns the tariff of the newest reading of [type], or null if there are no readings. */
    fun latestTariff(type: UtilityType): Double? =
        readingsOf(type).maxWithOrNull(chronologicalOrder)?.tariff

    /** Returns the cost in euro of the units consumed up to [reading], at its tariff. */
    fun costOf(reading: Reading): Double = unitsUsed(reading) * reading.tariff

    /**
     * Returns the total cost in euro of every utility for [yearMonth]: the cost of
     * all readings dated in that month plus the fixed broadband charge.
     */
    fun monthlyTotal(yearMonth: YearMonth): Double =
        UtilityType.entries.sumOf { monthlyTotalFor(it, yearMonth) }

    /**
     * Returns the cost in euro of one utility [type] for [yearMonth]. Utilities
     * without a meter are charged a fixed monthly amount.
     */
    fun monthlyTotalFor(type: UtilityType, yearMonth: YearMonth): Double =
        if (type.hasMeter) {
            readingsIn(type, yearMonth).sumOf { costOf(it) }
        } else {
            BROADBAND_MONTHLY_CHARGE
        }

    /** Returns the units of [type] consumed by readings dated in [yearMonth]. */
    fun unitsFor(type: UtilityType, yearMonth: YearMonth): Double =
        readingsIn(type, yearMonth).sumOf { unitsUsed(it) }

    /** Returns the total in euro of every bill that has not been paid yet. */
    fun unpaidBillsTotal(): Double = mutableBills.filterNot { it.isPaid }.sumOf { it.amount }

    /** Returns the bill with the given [id], or null if there is none. */
    fun getBillById(id: Long): Bill? = mutableBills.firstOrNull { it.id == id }

    /** Stores a new [bill]. Use [nextBillId] to give it a unique id. */
    fun addBill(bill: Bill) {
        mutableBills.add(bill)
    }

    /** Returns an id that no stored bill uses yet. */
    fun nextBillId(): Long = (mutableBills.maxOfOrNull { it.id } ?: 0L) + 1

    /** Sets whether the bill with [id] is paid. */
    fun setBillPaid(id: Long, isPaid: Boolean) {
        replaceBill(id) { it.copy(isPaid = isPaid) }
    }

    /** Replaces the housemate ids sharing the bill with [id]. */
    fun setBillSharedWith(id: Long, housemateIds: Set<Long>) {
        require(housemateIds.isNotEmpty()) { "A bill must be shared with at least one housemate." }
        replaceBill(id) { it.copy(sharedWith = housemateIds.toSet()) }
    }

    /** Returns the unpaid bill due first, or null when every bill has been paid. */
    fun nextUnpaidBill(): Bill? =
        mutableBills.filterNot { it.isPaid }.minByOrNull { it.dueDate }

    /**
     * Returns all bills except [nextUnpaidBill], with unpaid bills before paid bills and each
     * group ordered by due date.
     */
    fun upcomingBills(): List<Bill> {
        val nextBillId = nextUnpaidBill()?.id
        return mutableBills
            .filterNot { it.id == nextBillId }
            .sortedWith(compareBy<Bill>({ it.isPaid }, { it.dueDate }))
    }

    /** Returns up to [count] readings of any type, newest first. */
    fun latestReadings(count: Int): List<Reading> =
        mutableReadings.sortedByDescending { it.date }.take(count)

    private fun replaceBill(id: Long, transform: (Bill) -> Bill) {
        val index = mutableBills.indexOfFirst { it.id == id }
        require(index >= 0) { "No bill with id $id" }
        mutableBills[index] = transform(mutableBills[index])
    }

    private fun readingsOf(type: UtilityType, excludingId: Long? = null): List<Reading> =
        mutableReadings.filter { it.type == type && it.id != excludingId }

    private fun readingsIn(type: UtilityType, yearMonth: YearMonth): List<Reading> =
        mutableReadings.filter { it.type == type && YearMonth.from(it.date) == yearMonth }

    private fun electricityReading(id: Long, value: Double, month: Month, day: Int) = Reading(
        id = id,
        type = UtilityType.ELECTRICITY,
        value = value,
        date = LocalDate.of(SAMPLE_YEAR, month, day),
        tariff = ELECTRICITY_TARIFF
    )

    private fun gasReading(id: Long, value: Double, month: Month, day: Int) = Reading(
        id = id,
        type = UtilityType.GAS,
        value = value,
        date = LocalDate.of(SAMPLE_YEAR, month, day),
        tariff = GAS_TARIFF
    )
}
