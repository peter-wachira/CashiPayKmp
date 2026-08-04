package com.peterwachira.cashipay.sharedLogic.data.remote.mapper

import com.peterwachira.cashipay.sharedLogic.data.remote.dto.PaymentRequestDto
import com.peterwachira.cashipay.sharedLogic.data.remote.dto.PaymentResponseDto
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId

/**
 * Maps payment data between remote API and domain representations.
 */
internal fun PaymentRequest.toDto(): PaymentRequestDto = PaymentRequestDto(
    recipientEmail = recipientEmail.value,
    amountMinor = amount.amountMinor.value,
    currencyCode = amount.currency.code
)

/**
 * Converts a valid remote payment response to a domain transaction.
 */
internal fun PaymentResponseDto.toDomain(): PaymentTransaction? {
    val transactionId = TransactionId.from(id) ?: return null
    val recipientEmail = RecipientEmail.from(recipientEmail) ?: return null
    val minorUnits = MinorUnits.fromPositive(amountMinor) ?: return null
    val currency = PaymentCurrency.fromCode(currencyCode) ?: return null

    return PaymentTransaction(
        id = transactionId,
        recipientEmail = recipientEmail,
        amount = Money(
            amountMinor = minorUnits,
            currency = currency
        ),
        createdAtMillis = createdAtMillis
    )
}
