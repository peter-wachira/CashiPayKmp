package com.peterwachira.cashipay.sharedLogic.validation

import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail

/**
 * Validates payment input and maps valid values to a payment request.
 */
internal object PaymentValidator {

    private val emailRegex = Regex(
        pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )

    fun validate(input: PaymentInput): PaymentValidationResult {
        val errors = mutableListOf<PaymentValidationError>()

        val trimmedEmail = input.recipientEmail.trim()
        val trimmedAmount = input.amount.trim()
        val currency = PaymentCurrency.fromCode(input.currencyCode)

        val recipientEmail = RecipientEmail.from(trimmedEmail)

        if (trimmedEmail.isBlank()) {
            errors += PaymentValidationError.RecipientEmailRequired
        } else if (recipientEmail == null) {
            errors += PaymentValidationError.InvalidRecipientEmail
        }
        val parsedAmountMinor = if (trimmedAmount.isBlank()) {
            errors += PaymentValidationError.AmountRequired
            null
        } else {
            trimmedAmount.toMinorUnits(
                fractionDigits = currency?.fractionDigits ?: DEFAULT_FRACTION_DIGITS
            )
        }

        if (trimmedAmount.isNotBlank() && parsedAmountMinor == null) {
            errors += PaymentValidationError.InvalidAmount
        }
        val minorUnits = if (parsedAmountMinor != null) {
            MinorUnits.fromPositive(parsedAmountMinor)
        } else {
            null
        }

        if (parsedAmountMinor != null && minorUnits == null) {
            errors += PaymentValidationError.AmountMustBeGreaterThanZero
        }
        if (currency == null) {
            errors += PaymentValidationError.UnsupportedCurrency
        }

        return if (
            errors.isEmpty() &&
            recipientEmail != null &&
            minorUnits != null &&
            currency != null
        ) {
            PaymentValidationResult.Valid(
                paymentRequest = PaymentRequest(
                    recipientEmail = recipientEmail,
                    amount = Money(
                        amountMinor = minorUnits,
                        currency = currency
                    )
                )
            )
        } else {
            PaymentValidationResult.Invalid(errors)
        }
    }

    private fun String.toMinorUnits(fractionDigits: Int): Long? {
        val isNegative = startsWith("-")
        val unsignedValue = removePrefix("-")
        val parts = unsignedValue.split('.')

        if (parts.size > 2 || unsignedValue.isEmpty()) return null

        val wholePart = parts[0].ifEmpty { "0" }
        val fractionalPart = parts.getOrElse(1) { "" }

        if (
            !wholePart.all(Char::isDigit) ||
            !fractionalPart.all(Char::isDigit) ||
            fractionalPart.length > fractionDigits
        ) {
            return null
        }

        val multiplier = tenToThePowerOf(fractionDigits)
        val wholeMinor = wholePart.toLongOrNull() ?: return null
        val fractionalMinor = fractionalPart
            .padEnd(fractionDigits, '0')
            .ifEmpty { "0" }
            .toLongOrNull()
            ?: return null

        if (wholeMinor > (Long.MAX_VALUE - fractionalMinor) / multiplier) return null

        val amountMinor = (wholeMinor * multiplier) + fractionalMinor
        return if (isNegative) -amountMinor else amountMinor
    }

    private fun tenToThePowerOf(exponent: Int): Long {
        var result = 1L
        repeat(exponent) {
            result *= 10L
        }
        return result
    }

    private const val DEFAULT_FRACTION_DIGITS = 2
}
