package com.peterwachira.cashipay.sharedLogic.validation

import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest

/**
 * Represents the internal outcome of validating payment input.
 */
internal sealed interface PaymentValidationResult {
    data class Valid(val paymentRequest: PaymentRequest): PaymentValidationResult
    data class Invalid(val errors: List<PaymentValidationError>): PaymentValidationResult
}