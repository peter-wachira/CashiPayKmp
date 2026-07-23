package com.peterwachira.cashipay.sharedLogic.model

data class PaymentRequest(
    val recipientEmail: String,
    val amount: Double,
    val currency: PaymentCurrency
)