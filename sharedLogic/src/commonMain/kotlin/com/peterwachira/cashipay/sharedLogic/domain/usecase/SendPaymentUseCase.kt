package com.peterwachira.cashipay.sharedLogic.domain.usecase

import com.peterwachira.cashipay.sharedLogic.core.result.AppResult
import com.peterwachira.cashipay.sharedLogic.domain.repository.PaymentRepository
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationResult
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidator

/**
 * Sends a validated payment through the payment repository.
 */
class SendPaymentUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(input: PaymentInput): SendPaymentResult {
        return when (val validationResult = PaymentValidator.validate(input)) {
            is PaymentValidationResult.Invalid -> {
                SendPaymentResult.ValidationError(validationResult.errors)
            }

            is PaymentValidationResult.Valid -> {
                when (
                    val repositoryResult = paymentRepository.sendPayment(
                        validationResult.paymentRequest
                    )
                ) {
                    is AppResult.Success -> {
                        SendPaymentResult.Success(repositoryResult.data)
                    }

                    is AppResult.Error -> {
                        SendPaymentResult.Failure(repositoryResult.message)
                    }
                }
            }
        }
    }
}