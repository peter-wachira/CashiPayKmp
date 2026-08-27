package com.peterwachira.cashipay.sharedLogic.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class PaymentValueTypesTest {

    @Test
    fun `when recipient email is valid then factory normalizes it`() {
        // Given
        val rawEmail = "  customer@example.com  "

        // When
        val recipientEmail = RecipientEmail.from(rawEmail)

        // Then
        assertNotNull(recipientEmail)
        assertEquals("customer@example.com", recipientEmail.value)
    }

    @Test
    fun `when recipient email is invalid then factory returns null`() {
        // Given
        val rawEmail = "invalid-email"

        // When
        val recipientEmail = RecipientEmail.from(rawEmail)

        // Then
        assertNull(recipientEmail)
    }

    @Test
    fun `when minor units are positive then factory creates value`() {
        // Given
        val rawAmount = 10_050L

        // When
        val minorUnits = MinorUnits.fromPositive(rawAmount)

        // Then
        assertNotNull(minorUnits)
        assertEquals(10_050L, minorUnits.value)
    }

    @Test
    fun `when minor units are not positive then factory returns null`() {
        // Given
        val zeroAmount = 0L
        val negativeAmount = -1L

        // When
        val zeroResult = MinorUnits.fromPositive(zeroAmount)
        val negativeResult = MinorUnits.fromPositive(negativeAmount)

        // Then
        assertNull(zeroResult)
        assertNull(negativeResult)
    }

    @Test
    fun `when transaction id is valid then factory normalizes it`() {
        // Given
        val rawId = "  transaction-1  "

        // When
        val transactionId = TransactionId.from(rawId)

        // Then
        assertNotNull(transactionId)
        assertEquals("transaction-1", transactionId.value)
    }

    @Test
    fun `when transaction id is blank then factory returns null`() {
        // Given
        val rawId = "   "

        // When
        val transactionId = TransactionId.from(rawId)

        // Then
        assertNull(transactionId)
    }
}
