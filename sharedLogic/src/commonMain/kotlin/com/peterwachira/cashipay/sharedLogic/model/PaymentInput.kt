package com.peterwachira.cashipay.sharedLogic.model

data class PaymentInput(
    val recipientEmail: String,
    val amount: String,
    val currencyCode: String
)