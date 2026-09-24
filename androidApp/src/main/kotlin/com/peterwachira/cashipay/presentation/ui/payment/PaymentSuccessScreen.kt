package com.peterwachira.cashipay.presentation.ui.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peterwachira.cashipay.R
import com.peterwachira.cashipay.presentation.theme.CashiPayTheme
import com.peterwachira.cashipay.presentation.ui.component.CashiPrimaryButton
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId

/** Displays confirmation after a payment is processed and saved. */
@Composable
internal fun PaymentSuccessScreen(
    transaction: PaymentTransaction,
    onViewDetails: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clearAndSetSemantics {},
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.displayLarge
            )
        }

        Text(
            text = stringResource(R.string.payment_sent_title),
            modifier = Modifier.padding(top = 32.dp),
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = stringResource(
                R.string.payment_sent_amount,
                PaymentAmountFormatter.format(transaction.amount),
                transaction.amount.currency.code
            ),
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(
                R.string.payment_sent_recipient,
                transaction.recipientEmail.value
            ),
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CashiPrimaryButton(
                text = stringResource(R.string.payment_view_details),
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = onDoneClick,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(R.string.payment_done))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PaymentSuccessScreenPreview() {
    CashiPayTheme {
        PaymentSuccessScreen(
            transaction = PaymentTransaction(
                id = requireNotNull(TransactionId.from("transaction-1")),
                recipientEmail = requireNotNull(
                    RecipientEmail.from("amina@studio.co")
                ),
                amount = Money(
                    amountMinor = requireNotNull(
                        MinorUnits.fromPositive(25_000L)
                    ),
                    currency = PaymentCurrency.USD
                ),
                createdAtMillis = 1_000L
            ),
            onViewDetails = {},
            onDoneClick = {}
        )
    }
}
