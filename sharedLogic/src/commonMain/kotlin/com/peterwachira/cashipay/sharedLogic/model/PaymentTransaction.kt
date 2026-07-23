package com.peterwachira.cashipay.sharedLogic.model

data class PaymentTransaction(
    val id: String,
    val recipientEmail: String,
    val amount: String,
    val currency: PaymentCurrency,
    val createdAtMillis: Long
)