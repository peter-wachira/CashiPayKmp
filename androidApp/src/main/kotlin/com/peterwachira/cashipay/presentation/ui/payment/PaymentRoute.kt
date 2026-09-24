package com.peterwachira.cashipay.presentation.ui.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peterwachira.cashipay.presentation.payment.PaymentUiAction
import com.peterwachira.cashipay.presentation.payment.PaymentViewModel
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import org.koin.compose.viewmodel.koinViewModel

/** Connects the payment ViewModel to the stateless payment screens. */
@Composable
internal fun PaymentRoute(
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
    onViewDetails: (PaymentTransaction) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val submittedTransaction = state.submittedTransaction
    val paymentToReview = state.paymentToReview

    when {
        submittedTransaction != null -> {
            PaymentSuccessScreen(
                transaction = submittedTransaction,
                onViewDetails = {
                    onViewDetails(submittedTransaction)
                },
                onDoneClick = {
                    viewModel.onAction(PaymentUiAction.DismissFeedback)
                    onDoneClick()
                },
                modifier = modifier
            )
        }

        paymentToReview != null -> {
            ReviewPaymentScreen(
                paymentRequest = paymentToReview,
                isSubmitting = state.isSubmitting,
                submissionError = state.submissionError,
                onConfirmClick = {
                    viewModel.onAction(PaymentUiAction.ConfirmPayment)
                },
                onEditClick = {
                    viewModel.onAction(PaymentUiAction.EditPayment)
                },
                modifier = modifier
            )
        }

        else -> {
            SendPaymentScreen(
                state = state,
                onAction = viewModel::onAction,
                onBackClick = onBackClick,
                modifier = modifier
            )
        }
    }
}
