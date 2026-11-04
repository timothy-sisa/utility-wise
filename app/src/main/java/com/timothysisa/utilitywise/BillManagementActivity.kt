package com.timothysisa.utilitywise

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
import com.timothysisa.utilitywise.data.UtilityRepository
import com.timothysisa.utilitywise.model.Bill
import com.timothysisa.utilitywise.model.Housemate
import com.timothysisa.utilitywise.ui.bills.AddBillSheet
import com.timothysisa.utilitywise.ui.bills.BillsScreen
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import com.timothysisa.utilitywise.ui.theme.enableLightEdgeToEdge
import kotlinx.coroutines.launch

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
                    onUsageClick = {}
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
