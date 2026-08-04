package com.peterwachira.cashipay.sharedLogic.data.remote.mapper

import com.peterwachira.cashipay.sharedLogic.data.remote.dto.PaymentRequestDto
import com.peterwachira.cashipay.sharedLogic.data.remote.dto.PaymentResponseDto
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction

/**
 * Maps payment data between remote API and domain representations.
 */
internal fun PaymentRequest.toDto(): PaymentRequestDto = PaymentRequestDto(
    recipientEmail = recipientEmail,
    amountMinor = amount.amountMinor,
    currencyCode = amount.currency.code
)

/**
 * Converts a valid remote payment response to a domain transaction.
 */
internal fun PaymentResponseDto.toDomain(): PaymentTransaction? {
    val currency = PaymentCurrency.fromCode(currencyCode) ?: return null

    if (id.isBlank() || recipientEmail.isBlank() || amountMinor <= 0L) {
        return null
    }

    return PaymentTransaction(
        id = id,
        recipientEmail = recipientEmail,
        amount = Money(
            amountMinor = amountMinor,
            currency = currency
        ),
        createdAtMillis = createdAtMillis
    )
}
