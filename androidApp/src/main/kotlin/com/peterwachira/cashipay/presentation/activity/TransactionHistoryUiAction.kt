package com.peterwachira.cashipay.presentation.activity

/** Defines user actions accepted by transaction history. */
internal sealed interface TransactionHistoryUiAction {
    data object Retry : TransactionHistoryUiAction
}
