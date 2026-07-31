package com.peterwachira.cashipay.sharedLogic.domain.usecase

import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError

/**
 * Describes the outcome of attempting to send a payment.
 */
sealed interface SendPaymentResult {
    data class Success(
        val transaction: PaymentTransaction
    ): SendPaymentResult

    data class ValidationError(
        val errors: List<PaymentValidationError>
    ): SendPaymentResult

    data class Failure(
        val message: String
    ): SendPaymentResult
}