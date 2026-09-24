package com.peterwachira.cashipay.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.peterwachira.cashipay.presentation.ui.activity.TransactionDetailsRoute
import com.peterwachira.cashipay.presentation.ui.activity.TransactionHistoryRoute
import com.peterwachira.cashipay.presentation.ui.home.HomeRoute
import com.peterwachira.cashipay.presentation.ui.payment.PaymentRoute

/** Owns top-level navigation while destinations remain navigation-agnostic. */
@Composable
internal fun CashiPayNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = CashiPayDestination.Home.route,
        modifier = modifier
    ) {
        composable(CashiPayDestination.Home.route) {
            HomeRoute(
                onSendPaymentClick = {
                    navController.navigate(CashiPayDestination.SendPayment.route)
                },
                onViewAllClick = {
                    navController.navigate(CashiPayDestination.Activity.route)
                },
                onTransactionClick = { transaction ->
                    navController.navigate(
                        CashiPayDestination.TransactionDetails.createRoute(
                            transaction.id.value
                        )
                    )
                }
            )
        }

        composable(CashiPayDestination.SendPayment.route) {
            PaymentRoute(
                onBackClick = navController::navigateUp,
                onDoneClick = {
                    navController.navigate(CashiPayDestination.Home.route) {
                        popUpTo(CashiPayDestination.Home.route) {
                            inclusive = true
                        }
                    }
                },
                onViewDetails = { transaction ->
                    navController.navigate(
                        CashiPayDestination.TransactionDetails.createRoute(
                            transaction.id.value
                        )
                    )
                }
            )
        }

        composable(CashiPayDestination.Activity.route) {
            TransactionHistoryRoute(
                onTransactionClick = { transaction ->
                    navController.navigate(
                        CashiPayDestination.TransactionDetails.createRoute(
                            transaction.id.value
                        )
                    )
                }
            )
        }

        composable(
            route = CashiPayDestination.TransactionDetails.route,
            arguments = listOf(
                navArgument(CashiPayDestination.TRANSACTION_ID_ARGUMENT) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val transactionId = requireNotNull(
                backStackEntry.arguments?.getString(
                    CashiPayDestination.TRANSACTION_ID_ARGUMENT
                )
            )

            TransactionDetailsRoute(
                transactionId = transactionId,
                onBackClick = navController::navigateUp
            )
        }
    }
}
