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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class PaymentDtoMapperTest {

    @Test
    fun `when payment request is mapped then dto contains transport values`() {
        // Given
        val request = validPaymentRequest()

        // When
        val result = request.toDto()

        // Then
        assertEquals(
            PaymentRequestDto(
                recipientEmail = "peterwachira@email.com",
                amountMinor = 10_050L,
                currencyCode = "USD"
            ),
            result
        )
    }

    @Test
    fun `when response is valid then mapping returns transaction`() {
        // Given
        val response = validResponse()

        // When
        val result = response.toDomain()

        // Then
        assertEquals(validTransaction(), result)
    }

    @Test
    fun `when response id is blank then mapping returns null`() {
        // Given
        val response = validResponse().copy(id = "   ")

        // When
        val result = response.toDomain()

        // Then
        assertNull(result)
    }

    @Test
    fun `when response email is invalid then mapping returns null`() {
        // Given
        val response = validResponse().copy(
            recipientEmail = "invalid-email"
        )

        // When
        val result = response.toDomain()

        // Then
        assertNull(result)
    }

    @Test
    fun `when response amount is not positive then mapping returns null`() {
        // Given
        val response = validResponse().copy(amountMinor = 0L)

        // When
        val result = response.toDomain()

        // Then
        assertNull(result)
    }

    @Test
    fun `when response currency is unsupported then mapping returns null`() {
        // Given
        val response = validResponse().copy(currencyCode = "KES")

        // When
        val result = response.toDomain()

        // Then
        assertNull(result)
    }

    private fun validPaymentRequest(): PaymentRequest {
        return PaymentRequest(
            recipientEmail = requireNotNull(
                RecipientEmail.from("peterwachira@email.com")
            ),
            amount = Money(
                amountMinor = requireNotNull(
                    MinorUnits.fromPositive(10_050L)
                ),
                currency = PaymentCurrency.USD
            )
        )
    }

    private fun validResponse(): PaymentResponseDto {
        return PaymentResponseDto(
            id = "transaction-1",
            recipientEmail = "peterwachira@email.com",
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
                RecipientEmail.from("peterwachira@email.com")
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
