package com.peterwachira.cashipay.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peterwachira.cashipay.sharedLogic.domain.usecase.SendPaymentResult
import com.peterwachira.cashipay.sharedLogic.domain.usecase.SendPaymentUseCase
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates payment form actions and exposes immutable screen state.
 */
internal class PaymentViewModel(
    private val sendPaymentUseCase: SendPaymentUseCase
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

            PaymentUiAction.Submit -> {
                submitPayment()
            }

            PaymentUiAction.DismissFeedback -> {
                dismissFeedback()
            }
        }
    }

    private fun updateRecipientEmail(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                recipientEmail = value,
                validationErrors = emptyList(),
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
                submittedTransaction = null,
                submissionError = null
            )
        }
    }

    private fun submitPayment() {
        val currentState = _uiState.value

        if (currentState.isSubmitting) {
            return
        }

        val paymentInput = PaymentInput(
            recipientEmail = currentState.recipientEmail,
            amount = currentState.amount,
            currencyCode = currentState.selectedCurrency.code
        )

        _uiState.update { state ->
            state.copy(
                isSubmitting = true,
                validationErrors = emptyList(),
                submittedTransaction = null,
                submissionError = null
            )
        }

        viewModelScope.launch {
            val result = sendPaymentUseCase(paymentInput)
            handlePaymentResult(result)
        }
    }

    private fun handlePaymentResult(result: SendPaymentResult) {
        when (result) {
            is SendPaymentResult.Success -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        recipientEmail = "",
                        amount = "",
                        isSubmitting = false,
                        submittedTransaction = result.transaction
                    )
                }
            }

            is SendPaymentResult.ValidationError -> {
                _uiState.update { currentState ->
                    currentState.copy(
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
