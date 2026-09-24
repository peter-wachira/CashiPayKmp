package com.peterwachira.cashipay.presentation.activity

import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction

/** Represents everything the transaction history screen can render. */
internal data class TransactionHistoryUiState(
    val isLoading: Boolean = true,
    val transactions: List<PaymentTransaction> = emptyList(),
    val errorMessage: String? = null,
)
