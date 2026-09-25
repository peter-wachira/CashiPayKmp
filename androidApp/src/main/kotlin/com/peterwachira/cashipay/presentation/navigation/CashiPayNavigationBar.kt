package com.peterwachira.cashipay.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.peterwachira.cashipay.R

/** Displays the three primary app destinations. */
@Composable
internal fun CashiPayNavigationBar(
    currentRoute: String?,
    onDestinationClick: (CashiPayDestination) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = androidx.compose.ui.unit.Dp.Unspecified
    ) {
        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.destination.route,
                onClick = {
                    onDestinationClick(item.destination)
                },
                icon = {
                    Icon(
                        painter = painterResource(item.iconResource),
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(item.labelResource))
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

private data class NavigationItem(
    val destination: CashiPayDestination,
    @param:StringRes val labelResource: Int,
    @param:DrawableRes val iconResource: Int,
)

private val navigationItems = listOf(
    NavigationItem(
        destination = CashiPayDestination.Home,
        labelResource = R.string.navigation_home,
        iconResource = R.drawable.ic_home
    ),
    NavigationItem(
        destination = CashiPayDestination.SendPayment,
        labelResource = R.string.navigation_send,
        iconResource = R.drawable.ic_send
    ),
    NavigationItem(
        destination = CashiPayDestination.Activity,
        labelResource = R.string.navigation_activity,
        iconResource = R.drawable.ic_activity
    )
)
