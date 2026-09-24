package com.peterwachira.cashipay.presentation.ui.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peterwachira.cashipay.presentation.activity.TransactionHistoryViewModel
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import org.koin.compose.viewmodel.koinViewModel

/** Connects transaction history state to its stateless screen. */
@Composable
internal fun TransactionHistoryRoute(
    onTransactionClick: (PaymentTransaction) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionHistoryViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TransactionHistoryScreen(
        state = state,
        onAction = viewModel::onAction,
        onTransactionClick = onTransactionClick,
        modifier = modifier
    )
}
