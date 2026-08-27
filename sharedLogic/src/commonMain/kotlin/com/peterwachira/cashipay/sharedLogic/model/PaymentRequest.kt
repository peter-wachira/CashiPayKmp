package com.peterwachira.cashipay.sharedLogic.model

/**
 * Represents a validated payment ready to be sent to a repository.
 */
data class PaymentRequest(
    val recipientEmail: RecipientEmail,
    val amount: Money
)