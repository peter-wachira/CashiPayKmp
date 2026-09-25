package com.peterwachira.cashipay.presentation.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peterwachira.cashipay.R
import com.peterwachira.cashipay.presentation.activity.TransactionDetailsUiState
import com.peterwachira.cashipay.presentation.theme.CashiPayTheme
import com.peterwachira.cashipay.presentation.ui.payment.PaymentAmountFormatter
import com.peterwachira.cashipay.presentation.ui.component.PaymentSuccessGraphic
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId

/** Displays the persisted details of one outgoing payment. */
@Composable
internal fun TransactionDetailsScreen(
    state: TransactionDetailsUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TransactionDetailsTopBar(onBackClick = onBackClick)
        }
    ) { contentPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.transaction != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PaymentSuccessGraphic(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(112.dp)
                    )
                    Text(
                        text = stringResource(R.string.transaction_completed),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    TransactionDetailsCard(transaction = state.transaction)
                    SavedToActivityNotice()
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.errorMessage
                            ?: stringResource(R.string.transaction_not_found),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailsTopBar(
    onBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.transaction_details_title),
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(
                        R.string.payment_navigate_back
                    )
                )
            }
        }
    )
}

@Composable
private fun TransactionDetailsCard(
    transaction: PaymentTransaction,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transaction.recipientEmail.value
                            .substringBefore('@')
                            .take(2)
                            .uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = transaction.recipientEmail.value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Text(
                text = stringResource(
                    R.string.transaction_amount_value,
                    PaymentAmountFormatter.format(transaction.amount),
                    transaction.amount.currency.code
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            DetailRow(
                label = stringResource(R.string.transaction_status),
                value = stringResource(R.string.transaction_completed),
                valueColor = MaterialTheme.colorScheme.primary
            )
            DetailRow(
                label = stringResource(R.string.transaction_id),
                value = transaction.id.value
            )
            DetailRow(
                label = stringResource(R.string.transaction_date),
                value = PaymentDateFormatter.format(transaction.createdAtMillis)
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color =
        MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            color = valueColor,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SavedToActivityNotice(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = stringResource(R.string.transaction_saved_to_activity),
            modifier = Modifier.padding(20.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TransactionDetailsScreenPreview() {
    CashiPayTheme {
        TransactionDetailsScreen(
            state = TransactionDetailsUiState(
                isLoading = false,
                transaction = PaymentTransaction(
                    id = requireNotNull(TransactionId.from("txn_7f9a3c2e")),
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
                ),
            ),
            onBackClick = {}
        )
    }
}
