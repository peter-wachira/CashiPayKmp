package com.peterwachira.cashipay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.peterwachira.cashipay.presentation.navigation.CashiPayDestination
import com.peterwachira.cashipay.presentation.navigation.CashiPayNavHost
import com.peterwachira.cashipay.presentation.navigation.CashiPayNavigationBar
import com.peterwachira.cashipay.presentation.theme.CashiPayTheme

/**
 * Hosts the Android Compose content for CashiPay.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            CashiPayApp()
        }
    }
}

/**
 * Defines the root of the CashiPay Compose hierarchy.
 */
@Composable
internal fun CashiPayApp() {
    CashiPayTheme {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        val showNavigationBar = currentRoute == CashiPayDestination.Home.route ||
            currentRoute == CashiPayDestination.Activity.route

        Scaffold(
            bottomBar = {
                if (showNavigationBar) {
                    CashiPayNavigationBar(
                        currentRoute = currentRoute,
                        onDestinationClick = { destination ->
                            navController.navigate(destination.route) {
                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { contentPadding ->
            CashiPayNavHost(
                navController = navController,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}
