package com.peterwachira.cashipay.data.firestore

import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Verifies conversion between Firestore documents and payment domain models.
 */
internal class FirestorePaymentMapperTest {

    @Test
    fun `when transaction is mapped then document contains storage fields`() {
        // Given
        val transaction = validTransaction()

        // When
        val result = transaction.toFirestoreDocument()

        // Then
        assertEquals(
            FirestorePaymentDocument(
                recipientEmail = "customer@example.com",
                amountMinor = 10_050L,
                currencyCode = "USD",
                createdAtMillis = 1_000L
            ),
            result
        )
    }

    @Test
    fun `when document is valid then mapping returns transaction`() {
        // Given
        val document = validDocument()

        // When
        val result = document.toDomain(
            documentId = "transaction-1"
        )

        // Then
        assertEquals(validTransaction(), result)
    }

    @Test
    fun `when document id is blank then mapping returns null`() {
        // Given
        val document = validDocument()

        // When
        val result = document.toDomain(
            documentId = "   "
        )

        // Then
        assertNull(result)
    }

    @Test
    fun `when document email is invalid then mapping returns null`() {
        // Given
        val document = validDocument().copy(
            recipientEmail = "invalid-email"
        )

        // When
        val result = document.toDomain(
            documentId = "transaction-1"
        )

        // Then
        assertNull(result)
    }

    @Test
    fun `when document amount is not positive then mapping returns null`() {
        // Given
        val document = validDocument().copy(
            amountMinor = 0L
        )

        // When
        val result = document.toDomain(
            documentId = "transaction-1"
        )

        // Then
        assertNull(result)
    }

    @Test
    fun `when document currency is unsupported then mapping returns null`() {
        // Given
        val document = validDocument().copy(
            currencyCode = "KES"
        )

        // When
        val result = document.toDomain(
            documentId = "transaction-1"
        )

        // Then
        assertNull(result)
    }

    private fun validDocument(): FirestorePaymentDocument {
        return FirestorePaymentDocument(
            recipientEmail = "customer@example.com",
            amountMinor = 10_050L,
            currencyCode = "USD",
            createdAtMillis = 1_000L
        )
    }

    private fun validTransaction(): PaymentTransaction {
        return PaymentTransaction(
            id = requireNotNull(
                TransactionId.from("transaction-1")
            ),
            recipientEmail = requireNotNull(
                RecipientEmail.from("customer@example.com")
            ),
            amount = Money(
                amountMinor = requireNotNull(
                    MinorUnits.fromPositive(10_050L)
                ),
                currency = PaymentCurrency.USD
            ),
            createdAtMillis = 1_000L
        )
    }
}
