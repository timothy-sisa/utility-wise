package com.timothysisa.utilitywise.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.timothysisa.utilitywise.R
import com.timothysisa.utilitywise.ui.theme.Dimens
import com.timothysisa.utilitywise.ui.theme.InkSoft

enum class MainNavigationDestination {
    HOME,
    BILLS
}

/** The floating navigation shared by the dashboard and bill management screens. */
@Composable
fun FloatingNavigationBar(
    selectedDestination: MainNavigationDestination,
    onHomeClick: () -> Unit,
    onAddReadingClick: () -> Unit,
    onUsageClick: () -> Unit,
    onBillsClick: () -> Unit,
    modifier: Modifier = Modifier,
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
                selected = selectedDestination == MainNavigationDestination.HOME,
                onClick = onHomeClick
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
                selected = selectedDestination == MainNavigationDestination.BILLS,
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
