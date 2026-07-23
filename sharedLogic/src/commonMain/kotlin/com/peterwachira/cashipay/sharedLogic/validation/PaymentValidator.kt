package com.peterwachira.cashipay.sharedLogic.validation

import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest

object PaymentValidator {

    private val emailRegex = Regex(
        pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )

    fun validate(input: PaymentInput): PaymentValidationResult {
        val errors = mutableListOf<PaymentValidationError>()

        val trimmedEmail = input.recipientEmail.trim()
        val trimmedAmount = input.amount.trim()
        val currency = PaymentCurrency.fromCode(input.currencyCode)

        if (trimmedEmail.isBlank()) {
            errors += PaymentValidationError.RecipientEmailRequired
        } else if (!emailRegex.matches(trimmedEmail)) {
            errors += PaymentValidationError.InvalidRecipientEmail
        }

        val parsedAmount = if (trimmedAmount.isBlank()) {
            errors += PaymentValidationError.AmountRequired
            null
        } else {
            trimmedAmount.toDoubleOrNull()
        }

        if (trimmedAmount.isNotBlank() && parsedAmount == null) {
            errors += PaymentValidationError.InvalidAmount
        }

        if (parsedAmount != null && parsedAmount <= 0.0) {
            errors += PaymentValidationError.AmountMustBeGreaterThanZero
        }

        if (currency == null) {
            errors += PaymentValidationError.UnsupportedCurrency
        }

        return if (errors.isEmpty() && parsedAmount != null && currency != null) {
            PaymentValidationResult.Valid(
                paymentRequest = PaymentRequest(
                    recipientEmail = trimmedEmail,
                    amount = parsedAmount,
                    currency = currency
                )
            )
        } else {
            PaymentValidationResult.Invalid(errors)
        }
    }
}