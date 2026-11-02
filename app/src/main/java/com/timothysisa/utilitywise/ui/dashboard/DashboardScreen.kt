package com.timothysisa.utilitywise.ui.dashboard

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.timothysisa.utilitywise.R
import com.timothysisa.utilitywise.model.UtilityType
import com.timothysisa.utilitywise.ui.icon
import com.timothysisa.utilitywise.ui.theme.Dimens
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

private const val READING_DATE_PATTERN = "d MMM"

@Composable
fun DashboardScreen(
    state: DashboardState,
    onNotificationsClick: () -> Unit,
    onSeeAllReadingsClick: () -> Unit,
    onAddReadingClick: () -> Unit,
    onUsageClick: () -> Unit,
    onBillsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            DashboardNavigationBar(onUsageClick = onUsageClick, onBillsClick = onBillsClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(Dimens.SpacingExtraLarge))
                DashboardHeader(month = state.month, onNotificationsClick = onNotificationsClick)
                Spacer(modifier = Modifier.height(Dimens.SpacingHuge))
                MonthSummary(
                    monthlyTotal = state.monthlyTotal,
                    changeFromPreviousMonth = state.changeFromPreviousMonth,
                    previousMonth = state.month.minusMonths(1)
                )
                Spacer(modifier = Modifier.height(Dimens.SpacingExtraLarge))
                UtilityTileRow(summaries = state.utilitySummaries)
                Spacer(modifier = Modifier.height(Dimens.SpacingExtraLarge))
                LatestReadingsHeader(onSeeAllClick = onSeeAllReadingsClick)
                Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
                LatestReadingsCard(readings = state.latestReadings)
                Spacer(modifier = Modifier.height(Dimens.SpacingLarge))
                ShakeToRefreshHint()
                Spacer(modifier = Modifier.height(Dimens.SpacingLarge))
            }
            AddReadingButton(
                onClick = onAddReadingClick,
                modifier = Modifier.padding(vertical = Dimens.SpacingLarge)
            )
        }
    }
}

@Composable
fun MonthSummary(
    monthlyTotal: Double,
    changeFromPreviousMonth: Double,
    previousMonth: YearMonth,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.dashboard_spent_this_month),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.money_total, monthlyTotal),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
        MonthChangeChip(change = changeFromPreviousMonth, previousMonth = previousMonth)
    }
}

@Composable
fun UtilityTile(summary: UtilitySummary, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(Dimens.SpacingLarge)) {
            UtilityIconCircle(type = summary.type)
            Spacer(modifier = Modifier.height(Dimens.SpacingExtraLarge))
            Text(
                text = stringResource(summary.type.displayNameRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.money_amount, summary.cost),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = if (summary.type.hasMeter) {
                    unitsLabel(type = summary.type, units = summary.unitsUsed)
                } else {
                    stringResource(R.string.utility_fixed_charge)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ReadingRow(summary: ReadingSummary, modifier: Modifier = Modifier) {
    val locale = currentLocale()
    val dateFormatter = remember(locale) { DateTimeFormatter.ofPattern(READING_DATE_PATTERN, locale) }
    Row(
        modifier = modifier.padding(Dimens.SpacingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UtilityIconCircle(type = summary.type)
        Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(summary.type.displayNameRes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(
                    R.string.reading_units_on_date,
                    unitsLabel(type = summary.type, units = summary.unitsUsed),
                    summary.date.format(dateFormatter)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
        Text(
            text = stringResource(R.string.money_amount, summary.cost),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DashboardHeader(month: YearMonth, onNotificationsClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.dashboard_greeting),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.dashboard_month_so_far, monthName(month)),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        FilledIconButton(
            onClick = onNotificationsClick,
            modifier = Modifier.size(Dimens.IconButtonSize),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.dashboard_notifications)
            )
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
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface) {
        Text(
            text = changeText,
            modifier = Modifier.padding(
                horizontal = Dimens.SpacingLarge,
                vertical = Dimens.SpacingSmall
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun UtilityTileRow(summaries: List<UtilitySummary>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
    ) {
        summaries.forEach { summary ->
            UtilityTile(
                summary = summary,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun LatestReadingsHeader(onSeeAllClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.dashboard_latest_readings),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onSeeAllClick) {
            Text(
                text = stringResource(R.string.dashboard_see_all),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LatestReadingsCard(readings: List<ReadingSummary>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            readings.forEachIndexed { index, reading ->
                if (index > 0) {
                    // Inset so the divider starts under the text, not under the icon.
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            start = Dimens.SpacingLarge + Dimens.IconCircleSize + Dimens.SpacingMedium,
                            end = Dimens.SpacingLarge
                        ),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                ReadingRow(summary = reading)
            }
        }
    }
}

@Composable
private fun ShakeToRefreshHint() {
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier.padding(
                horizontal = Dimens.SpacingLarge,
                vertical = Dimens.SpacingSmall
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Vibration,
                contentDescription = null,
                modifier = Modifier.size(Dimens.IconSizeSmall),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
            Text(
                text = stringResource(R.string.dashboard_shake_to_refresh),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddReadingButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ButtonHeight)
    ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
        Text(
            text = stringResource(R.string.dashboard_add_reading),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun UtilityIconCircle(type: UtilityType) {
    Box(
        modifier = Modifier
            .size(Dimens.IconCircleSize)
            .background(color = MaterialTheme.colorScheme.secondary, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = type.icon,
            contentDescription = null,
            modifier = Modifier.size(Dimens.IconSizeSmall),
            tint = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
private fun DashboardNavigationBar(onUsageClick: () -> Unit, onBillsClick: () -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        DashboardNavigationItem(
            icon = Icons.Outlined.Home,
            labelRes = R.string.navigation_home,
            selected = true,
            onClick = {}
        )
        DashboardNavigationItem(
            icon = Icons.Outlined.BarChart,
            labelRes = R.string.navigation_usage,
            selected = false,
            onClick = onUsageClick
        )
        DashboardNavigationItem(
            icon = Icons.AutoMirrored.Outlined.ReceiptLong,
            labelRes = R.string.navigation_bills,
            selected = false,
            onClick = onBillsClick
        )
    }
}

@Composable
private fun RowScope.DashboardNavigationItem(
    icon: ImageVector,
    @StringRes labelRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(imageVector = icon, contentDescription = null) },
        label = { Text(text = stringResource(labelRes)) },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.primary,
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
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
            onNotificationsClick = {},
            onSeeAllReadingsClick = {},
            onAddReadingClick = {},
            onUsageClick = {},
            onBillsClick = {}
        )
    }
}

@Preview
@Composable
private fun MonthSummaryPreview() {
    val state = dashboardStateFor(currentReportingMonth())
    UtilityWiseTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MonthSummary(
                monthlyTotal = state.monthlyTotal,
                changeFromPreviousMonth = state.changeFromPreviousMonth,
                previousMonth = state.month.minusMonths(1)
            )
        }
    }
}

@Preview
@Composable
private fun UtilityTilePreview() {
    val state = dashboardStateFor(currentReportingMonth())
    UtilityWiseTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            UtilityTile(summary = state.utilitySummaries.first())
        }
    }
}

@Preview
@Composable
private fun ReadingRowPreview() {
    val state = dashboardStateFor(currentReportingMonth())
    UtilityWiseTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            ReadingRow(summary = state.latestReadings.first())
        }
    }
}
