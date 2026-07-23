package com.peterwachira.cashipay.sharedLogic.domain.validation

import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationResult
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaymentValidatorTest {

    @Test
    fun validPaymentInputReturnsValidPaymentRequest() {
        val input = PaymentInput(
            recipientEmail = "customer@example.com",
            amount = "100.50",
            currencyCode = "USD"
        )

        val result = PaymentValidator.validate(input)

        assertTrue(result is PaymentValidationResult.Valid)
        assertEquals("customer@example.com", result.paymentRequest.recipientEmail)
        assertEquals(100.50, result.paymentRequest.amount)
        assertEquals(PaymentCurrency.USD, result.paymentRequest.currency)
    }

    @Test
    fun blankEmailReturnsRecipientEmailRequiredError() {
        val input = PaymentInput(
            recipientEmail = "",
            amount = "50",
            currencyCode = "EUR"
        )

        val result = PaymentValidator.validate(input)

        assertTrue(result is PaymentValidationResult.Invalid)
        assertTrue(result.errors.contains(PaymentValidationError.RecipientEmailRequired))
    }

    @Test
    fun invalidEmailReturnsInvalidRecipientEmailError() {
        val input = PaymentInput(
            recipientEmail = "invalid-email",
            amount = "50",
            currencyCode = "EUR"
        )

        val result = PaymentValidator.validate(input)

        assertTrue(result is PaymentValidationResult.Invalid)
        assertTrue(result.errors.contains(PaymentValidationError.InvalidRecipientEmail))
    }

    @Test
    fun zeroAmountReturnsAmountMustBeGreaterThanZeroError() {
        val input = PaymentInput(
            recipientEmail = "customer@example.com",
            amount = "0",
            currencyCode = "USD"
        )

        val result = PaymentValidator.validate(input)

        assertTrue(result is PaymentValidationResult.Invalid)
        assertTrue(result.errors.contains(PaymentValidationError.AmountMustBeGreaterThanZero))
    }

    @Test
    fun unsupportedCurrencyReturnsUnsupportedCurrencyError() {
        val input = PaymentInput(
            recipientEmail = "customer@example.com",
            amount = "50",
            currencyCode = "KES"
        )

        val result = PaymentValidator.validate(input)

        assertTrue(result is PaymentValidationResult.Invalid)
        assertTrue(result.errors.contains(PaymentValidationError.UnsupportedCurrency))
    }
}