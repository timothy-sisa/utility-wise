package com.timothysisa.utilitywise.ui.dashboard

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.WavingHand
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.timothysisa.utilitywise.R
import com.timothysisa.utilitywise.model.Housemate
import com.timothysisa.utilitywise.model.UtilityType
import com.timothysisa.utilitywise.ui.icon
import com.timothysisa.utilitywise.ui.navigation.FloatingNavigationBar
import com.timothysisa.utilitywise.ui.navigation.MainNavigationDestination
import com.timothysisa.utilitywise.ui.theme.Dimens
import com.timothysisa.utilitywise.ui.theme.InkMute
import com.timothysisa.utilitywise.ui.theme.InkSoft
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

private const val READING_DATE_PATTERN = "d MMM"
private const val SPEND_MONTH_PATTERN = "MMM yyyy"

/**
 * The home screen. [onReadingClick] receives the id of the reading row the user tapped, and
 * messages sent to [snackbarHostState] appear above the floating navigation bar.
 */
@Composable
fun DashboardScreen(
    state: DashboardState,
    snackbarHostState: SnackbarHostState,
    onAddReadingClick: () -> Unit,
    onReadingClick: (Long) -> Unit,
    onUsageClick: () -> Unit,
    onBillsClick: () -> Unit,
    modifier: Modifier = Modifier
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
                    )
            ) {
                DashboardTopBar()
                Spacer(modifier = Modifier.height(Dimens.CardPaddingVertical))
                SpendSummaryCard(
                    month = state.month,
                    monthlyTotal = state.monthlyTotal,
                    changeFromPreviousMonth = state.changeFromPreviousMonth,
                    utilitySummaries = state.utilitySummaries
                )
                QuickActionRow(
                    onAddReadingClick = onAddReadingClick,
                    onUsageClick = onUsageClick,
                    onBillsClick = onBillsClick,
                    modifier = Modifier.padding(vertical = Dimens.SpacingLarge)
                )
                HousematesCard(
                    housemates = state.housemates,
                    unpaidBillsTotal = state.unpaidBillsTotal
                )
                RecentReadingsHeader()
                RecentReadingsCard(readings = state.latestReadings, onReadingClick = onReadingClick)
            }
            FloatingNavigationBar(
                selectedDestination = MainNavigationDestination.HOME,
                onHomeClick = {},
                onAddReadingClick = onAddReadingClick,
                onUsageClick = onUsageClick,
                onBillsClick = onBillsClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = innerPadding.calculateBottomPadding() + Dimens.SpacingLarge)
            )
        }
    }
}

@Composable
fun SpendSummaryCard(
    month: YearMonth,
    monthlyTotal: Double,
    changeFromPreviousMonth: Double,
    utilitySummaries: List<UtilitySummary>,
    modifier: Modifier = Modifier
) {
    val locale = currentLocale()
    val monthFormatter = remember(locale) { DateTimeFormatter.ofPattern(SPEND_MONTH_PATTERN, locale) }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.HeroMinHeight),
        shape = RoundedCornerShape(Dimens.CornerRadiusHero),
        color = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(modifier = Modifier.padding(Dimens.HeroPadding)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.dashboard_estimated_spend, month.format(monthFormatter)),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMute
                    )
                    MonthChangeChip(change = changeFromPreviousMonth, previousMonth = month.minusMonths(1))
                }
                Column {
                    Text(
                        text = stringResource(R.string.money_total, monthlyTotal),
                        modifier = Modifier.padding(top = Dimens.SpacingTiny, bottom = Dimens.SpacingMedium),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.HeroMetaSpacing)) {
                        utilitySummaries.forEach { summary -> SpendMetaItem(summary = summary) }
                    }
                }
            }
    }
}

@Composable
fun ReadingRow(summary: ReadingSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val locale = currentLocale()
    val dateFormatter = remember(locale) { DateTimeFormatter.ofPattern(READING_DATE_PATTERN, locale) }
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.CardPaddingHorizontal, vertical = Dimens.RowPaddingVertical),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UtilityIconCircle(type = summary.type)
        Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(summary.type.displayNameRes),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = stringResource(
                    R.string.reading_units_on_date,
                    unitsLabel(type = summary.type, units = summary.unitsUsed),
                    summary.date.format(dateFormatter)
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
        Text(
            text = stringResource(R.string.money_amount, summary.cost),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun DashboardTopBar() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(Dimens.IconCircleSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.dashboard_initials),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
        Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.dashboard_greeting),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(Dimens.SpacingTiny))
                Icon(
                    imageVector = Icons.Rounded.WavingHand,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.IconSizeSmall)
                )
            }
        }
    }
}

@Composable
private fun MonthChangeChip(change: Double, previousMonth: YearMonth) {
    // Decide the sign on the rounded value so a change of -0.40 reads as "+€0", not "-€0".
    val roundedChange = change.roundToLong()
    val changeText = if (roundedChange >= 0) {
        stringResource(R.string.dashboard_change_increase, roundedChange, monthName(previousMonth))
    } else {
        stringResource(R.string.dashboard_change_decrease, abs(roundedChange), monthName(previousMonth))
    }
    Surface(
        shape = RoundedCornerShape(Dimens.CornerRadiusSmall),
        color = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    ) {
        Text(
            text = changeText,
            modifier = Modifier.padding(horizontal = Dimens.SpacingSmall, vertical = Dimens.SpacingTiny),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun SpendMetaItem(summary: UtilitySummary) {
    Column {
        Text(
            text = stringResource(summary.type.displayNameRes),
            style = MaterialTheme.typography.labelSmall,
            color = InkSoft,
            maxLines = 1
        )
        Text(
            text = stringResource(R.string.money_amount, summary.cost),
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1
        )
    }
}

@Composable
private fun QuickActionRow(
    onAddReadingClick: () -> Unit,
    onUsageClick: () -> Unit,
    onBillsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickAction(
            icon = Icons.Rounded.Add,
            labelRes = R.string.dashboard_action_reading,
            onClick = onAddReadingClick
        )
        QuickAction(
            icon = Icons.Rounded.BarChart,
            labelRes = R.string.navigation_usage,
            onClick = onUsageClick
        )
        QuickAction(
            icon = Icons.AutoMirrored.Rounded.ReceiptLong,
            labelRes = R.string.navigation_bills,
            onClick = onBillsClick
        )
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    @StringRes labelRes: Int,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surface
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(Dimens.QuickActionSize),
            shape = CircleShape,
            color = containerColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.IconSizeMedium)
                )
            }
        }
        Text(
            text = stringResource(labelRes),
            modifier = Modifier.padding(top = Dimens.SpacingTiny),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun HousematesCard(
    housemates: List<Housemate>,
    unpaidBillsTotal: Double,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.dashboard_housemates),
            modifier = Modifier.padding(vertical = Dimens.SpacingSmall),
            style = MaterialTheme.typography.titleMedium
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = Dimens.CardPaddingHorizontal,
                    vertical = Dimens.CardPaddingVertical
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    housemates.forEach { housemate -> HousemateAvatar(housemate = housemate) }
                }
                Row(
                    modifier = Modifier.padding(top = Dimens.SpacingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_unpaid_to_split),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpacingTiny))
                    Text(
                        text = stringResource(R.string.money_amount, unpaidBillsTotal),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun HousemateAvatar(housemate: Housemate) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(Dimens.HousemateAvatarSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = housemate.name.take(1),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
        Text(
            text = housemate.name,
            modifier = Modifier.padding(top = Dimens.SpacingTiny),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun RecentReadingsHeader() {
    Text(
        text = stringResource(R.string.dashboard_recent_readings),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun RecentReadingsCard(readings: List<ReadingSummary>, onReadingClick: (Long) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        color = MaterialTheme.colorScheme.surface
    ) {
        // Rows carry their own side padding so the ripple fills the whole width of the card.
        Column(modifier = Modifier.padding(vertical = Dimens.SpacingTiny)) {
            readings.forEachIndexed { index, reading ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Dimens.CardPaddingHorizontal),
                        thickness = Dimens.DividerThickness,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                ReadingRow(summary = reading, onClick = { onReadingClick(reading.id) })
            }
        }
    }
}

@Composable
private fun UtilityIconCircle(type: UtilityType) {
    Surface(
        modifier = Modifier.size(Dimens.IconCircleSize),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = type.icon,
                contentDescription = null,
                modifier = Modifier.size(Dimens.IconSizeSmall)
            )
        }
    }
}

@Composable
private fun unitsLabel(type: UtilityType, units: Double): String {
    val unitRes = type.unitRes
    return if (unitRes == null) {
        stringResource(R.string.units_without_unit, units)
    } else {
        stringResource(R.string.units_with_unit, units, stringResource(unitRes))
    }
}

@Composable
private fun monthName(month: YearMonth): String =
    month.month.getDisplayName(TextStyle.FULL_STANDALONE, currentLocale())

@Composable
private fun currentLocale(): Locale = LocalConfiguration.current.locales[0]

@Preview
@Composable
private fun DashboardScreenPreview() {
    UtilityWiseTheme {
        DashboardScreen(
            state = dashboardStateFor(currentReportingMonth()),
            snackbarHostState = remember { SnackbarHostState() },
            onAddReadingClick = {},
            onReadingClick = {},
            onUsageClick = {},
            onBillsClick = {}
        )
    }
}

@Preview
@Composable
private fun SpendSummaryCardPreview() {
    val state = dashboardStateFor(currentReportingMonth())
    UtilityWiseTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SpendSummaryCard(
                month = state.month,
                monthlyTotal = state.monthlyTotal,
                changeFromPreviousMonth = state.changeFromPreviousMonth,
                utilitySummaries = state.utilitySummaries
            )
        }
    }
}

@Preview
@Composable
private fun HousematesCardPreview() {
    val state = dashboardStateFor(currentReportingMonth())
    UtilityWiseTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HousematesCard(
                housemates = state.housemates,
                unpaidBillsTotal = state.unpaidBillsTotal
            )
        }
    }
}

@Preview
@Composable
private fun ReadingRowPreview() {
    val state = dashboardStateFor(currentReportingMonth())
    UtilityWiseTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            ReadingRow(summary = state.latestReadings.first(), onClick = {})
        }
    }
}
