package com.peterwachira.cashipay.sharedLogic.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Represents the payment payload sent to the remote API.
 */
@Serializable
internal data class PaymentRequestDto(
    val recipientEmail: String,
    val amountMinor: Long,
    val currencyCode: String
)
