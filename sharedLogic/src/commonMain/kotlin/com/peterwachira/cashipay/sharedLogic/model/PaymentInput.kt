package com.peterwachira.cashipay.sharedLogic.model

/**
 * Represents the unvalidated payment values entered by a user.
 */
data class PaymentInput(
    val recipientEmail: String,
    val amount: String,
    val currencyCode: String
)