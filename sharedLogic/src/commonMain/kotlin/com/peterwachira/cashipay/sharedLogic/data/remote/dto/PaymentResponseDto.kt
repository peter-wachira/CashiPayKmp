package com.peterwachira.cashipay.sharedLogic.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Represents a successfully processed payment returned by the remote API.
 */
@Serializable
internal data class PaymentResponseDto(
    val id: String,
    val recipientEmail: String,
    val amountMinor: Long,
    val currencyCode: String,
    val createdAtMillis: Long
)
