package com.timothysisa.utilitywise.ui.dashboard

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WavingHand
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.timothysisa.utilitywise.R
import com.timothysisa.utilitywise.model.Housemate
import com.timothysisa.utilitywise.model.UtilityType
import com.timothysisa.utilitywise.ui.icon
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

@Composable
fun DashboardScreen(
    state: DashboardState,
    onRefreshClick: () -> Unit,
    onShareClick: () -> Unit,
    onSeeAllReadingsClick: () -> Unit,
    onAddReadingClick: () -> Unit,
    onUsageClick: () -> Unit,
    onBillsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(
                        start = Dimens.ScreenPadding,
                        top = Dimens.SpacingSmall,
                        end = Dimens.ScreenPadding,
                        bottom = Dimens.ScreenBottomPadding
                    )
            ) {
                DashboardTopBar(onRefreshClick = onRefreshClick)
                Spacer(modifier = Modifier.height(Dimens.CardPaddingVertical))
                SpendSummaryCard(
                    month = state.month,
                    monthlyTotal = state.monthlyTotal,
                    changeFromPreviousMonth = state.changeFromPreviousMonth,
                    utilitySummaries = state.utilitySummaries,
                    onShareClick = onShareClick
                )
                QuickActionRow(
                    onAddReadingClick = onAddReadingClick,
                    onUsageClick = onUsageClick,
                    onBillsClick = onBillsClick,
                    onShareClick = onShareClick,
                    modifier = Modifier.padding(vertical = Dimens.SpacingLarge)
                )
                HousematesCard(
                    housemates = state.housemates,
                    unpaidBillsTotal = state.unpaidBillsTotal
                )
                RecentReadingsHeader(onSeeAllClick = onSeeAllReadingsClick)
                RecentReadingsCard(readings = state.latestReadings)
            }
            FloatingNavigationBar(
                onAddReadingClick = onAddReadingClick,
                onUsageClick = onUsageClick,
                onBillsClick = onBillsClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = Dimens.SpacingLarge)
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
    onShareClick: () -> Unit,
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
        Box {
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
                // Keeps the figures clear of the share tab on the card's right edge.
                Column(modifier = Modifier.padding(end = Dimens.ShareTabWidth)) {
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
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = Dimens.ShareTabTopOffset, bottom = Dimens.HeroPadding),
                contentAlignment = Alignment.CenterEnd
            ) {
                ShareTab(onClick = onShareClick)
            }
        }
    }
}

@Composable
fun ReadingRow(summary: ReadingSummary, modifier: Modifier = Modifier) {
    val locale = currentLocale()
    val dateFormatter = remember(locale) { DateTimeFormatter.ofPattern(READING_DATE_PATTERN, locale) }
    Row(
        modifier = modifier.padding(vertical = Dimens.RowPaddingVertical),
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
private fun DashboardTopBar(onRefreshClick: () -> Unit) {
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
        Surface(
            onClick = onRefreshClick,
            shape = RoundedCornerShape(Dimens.CornerRadiusMedium),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = Dimens.SpacingLarge,
                    vertical = Dimens.RowPaddingVertical
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dashboard_shake),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.width(Dimens.SpacingTiny))
                Icon(
                    imageVector = Icons.Rounded.Refresh,
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
private fun ShareTab(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxHeight()
            .width(Dimens.ShareTabWidth),
        shape = RoundedCornerShape(
            topStart = Dimens.CornerRadiusLarge,
            bottomStart = Dimens.CornerRadiusLarge
        ),
        color = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Rounded.IosShare,
                contentDescription = stringResource(R.string.dashboard_share_summary)
            )
        }
    }
}

@Composable
private fun QuickActionRow(
    onAddReadingClick: () -> Unit,
    onUsageClick: () -> Unit,
    onBillsClick: () -> Unit,
    onShareClick: () -> Unit,
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
        QuickAction(
            icon = Icons.Rounded.IosShare,
            labelRes = R.string.dashboard_action_share,
            onClick = onShareClick,
            containerColor = MaterialTheme.colorScheme.secondary
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
private fun RecentReadingsHeader(onSeeAllClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.dashboard_recent_readings),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )
        TextButton(onClick = onSeeAllClick) {
            Text(
                text = stringResource(R.string.dashboard_see_all),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
private fun RecentReadingsCard(readings: List<ReadingSummary>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = Dimens.CardPaddingHorizontal,
                vertical = Dimens.SpacingTiny
            )
        ) {
            readings.forEachIndexed { index, reading ->
                if (index > 0) {
                    HorizontalDivider(
                        thickness = Dimens.DividerThickness,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                ReadingRow(summary = reading)
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
private fun FloatingNavigationBar(
    onAddReadingClick: () -> Unit,
    onUsageClick: () -> Unit,
    onBillsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.CornerRadiusNavigation),
        color = MaterialTheme.colorScheme.inverseSurface
    ) {
        Row(
            modifier = Modifier.padding(Dimens.NavigationPadding),
            horizontalArrangement = Arrangement.spacedBy(Dimens.NavigationItemSpacing)
        ) {
            FloatingNavigationItem(
                icon = Icons.Rounded.Home,
                labelRes = R.string.navigation_home,
                selected = true,
                onClick = {}
            )
            FloatingNavigationItem(
                icon = Icons.Rounded.Add,
                labelRes = R.string.dashboard_add_reading,
                selected = false,
                onClick = onAddReadingClick
            )
            FloatingNavigationItem(
                icon = Icons.Rounded.BarChart,
                labelRes = R.string.navigation_usage,
                selected = false,
                onClick = onUsageClick
            )
            FloatingNavigationItem(
                icon = Icons.AutoMirrored.Rounded.ReceiptLong,
                labelRes = R.string.navigation_bills,
                selected = false,
                onClick = onBillsClick
            )
        }
    }
}

@Composable
private fun FloatingNavigationItem(
    icon: ImageVector,
    @StringRes labelRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .width(Dimens.NavigationItemWidth)
            .heightIn(min = Dimens.NavigationItemHeight),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        color = if (selected) MaterialTheme.colorScheme.secondary else Color.Transparent,
        contentColor = if (selected) MaterialTheme.colorScheme.onSecondary else InkSoft
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = stringResource(labelRes))
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
            onRefreshClick = {},
            onShareClick = {},
            onSeeAllReadingsClick = {},
            onAddReadingClick = {},
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
                utilitySummaries = state.utilitySummaries,
                onShareClick = {}
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
            ReadingRow(summary = state.latestReadings.first())
        }
    }
}
