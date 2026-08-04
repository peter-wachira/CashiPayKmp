package com.peterwachira.cashipay.sharedLogic.domain.validation

import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationResult
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class PaymentValidatorTest {

    @Test
    fun `when payment input is valid then validation returns a payment request`() {
        // Given
        val input = PaymentInput(
            recipientEmail = "customer@example.com",
            amount = "100.50",
            currencyCode = "USD"
        )

        // When
        val result = PaymentValidator.validate(input)

        // Then
        assertTrue(result is PaymentValidationResult.Valid)
        assertEquals("customer@example.com", result.paymentRequest.recipientEmail)
        assertEquals(
            Money(amountMinor = 10_050L, currency = PaymentCurrency.USD),
            result.paymentRequest.amount
        )
    }

    @Test
    fun `when email is blank then validation returns recipient email required`() {
        // Given
        val input = PaymentInput(
            recipientEmail = "",
            amount = "50",
            currencyCode = "EUR"
        )

        // When
        val result = PaymentValidator.validate(input)

        // Then
        assertTrue(result is PaymentValidationResult.Invalid)
        assertTrue(result.errors.contains(PaymentValidationError.RecipientEmailRequired))
    }

    @Test
    fun `when email is invalid then validation returns invalid recipient email`() {
        // Given
        val input = PaymentInput(
            recipientEmail = "invalid-email",
            amount = "50",
            currencyCode = "EUR"
        )

        // When
        val result = PaymentValidator.validate(input)

        // Then
        assertTrue(result is PaymentValidationResult.Invalid)
        assertTrue(result.errors.contains(PaymentValidationError.InvalidRecipientEmail))
    }

    @Test
    fun `when amount is zero then validation returns amount greater than zero error`() {
        // Given
        val input = PaymentInput(
            recipientEmail = "customer@example.com",
            amount = "0",
            currencyCode = "USD"
        )

        // When
        val result = PaymentValidator.validate(input)

        // Then
        assertTrue(result is PaymentValidationResult.Invalid)
        assertTrue(result.errors.contains(PaymentValidationError.AmountMustBeGreaterThanZero))
    }

    @Test
    fun `when amount has too many decimal places then validation returns invalid amount`() {
        // Given
        val input = PaymentInput(
            recipientEmail = "customer@example.com",
            amount = "100.123",
            currencyCode = "USD"
        )

        // When
        val result = PaymentValidator.validate(input)

        // Then
        assertTrue(result is PaymentValidationResult.Invalid)
        assertTrue(result.errors.contains(PaymentValidationError.InvalidAmount))
    }

    @Test
    fun `when amount exceeds minor unit range then validation returns invalid amount`() {
        // Given
        val input = PaymentInput(
            recipientEmail = "customer@example.com",
            amount = "92233720368547758.08",
            currencyCode = "USD"
        )

        // When
        val result = PaymentValidator.validate(input)

        // Then
        assertTrue(result is PaymentValidationResult.Invalid)
        assertTrue(result.errors.contains(PaymentValidationError.InvalidAmount))
    }

    @Test
    fun `when currency is unsupported then validation returns unsupported currency`() {
        // Given
        val input = PaymentInput(
            recipientEmail = "customer@example.com",
            amount = "50",
            currencyCode = "KES"
        )

        // When
        val result = PaymentValidator.validate(input)

        // Then
        assertTrue(result is PaymentValidationResult.Invalid)
        assertTrue(result.errors.contains(PaymentValidationError.UnsupportedCurrency))
    }
}