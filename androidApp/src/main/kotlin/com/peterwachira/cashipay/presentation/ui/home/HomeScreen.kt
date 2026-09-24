package com.peterwachira.cashipay.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peterwachira.cashipay.R
import com.peterwachira.cashipay.presentation.activity.TransactionHistoryUiState
import com.peterwachira.cashipay.presentation.theme.CashiPayTheme
import com.peterwachira.cashipay.presentation.ui.activity.PaymentDateFormatter
import com.peterwachira.cashipay.presentation.ui.component.CashiHeroDecoration
import com.peterwachira.cashipay.presentation.ui.component.CashiPayWordmark
import com.peterwachira.cashipay.presentation.ui.payment.PaymentAmountFormatter
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId

/** Presents the primary payment action and a concise activity snapshot. */
@Composable
internal fun HomeScreen(
    state: TransactionHistoryUiState,
    onSendPaymentClick: () -> Unit,
    onViewAllClick: () -> Unit,
    onTransactionClick: (PaymentTransaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        CashiPayWordmark()

        Text(
            text = stringResource(R.string.home_greeting),
            modifier = Modifier.padding(top = 28.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.home_title),
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.headlineMedium
        )

        PaymentActionCard(
            onSendPaymentClick = onSendPaymentClick,
            modifier = Modifier.padding(top = 28.dp)
        )

        RecentActivityHeader(
            onViewAllClick = onViewAllClick,
            modifier = Modifier.padding(top = 28.dp)
        )

        RecentActivityContent(
            state = state,
            onTransactionClick = onTransactionClick
        )
    }
}

@Composable
private fun PaymentActionCard(
    onSendPaymentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.heightIn(min = 224.dp)) {
            CashiHeroDecoration(modifier = Modifier.fillMaxSize())

            Column(modifier = Modifier.padding(24.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.14f),
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    Text(
                        text = stringResource(R.string.home_secure_payments),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Text(
                    text = stringResource(R.string.home_send_card_title),
                    modifier = Modifier.padding(top = 20.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.home_send_card_message),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(0.78f),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.80f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = onSendPaymentClick,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = stringResource(R.string.home_send_action),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentActivityHeader(
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.home_recent_activity),
            style = MaterialTheme.typography.titleLarge
        )
        TextButton(onClick = onViewAllClick) {
            Text(text = stringResource(R.string.home_view_all))
        }
    }
}

@Composable
private fun RecentActivityContent(
    state: TransactionHistoryUiState,
    onTransactionClick: (PaymentTransaction) -> Unit,
) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
            }
        }

        state.errorMessage != null -> {
            Text(
                text = stringResource(R.string.home_activity_unavailable),
                modifier = Modifier.padding(vertical = 24.dp),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        state.transactions.isEmpty() -> {
            Text(
                text = stringResource(R.string.home_no_activity),
                modifier = Modifier.padding(vertical = 24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        else -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.transactions.take(RECENT_PAYMENT_COUNT).forEach { transaction ->
                    RecentPaymentRow(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentPaymentRow(
    transaction: PaymentTransaction,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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
                    .size(42.dp)
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
                    text = transaction.recipientEmail.value.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.recipientEmail.value,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = PaymentDateFormatter.format(transaction.createdAtMillis),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = stringResource(
                    R.string.activity_outgoing_amount,
                    PaymentAmountFormatter.format(transaction.amount)
                ),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private const val RECENT_PAYMENT_COUNT = 3

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenPreview() {
    CashiPayTheme {
        HomeScreen(
            state = TransactionHistoryUiState(
                isLoading = false,
                transactions = listOf(
                    PaymentTransaction(
                        id = requireNotNull(TransactionId.from("txn-home")),
                        recipientEmail = requireNotNull(
                            RecipientEmail.from("amina@studio.co")
                        ),
                        amount = Money(
                            amountMinor = requireNotNull(
                                MinorUnits.fromPositive(25_000L)
                            ),
                            currency = PaymentCurrency.USD
                        ),
                        createdAtMillis = 1_745_578_240_000L
                    )
                )
            ),
            onSendPaymentClick = {},
            onViewAllClick = {},
            onTransactionClick = {}
        )
    }
}
