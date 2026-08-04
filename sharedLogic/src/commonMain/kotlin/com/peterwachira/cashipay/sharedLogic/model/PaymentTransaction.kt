package com.peterwachira.cashipay.sharedLogic.model

/**
 * Represents a payment transaction returned after successful processing.
 */
data class PaymentTransaction(
    val id: TransactionId,
    val recipientEmail: RecipientEmail,
    val amount: Money,
    val createdAtMillis: Long
)