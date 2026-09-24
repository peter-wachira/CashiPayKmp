package com.peterwachira.cashipay.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peterwachira.cashipay.sharedLogic.domain.usecase.SendPaymentResult
import com.peterwachira.cashipay.sharedLogic.domain.usecase.SendPaymentUseCase
import com.peterwachira.cashipay.sharedLogic.domain.usecase.ValidatePaymentResult
import com.peterwachira.cashipay.sharedLogic.domain.usecase.ValidatePaymentUseCase
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates payment actions and exposes immutable workflow state.
 */
internal class PaymentViewModel(
    private val validatePaymentUseCase: ValidatePaymentUseCase,
    private val sendPaymentUseCase: SendPaymentUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun onAction(action: PaymentUiAction) {
        when (action) {
            is PaymentUiAction.RecipientEmailChanged -> {
                updateRecipientEmail(action.value)
            }

            is PaymentUiAction.AmountChanged -> {
                updateAmount(action.value)
            }

            is PaymentUiAction.CurrencySelected -> {
                updateCurrency(action.currency)
            }

            PaymentUiAction.DismissFeedback -> {
                dismissFeedback()
            }

            PaymentUiAction.ConfirmPayment -> {
                confirmPayment()
            }

            PaymentUiAction.EditPayment -> {
                editPayment()
            }

            PaymentUiAction.ReviewPayment -> {
                reviewPayment()
            }
        }
    }

    private fun reviewPayment() {
        val paymentInput = createPaymentInput()
        val result = validatePaymentUseCase(paymentInput)
        when (result) {
            is ValidatePaymentResult.Valid -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        validationErrors = emptyList(),
                        paymentToReview = result.paymentRequest,
                        submissionError = null
                    )
                }
            }

            is ValidatePaymentResult.Invalid -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        validationErrors = result.errors,
                        paymentToReview = null,
                        submissionError = null
                    )
                }
            }
        }
    }

    private fun editPayment() {
        _uiState.update { currentState ->
            currentState.copy(
                paymentToReview = null,
                submissionError = null
            )
        }
    }

    private fun confirmPayment() {
        val currentState = _uiState.value
        if (currentState.paymentToReview == null || currentState.isSubmitting) {
            return
        }
        val paymentInput = createPaymentInput()
        _uiState.update { state ->
            state.copy(
                isSubmitting = true,
                validationErrors = emptyList(),
                submissionError = null,
                submittedTransaction = null
            )
        }

        viewModelScope.launch {
            val result = sendPaymentUseCase(paymentInput)
            handlePaymentResult(result)
        }
    }

    private fun createPaymentInput(): PaymentInput {
        val currentState = _uiState.value
        val currencyCode = currentState.selectedCurrency
        return PaymentInput(
            recipientEmail = currentState.recipientEmail,
            amount = currentState.amount,
            currencyCode = currencyCode.code
        )
    }

    private fun updateRecipientEmail(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                recipientEmail = value,
                validationErrors = emptyList(),
                paymentToReview = null,
                submittedTransaction = null,
                submissionError = null
            )
        }
    }

    private fun updateAmount(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                amount = value,
                validationErrors = emptyList(),
                paymentToReview = null,
                submittedTransaction = null,
                submissionError = null
            )
        }
    }

    private fun updateCurrency(currency: PaymentCurrency) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCurrency = currency,
                validationErrors = emptyList(),
                paymentToReview = null,
                submittedTransaction = null,
                submissionError = null
            )
        }
    }

    private fun handlePaymentResult(result: SendPaymentResult) {
        when (result) {
            is SendPaymentResult.Success -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        recipientEmail = "",
                        amount = "",
                        paymentToReview = null,
                        isSubmitting = false,
                        submittedTransaction = result.transaction
                    )
                }
            }

            is SendPaymentResult.ValidationError -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        paymentToReview = null,
                        isSubmitting = false,
                        validationErrors = result.errors
                    )
                }
            }

            is SendPaymentResult.Failure -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        isSubmitting = false,
                        submissionError = result.message
                    )
                }
            }
        }
    }

    private fun dismissFeedback() {
        _uiState.update { currentState ->
            currentState.copy(
                submittedTransaction = null,
                submissionError = null
            )
        }
    }
}
