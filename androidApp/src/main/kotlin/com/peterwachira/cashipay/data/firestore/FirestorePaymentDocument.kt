package com.peterwachira.cashipay.data.firestore

/**
 * Represents the transaction fields stored in a Firestore document.
 */
internal data class FirestorePaymentDocument(
    val recipientEmail: String = "",
    val amountMinor: Long = 0L,
    val currencyCode: String = "",
    val createdAtMillis: Long = 0L
)
