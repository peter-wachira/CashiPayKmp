package com.peterwachira.cashipay.presentation.payment

import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency

/**
 * Defines the user actions accepted by the payment workflow.
 */
internal sealed interface PaymentUiAction {
    data class RecipientEmailChanged(
        val value: String,
    ) : PaymentUiAction

    data class AmountChanged(
        val value: String,
    ) : PaymentUiAction

    data class CurrencySelected(
        val currency: PaymentCurrency,
    ) : PaymentUiAction

    data object ReviewPayment : PaymentUiAction

    data object EditPayment : PaymentUiAction

    data object ConfirmPayment : PaymentUiAction

    data object DismissFeedback : PaymentUiAction
}
