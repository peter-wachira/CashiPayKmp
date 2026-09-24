package com.peterwachira.cashipay.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peterwachira.cashipay.sharedLogic.domain.usecase.ObserveTransactionsUseCase
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Exposes the real-time outgoing transaction history as immutable UI state. */
internal class TransactionHistoryViewModel(
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState.asStateFlow()

    private var observationJob: Job? = null

    init {
        observeTransactions()
    }

    fun onAction(action: TransactionHistoryUiAction) {
        when (action) {
            TransactionHistoryUiAction.Retry -> observeTransactions()
        }
    }

    private fun observeTransactions() {
        observationJob?.cancel()

        _uiState.update { currentState ->
            currentState.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        observationJob = viewModelScope.launch {
            observeTransactionsUseCase()
                .catch { exception ->
                    if (exception is CancellationException) {
                        throw exception
                    }

                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = exception.message
                                ?: TRANSACTION_HISTORY_ERROR
                        )
                    }
                }
                .collect { transactions ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            transactions = transactions,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    private companion object {
        const val TRANSACTION_HISTORY_ERROR =
            "Unable to load payment activity"
    }
}
