package com.peterwachira.cashipay.presentation.payment

import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency

/**
 * Defines the user actions accepted by the payment screen.
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

    data object Submit : PaymentUiAction
    data object DismissFeedback : PaymentUiAction
}
