package com.peterwachira.cashipay.sharedLogic.domain.usecase

import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationResult
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidator

/**
 * Validates payment input before it is shown for review.
 */
class ValidatePaymentUseCase {
    operator fun invoke(input: PaymentInput): ValidatePaymentResult {
        return when (val result = PaymentValidator.validate(input)) {
            is PaymentValidationResult.Valid -> {
                ValidatePaymentResult.Valid(
                    paymentRequest = result.paymentRequest
                )
            }

            is PaymentValidationResult.Invalid -> {
                ValidatePaymentResult.Invalid(
                    errors = result.errors
                )
            }
        }
    }
}
