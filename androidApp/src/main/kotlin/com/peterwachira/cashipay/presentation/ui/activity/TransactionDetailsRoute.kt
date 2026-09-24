package com.peterwachira.cashipay.presentation.ui.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peterwachira.cashipay.presentation.activity.TransactionDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Connects an ID-based details destination to its lifecycle-aware UI state. */
@Composable
internal fun TransactionDetailsRoute(
    transactionId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionDetailsViewModel = koinViewModel(
        key = transactionId,
        parameters = { parametersOf(transactionId) }
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TransactionDetailsScreen(
        state = state,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
