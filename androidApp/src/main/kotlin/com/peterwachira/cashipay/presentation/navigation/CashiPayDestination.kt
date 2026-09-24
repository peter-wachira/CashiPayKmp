package com.peterwachira.cashipay.presentation.navigation

/** Defines the small, explicit set of destinations in the Android app. */
internal sealed class CashiPayDestination(val route: String) {
    data object Home : CashiPayDestination("home")
    data object SendPayment : CashiPayDestination("send-payment")
    data object Activity : CashiPayDestination("activity")
    data object TransactionDetails : CashiPayDestination(
        "transaction-details/{$TRANSACTION_ID_ARGUMENT}"
    ) {
        fun createRoute(transactionId: String): String {
            return "transaction-details/${android.net.Uri.encode(transactionId)}"
        }
    }

    companion object {
        const val TRANSACTION_ID_ARGUMENT = "transactionId"
    }
}
