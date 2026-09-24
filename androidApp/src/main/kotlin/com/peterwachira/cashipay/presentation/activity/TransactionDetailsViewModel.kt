package com.peterwachira.cashipay.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peterwachira.cashipay.sharedLogic.domain.usecase.ObserveTransactionsUseCase
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/** Resolves one transaction by ID from the authenticated user's activity. */
internal class TransactionDetailsViewModel(
    private val transactionId: String,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionDetailsUiState())
    val uiState: StateFlow<TransactionDetailsUiState> = _uiState.asStateFlow()

    init {
        observeTransaction()
    }

    private fun observeTransaction() {
        viewModelScope.launch {
            observeTransactionsUseCase()
                .catch { exception ->
                    if (exception is CancellationException) {
                        throw exception
                    }

                    _uiState.value = TransactionDetailsUiState(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: TRANSACTION_DETAILS_ERROR
                    )
                }
                .collect { transactions ->
                    val transaction = transactions.firstOrNull { candidate ->
                        candidate.id.value == transactionId
                    }

                    _uiState.value = TransactionDetailsUiState(
                        isLoading = false,
                        transaction = transaction,
                        errorMessage = if (transaction == null) {
                            TRANSACTION_NOT_FOUND_ERROR
                        } else {
                            null
                        }
                    )
                }
        }
    }

    private companion object {
        const val TRANSACTION_DETAILS_ERROR =
            "Unable to load payment details"
        const val TRANSACTION_NOT_FOUND_ERROR =
            "This payment could not be found"
    }
}
