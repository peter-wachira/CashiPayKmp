package com.peterwachira.cashipay.presentation.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peterwachira.cashipay.R
import com.peterwachira.cashipay.presentation.activity.TransactionHistoryUiAction
import com.peterwachira.cashipay.presentation.activity.TransactionHistoryUiState
import com.peterwachira.cashipay.presentation.theme.CashiPayTheme
import com.peterwachira.cashipay.presentation.ui.payment.PaymentAmountFormatter
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId

/** Displays real-time outgoing payment activity. */
@Composable
internal fun TransactionHistoryScreen(
    state: TransactionHistoryUiState,
    onAction: (TransactionHistoryUiAction) -> Unit,
    onTransactionClick: (PaymentTransaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TransactionHistoryHeader()
        }
    ) { contentPadding ->
        when {
            state.isLoading -> {
                TransactionHistoryLoading(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                )
            }

            state.errorMessage != null -> {
                TransactionHistoryError(
                    message = state.errorMessage,
                    onRetryClick = {
                        onAction(TransactionHistoryUiAction.Retry)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                )
            }

            state.transactions.isEmpty() -> {
                EmptyTransactionHistory(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                )
            }

            else -> {
                TransactionList(
                    transactions = state.transactions,
                    onTransactionClick = onTransactionClick,
                    contentPadding = contentPadding
                )
            }
        }
    }
}

@Composable
private fun TransactionHistoryHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.activity_title),
            style = MaterialTheme.typography.headlineSmall
        )

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Text(
                text = stringResource(R.string.activity_up_to_date),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TransactionList(
    transactions: List<PaymentTransaction>,
    onTransactionClick: (PaymentTransaction) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = contentPadding.calculateTopPadding(),
            end = 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = transactions,
            key = { transaction -> transaction.id.value }
        ) { transaction ->
            TransactionRow(
                transaction = transaction,
                onClick = {
                    onTransactionClick(transaction)
                }
            )
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: PaymentTransaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = recipientInitials(transaction.recipientEmail.value),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.recipientEmail.value,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = PaymentDateFormatter.format(
                        transaction.createdAtMillis
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(
                        R.string.activity_outgoing_amount,
                        PaymentAmountFormatter.format(transaction.amount)
                    ),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = transaction.amount.currency.code,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TransactionHistoryLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyTransactionHistory(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.activity_empty_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.activity_empty_message),
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TransactionHistoryError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.activity_error_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        TextButton(
            onClick = onRetryClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = stringResource(R.string.activity_retry))
        }
    }
}

private fun recipientInitials(email: String): String {
    val localPart = email.substringBefore('@')
    return localPart.take(2).uppercase()
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TransactionHistoryScreenPreview() {
    CashiPayTheme {
        TransactionHistoryScreen(
            state = TransactionHistoryUiState(
                isLoading = false,
                transactions = listOf(previewTransaction())
            ),
            onAction = {},
            onTransactionClick = {}
        )
    }
}

private fun previewTransaction(): PaymentTransaction {
    return PaymentTransaction(
        id = requireNotNull(TransactionId.from("transaction-1")),
        recipientEmail = requireNotNull(RecipientEmail.from("amina@studio.co")),
        amount = Money(
            amountMinor = requireNotNull(MinorUnits.fromPositive(25_000L)),
            currency = PaymentCurrency.USD
        ),
        createdAtMillis = 1_745_578_240_000L
    )
}
