package com.peterwachira.cashipay.sharedLogic.domain.usecase

import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Verifies payment validation before the review step.
 */
internal class ValidatePaymentUseCaseTest {

    private val useCase = ValidatePaymentUseCase()

    @Test
    fun `when payment input is valid then validated request is returned`() {
        // Given
        val input = PaymentInput(
            recipientEmail = "customer@example.com",
            amount = "45.50",
            currencyCode = "EUR"
        )

        // When
        val result = useCase(input)

        // Then
        assertIs<ValidatePaymentResult.Valid>(result)
        assertEquals(
            "customer@example.com",
            result.paymentRequest.recipientEmail.value
        )
        assertEquals(
            4_550L,
            result.paymentRequest.amount.amountMinor.value
        )
        assertEquals(
            PaymentCurrency.EUR,
            result.paymentRequest.amount.currency
        )
    }

    @Test
    fun `when payment input is invalid then validation errors are returned`() {
        // Given
        val input = PaymentInput(
            recipientEmail = "invalid-email",
            amount = "0",
            currencyCode = "USD"
        )

        // When
        val result = useCase(input)

        // Then
        assertIs<ValidatePaymentResult.Invalid>(result)
        assertEquals(
            listOf(
                PaymentValidationError.InvalidRecipientEmail,
                PaymentValidationError.AmountMustBeGreaterThanZero
            ),
            result.errors
        )
    }
}
