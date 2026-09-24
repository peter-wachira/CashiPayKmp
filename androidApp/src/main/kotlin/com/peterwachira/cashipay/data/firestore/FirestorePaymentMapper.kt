package com.peterwachira.cashipay.data.firestore

import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId

/**
 * Converts a domain transaction into fields that can be stored in Firestore.
 */
internal fun PaymentTransaction.toFirestoreDocument(): FirestorePaymentDocument {
    return FirestorePaymentDocument(
        recipientEmail = recipientEmail.value,
        amountMinor = amount.amountMinor.value,
        currencyCode = amount.currency.code,
        createdAtMillis = createdAtMillis
    )
}

/**
 * Converts valid Firestore fields and a document ID into a domain transaction.
 */
internal fun FirestorePaymentDocument.toDomain(
    documentId: String
): PaymentTransaction? {
    val transactionId = TransactionId.from(documentId) ?: return null
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
