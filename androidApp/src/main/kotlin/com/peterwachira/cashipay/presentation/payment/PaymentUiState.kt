package com.peterwachira.cashipay.presentation.payment

import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError

/**
 * Represents everything the payment workflow needs to render.
 */
internal data class PaymentUiState(
    val recipientEmail: String = "",
    val amount: String = "",
    val selectedCurrency: PaymentCurrency = PaymentCurrency.USD,
    val validationErrors: List<PaymentValidationError> = emptyList(),
    val paymentToReview: PaymentRequest? = null,
    val isSubmitting: Boolean = false,
    val submittedTransaction: PaymentTransaction? = null,
    val submissionError: String? = null,
)
