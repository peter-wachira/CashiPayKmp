package com.peterwachira.cashipay.presentation.activity

import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction

/** Represents the content displayed while one persisted payment is resolved. */
internal data class TransactionDetailsUiState(
    val isLoading: Boolean = true,
    val transaction: PaymentTransaction? = null,
    val errorMessage: String? = null,
)
