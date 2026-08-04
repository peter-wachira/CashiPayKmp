package com.peterwachira.cashipay.sharedLogic.model

/**
 * Represents a payment transaction returned after successful processing.
 */
data class PaymentTransaction(
    val id: String,
    val recipientEmail: String,
    val amount: Money,
    val createdAtMillis: Long
)