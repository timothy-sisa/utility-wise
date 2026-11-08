package com.timothysisa.utilitywise

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.SnackbarResult
import com.timothysisa.utilitywise.bills.BillSplitter
import com.timothysisa.utilitywise.bills.BillSummaryFormatter
import com.timothysisa.utilitywise.bills.BillSummaryTemplates
import com.timothysisa.utilitywise.data.UtilityRepository
import com.timothysisa.utilitywise.model.Bill
import com.timothysisa.utilitywise.model.Housemate
import com.timothysisa.utilitywise.ui.bills.AddBillSheet
import com.timothysisa.utilitywise.ui.bills.BillsScreen
import com.timothysisa.utilitywise.ui.dashboard.currentReportingMonth
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import com.timothysisa.utilitywise.ui.theme.enableLightEdgeToEdge
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class BillManagementActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLightEdgeToEdge()
        setContent {
            UtilityWiseTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                var selectedBillId by rememberSaveable { mutableStateOf(UtilityRepository.nextUnpaidBill()?.id) }
                var showAddBillSheet by rememberSaveable { mutableStateOf(value = false) }
                val selectedBill = selectedBillId?.let(UtilityRepository::getBillById)
                    ?: UtilityRepository.nextUnpaidBill()

                BillsScreen(
                    selectedBill = selectedBill,
                    upcomingBills = UtilityRepository.upcomingBills(),
                    housemates = UtilityRepository.housemates,
                    snackbarHostState = snackbarHostState,
                    onAddBillClick = { showAddBillSheet = true },
                    onAddReadingClick = { startActivity(AddReadingActivity.newIntent(this@BillManagementActivity)) },
                    onShareBillClick = { bill ->
                        shareBill(
                            bill = bill,
                            showSnackbar = { scope.launch { snackbarHostState.showSnackbar(it) } },
                        )
                    },
                    onMarkPaidClick = { bill ->
                        markPaid(
                            bill = bill,
                            onSelectNextBill = { selectedBillId = UtilityRepository.nextUnpaidBill()?.id },
                            onUndo = { selectedBillId = bill.id },
                            showSnackbar = { message, onUndo ->
                                scope.launch {
                                    if (snackbarHostState.showSnackbar(
                                            message = message,
                                            actionLabel = getString(R.string.bills_undo)
                                        ) == SnackbarResult.ActionPerformed
                                    ) {
                                        onUndo()
                                    }
                                }
                            }
                        )
                    },
                    onHousemateToggle = { housemate ->
                        toggleHousemate(selectedBill, housemate)
                    },
                    onBillClick = { selectedBillId = it.id },
                    onHomeClick = { startActivity(MainActivity.newIntent(this@BillManagementActivity)) },
                    onUsageClick = {
                        startActivity(
                            UsageAnalysisActivity.newIntent(this@BillManagementActivity, currentReportingMonth())
                        )
                    }
                )
                if (showAddBillSheet) {
                    AddBillSheet(
                        onSave = { type, validation ->
                            val bill = Bill(
                                id = UtilityRepository.nextBillId(),
                                type = type,
                                amount = validation.amount,
                                periodLabel = validation.periodLabel,
                                dueDate = validation.dueDate,
                                isPaid = false,
                                sharedWith = UtilityRepository.housemates.mapTo(linkedSetOf()) { it.id }
                            )
                            UtilityRepository.addBill(bill)
                            selectedBillId = bill.id
                            showAddBillSheet = false
                            scope.launch {
                                snackbarHostState.showSnackbar(getString(R.string.bills_added))
                            }
                        },
                        onDismiss = { showAddBillSheet = false }
                    )
                }
            }
        }
    }

    companion object {
        /** Creates an intent that opens Bills without duplicating an existing Bills activity. */
        fun newIntent(context: Context): Intent =
            Intent(context, BillManagementActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
    }

    private fun shareBill(bill: Bill, showSnackbar: (String) -> Unit) {
        val sharedHousemates = UtilityRepository.housemates.filter { it.id in bill.sharedWith }
        val shares = BillSplitter.split(bill.amount, sharedHousemates.size)
        val formattedShares = if ((shares.distinct().size == 1) && (sharedHousemates.size > 1)) {
            shares.map { getString(R.string.bills_summary_each, getString(R.string.money_amount, it)) }
        } else {
            shares.map { getString(R.string.money_amount, it) }
        }
        val locale = resources.configuration.locales[0]
        val dueDate = bill.dueDate.format(
            DateTimeFormatter.ofPattern(getString(R.string.bills_due_date_pattern), locale)
        )
        val summary = BillSummaryFormatter.format(
            utilityName = getString(bill.type.displayNameRes),
            periodLabel = bill.periodLabel,
            formattedAmount = getString(R.string.money_amount, bill.amount),
            formattedDueDate = dueDate,
            housemateNames = sharedHousemates.map { it.name },
            formattedShares = formattedShares,
            templates = BillSummaryTemplates(
                evenSplit = getString(R.string.bills_summary),
                unevenSplit = getString(R.string.bills_summary_uneven),
                singlePerson = getString(R.string.bills_summary_single),
                conjunction = getString(R.string.bills_conjunction_and)
            ),
            locale = locale
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, summary)
            putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.bills_share_subject, getString(bill.type.displayNameRes))
            )
        }
        try {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.bills_share_chooser)))
        } catch (_: ActivityNotFoundException) {
            showSnackbar(getString(R.string.bills_share_unavailable))
        }
    }

    private fun markPaid(
        bill: Bill,
        onSelectNextBill: () -> Unit,
        onUndo: () -> Unit,
        showSnackbar: (String, () -> Unit) -> Unit
    ) {
        UtilityRepository.setBillPaid(bill.id, true)
        onSelectNextBill()
        showSnackbar(getString(R.string.bills_marked_paid, getString(bill.type.displayNameRes))) {
            UtilityRepository.setBillPaid(bill.id, false)
            onUndo()
        }
    }

    private fun toggleHousemate(bill: Bill?, housemate: Housemate) {
        if (bill == null) return
        val sharedWith = bill.sharedWith
        if (housemate.id in sharedWith && sharedWith.size == 1) return
        val updatedHousemates = if (housemate.id in sharedWith) {
            sharedWith - housemate.id
        } else {
            sharedWith + housemate.id
        }
        UtilityRepository.setBillSharedWith(bill.id, updatedHousemates)
    }
}
