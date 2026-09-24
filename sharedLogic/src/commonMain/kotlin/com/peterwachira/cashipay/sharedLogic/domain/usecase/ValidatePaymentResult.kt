package com.peterwachira.cashipay.sharedLogic.domain.usecase

import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError

/**
 * Describes whether payment input is ready for review.
 */
sealed interface ValidatePaymentResult {
    data class Valid(
        val paymentRequest: PaymentRequest,
    ) : ValidatePaymentResult

    data class Invalid(
        val errors: List<PaymentValidationError>,
    ) : ValidatePaymentResult
}
