package com.peterwachira.cashipay.sharedLogic.model

/**
 * Represents a monetary value as minor units paired with its currency.
 */
data class Money(
    val amountMinor: MinorUnits,
    val currency: PaymentCurrency
)