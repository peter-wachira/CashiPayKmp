package com.peterwachira.cashipay.presentation.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peterwachira.cashipay.presentation.activity.TransactionHistoryViewModel
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import org.koin.compose.viewmodel.koinViewModel

/** Connects the home destination to recent payment activity. */
@Composable
internal fun HomeRoute(
    onSendPaymentClick: () -> Unit,
    onViewAllClick: () -> Unit,
    onTransactionClick: (PaymentTransaction) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionHistoryViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onSendPaymentClick = onSendPaymentClick,
        onViewAllClick = onViewAllClick,
        onTransactionClick = onTransactionClick,
        modifier = modifier
    )
}
