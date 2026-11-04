package com.timothysisa.utilitywise.ui.bills

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.timothysisa.utilitywise.R
import com.timothysisa.utilitywise.bills.BillSplitter
import com.timothysisa.utilitywise.model.Bill
import com.timothysisa.utilitywise.model.Housemate
import com.timothysisa.utilitywise.model.UtilityType
import com.timothysisa.utilitywise.ui.icon
import com.timothysisa.utilitywise.ui.navigation.FloatingNavigationBar
import com.timothysisa.utilitywise.ui.navigation.MainNavigationDestination
import com.timothysisa.utilitywise.ui.theme.Dimens
import com.timothysisa.utilitywise.ui.theme.InkSoft
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Displays the bill list and the selected bill's payment and sharing details.
 *
 * State changes are surfaced as callbacks so the screen stays independent from the repository.
 */
@Composable
fun BillsScreen(
    selectedBill: Bill?,
    upcomingBills: List<Bill>,
    housemates: List<Housemate>,
    snackbarHostState: SnackbarHostState,
    onAddBillClick: () -> Unit,
    onAddReadingClick: () -> Unit,
    onMarkPaidClick: (Bill) -> Unit,
    onShareBillClick: (Bill) -> Unit,
    onHousemateToggle: (Housemate) -> Unit,
    onBillClick: (Bill) -> Unit,
    onHomeClick: () -> Unit,
    onUsageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = Dimens.FloatingNavigationClearance)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(
                        start = Dimens.ScreenPadding,
                        top = Dimens.SpacingSmall,
                        end = Dimens.ScreenPadding,
                        bottom = Dimens.FloatingNavigationClearance
                    ),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge)
            ) {
                BillsTopBar(onAddBillClick = onAddBillClick)
                NextUpSection(bill = selectedBill, onMarkPaidClick = onMarkPaidClick)
                selectedBill?.let { bill ->
                    BillSplitCard(
                        bill = bill,
                        housemates = housemates,
                        onHousemateToggle = onHousemateToggle,
                        onShareBillClick = { onShareBillClick(bill) },
                    )
                }
                ComingUpSection(bills = upcomingBills, onBillClick = onBillClick)
            }
            FloatingNavigationBar(
                selectedDestination = MainNavigationDestination.BILLS,
                onHomeClick = onHomeClick,
                onAddReadingClick = onAddReadingClick,
                onUsageClick = onUsageClick,
                onBillsClick = {},
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = innerPadding.calculateBottomPadding() + Dimens.SpacingLarge)
            )
        }
    }
}

@Composable
private fun BillsTopBar(onAddBillClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.bills_title),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineLarge
        )
        Surface(
            onClick = onAddBillClick,
            modifier = Modifier.size(Dimens.IconButtonSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.bills_add),
                    modifier = Modifier.size(Dimens.IconSizeMedium)
                )
            }
        }
    }
}

@Composable
private fun NextUpSection(bill: Bill?, onMarkPaidClick: (Bill) -> Unit) {
    Text(text = stringResource(R.string.bills_next_up), style = MaterialTheme.typography.titleMedium)
    if (bill == null) {
        AllPaidCard()
    } else {
        NextUpCard(bill = bill) { onMarkPaidClick(bill) }
    }
}

@Composable
private fun AllPaidCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(Dimens.CardPaddingHorizontal)) {
            Text(text = stringResource(R.string.bills_all_paid_title), style = MaterialTheme.typography.titleLarge)
            Text(
                text = stringResource(R.string.bills_all_paid_description),
                modifier = Modifier.padding(top = Dimens.SpacingTiny),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NextUpCard(bill: Bill, onMarkPaidClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CornerRadiusHero),
        color = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(modifier = Modifier.padding(Dimens.HeroPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UtilityIcon(type = bill.type, inverted = true)
                Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
                Text(text = stringResource(bill.type.displayNameRes), style = MaterialTheme.typography.titleMedium)
            }
            Text(
                text = stringResource(R.string.money_amount, bill.amount),
                modifier = Modifier.padding(top = Dimens.SpacingLarge),
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = stringResource(R.string.bills_due, bill.periodLabel, dueDateLabel(bill.dueDate)),
                modifier = Modifier.padding(top = Dimens.SpacingTiny),
                style = MaterialTheme.typography.bodyMedium,
                color = InkSoft
            )
            Button(
                onClick = onMarkPaidClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingLarge)
                    .height(Dimens.ButtonHeight)
            ) {
                Text(text = stringResource(R.string.bills_mark_paid))
            }
        }
    }
}

@Composable
private fun BillSplitCard(
    bill: Bill,
    housemates: List<Housemate>,
    onHousemateToggle: (Housemate) -> Unit,
    onShareBillClick: () -> Unit
) {
    val sharedHousemates = housemates.filter { (id) -> id in bill.sharedWith }
    val shares = BillSplitter.split(bill.amount, sharedHousemates.size)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(Dimens.CardPaddingHorizontal)) {
            Text(text = stringResource(R.string.bills_split_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(
                    R.string.bills_split_total,
                    stringResource(R.string.money_amount, bill.amount),
                    pluralStringResource(
                        R.plurals.bills_people_count,
                        sharedHousemates.size,
                        sharedHousemates.size
                    )
                ),
                modifier = Modifier.padding(top = Dimens.SpacingTiny),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingLarge),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
            ) {
                housemates.forEach { housemate ->
                    SplitHousemate(
                        housemate = housemate,
                        selected = housemate.id in bill.sharedWith,
                        onClick = { onHousemateToggle(housemate) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            SplitDescription(shares = shares, modifier = Modifier.padding(top = Dimens.SpacingLarge))
            Button(
                onClick = onShareBillClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingLarge)
                    .height(Dimens.ButtonHeight)
            ) {
                Text(text = stringResource(R.string.bills_send_to_house_group))
            }
        }
    }
}

@Composable
private fun SplitHousemate(
    housemate: Housemate,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(Dimens.HousemateAvatarSize),
            shape = CircleShape,
            color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = housemate.name.take(1), style = MaterialTheme.typography.titleSmall)
            }
        }
        Text(
            text = housemate.name,
            modifier = Modifier.padding(top = Dimens.SpacingTiny),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SplitDescription(shares: List<Double>, modifier: Modifier = Modifier) {
    val highestShare = shares.first()
    val lowestShare = shares.last()
    val text = if (highestShare == lowestShare) {
        stringResource(R.string.bills_each_pays, stringResource(R.string.money_amount, highestShare))
    } else {
        stringResource(
            R.string.bills_uneven_split,
            stringResource(R.string.money_amount, highestShare),
            stringResource(R.string.money_amount, lowestShare)
        )
    }
    Text(text = text, modifier = modifier, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun ComingUpSection(bills: List<Bill>, onBillClick: (Bill) -> Unit) {
    Text(text = stringResource(R.string.bills_coming_up), style = MaterialTheme.typography.titleMedium)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(vertical = Dimens.SpacingTiny)) {
            bills.forEach { bill ->
                BillRow(bill = bill, onClick = { onBillClick(bill) })
            }
        }
    }
}

@Composable
private fun BillRow(bill: Bill, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.CardPaddingHorizontal, vertical = Dimens.RowPaddingVertical),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UtilityIcon(type = bill.type)
        Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(bill.type.displayNameRes), style = MaterialTheme.typography.titleSmall)
            Text(
                text = if (bill.isPaid) {
                    stringResource(R.string.bills_paid_on, shortDateLabel(bill.dueDate))
                } else {
                    stringResource(R.string.bills_due, bill.periodLabel, dueDateLabel(bill.dueDate))
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = stringResource(R.string.money_amount, bill.amount), style = MaterialTheme.typography.titleSmall)
            BillStatus(isPaid = bill.isPaid)
        }
    }
}

@Composable
private fun BillStatus(isPaid: Boolean) {
    if (isPaid) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = Dimens.SpacingSmall, vertical = Dimens.SpacingTiny),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.IconSizeSmall)
                )
                Text(text = stringResource(R.string.bills_paid), style = MaterialTheme.typography.labelSmall)
            }
        }
    } else {
        Text(
            text = stringResource(R.string.bills_unpaid),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UtilityIcon(type: UtilityType, inverted: Boolean = false) {
    Surface(
        modifier = Modifier.size(Dimens.IconCircleSize),
        shape = CircleShape,
        color = if (inverted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
        contentColor = if (inverted) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = type.icon, contentDescription = null, modifier = Modifier.size(Dimens.IconSizeSmall))
        }
    }
}

@Composable
private fun dueDateLabel(date: LocalDate): String {
    val formatter = rememberDateFormatter(R.string.bills_due_date_pattern)
    return date.format(formatter)
}

@Composable
private fun shortDateLabel(date: LocalDate): String {
    val formatter = rememberDateFormatter(R.string.bills_short_date_pattern)
    return date.format(formatter)
}

@Composable
private fun rememberDateFormatter(@StringRes patternRes: Int): DateTimeFormatter {
    val pattern = stringResource(patternRes)
    val locale = LocalConfiguration.current.locales[0]
    return remember(pattern, locale) { DateTimeFormatter.ofPattern(pattern, locale) }
}

@Preview
@Composable
private fun BillsScreenPreview() {
    UtilityWiseTheme {
        BillsScreen(
            selectedBill = previewBills.first(),
            upcomingBills = previewBills.drop(1),
            housemates = previewHousemates,
            snackbarHostState = remember { SnackbarHostState() },
            onAddBillClick = {},
            onAddReadingClick = {},
            onMarkPaidClick = {},
            onShareBillClick = {},
            onHousemateToggle = {},
            onBillClick = {},
            onHomeClick = {},
            onUsageClick = {}
        )
    }
}

@Preview
@Composable
private fun AllPaidBillsPreview() {
    UtilityWiseTheme {
        BillsScreen(
            selectedBill = null,
            upcomingBills = previewBills.map { it.copy(isPaid = true) },
            housemates = previewHousemates,
            snackbarHostState = remember { SnackbarHostState() },
            onAddBillClick = {},
            onAddReadingClick = {},
            onMarkPaidClick = {},
            onShareBillClick = {},
            onHousemateToggle = {},
            onBillClick = {},
            onHomeClick = {},
            onUsageClick = {}
        )
    }
}

@Preview
@Composable
private fun UnevenSplitPreview() {
    UtilityWiseTheme {
        BillsScreen(
            selectedBill = previewBills.first().copy(sharedWith = setOf(1L, 2L, 3L)),
            upcomingBills = previewBills.drop(1),
            housemates = previewHousemates,
            snackbarHostState = remember { SnackbarHostState() },
            onAddBillClick = {},
            onAddReadingClick = {},
            onMarkPaidClick = {},
            onShareBillClick = {},
            onHousemateToggle = {},
            onBillClick = {},
            onHomeClick = {},
            onUsageClick = {}
        )
    }
}

private val previewHousemates = listOf(
    Housemate(1, "Timothy"),
    Housemate(2, "Faith"),
    Housemate(3, "Brian"),
    Housemate(4, "Wanjiru")
)

private val previewBills = listOf(
    Bill(
        id = 1,
        type = UtilityType.ELECTRICITY,
        amount = 232.40,
        periodLabel = "Sep and Oct",
        dueDate = LocalDate.of(2026, 11, 5),
        isPaid = false,
        sharedWith = setOf(1, 2, 3, 4)
    ),
    Bill(
        id = 2,
        type = UtilityType.GAS,
        amount = 171.60,
        periodLabel = "Sep and Oct",
        dueDate = LocalDate.of(2026, 11, 8),
        isPaid = false,
        sharedWith = setOf(1, 2, 3, 4)
    ),
    Bill(3, UtilityType.BROADBAND, 50.0, "November", LocalDate.of(2026, 11, 1), true, setOf(1, 2, 3, 4))
)
