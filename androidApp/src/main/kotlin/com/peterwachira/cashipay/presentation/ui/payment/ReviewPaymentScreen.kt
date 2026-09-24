package com.peterwachira.cashipay.presentation.ui.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.peterwachira.cashipay.presentation.theme.CashiPayTheme
import com.peterwachira.cashipay.presentation.ui.component.CashiPrimaryButton
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail

/** Displays a validated payment before final confirmation. */
@Composable
internal fun ReviewPaymentScreen(
    paymentRequest: PaymentRequest,
    isSubmitting: Boolean,
    submissionError: String?,
    onConfirmClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ReviewPaymentTopBar(onBackClick = onEditClick)
        },
        bottomBar = {
            ReviewPaymentActions(
                isSubmitting = isSubmitting,
                onConfirmClick = onConfirmClick,
                onEditClick = onEditClick
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PaymentSummaryCard(paymentRequest = paymentRequest)
            PaymentProcessingNotice()

            if (submissionError != null) {
                PaymentSubmissionError(message = submissionError)
            }
        }
    }
}

@Composable
private fun ReviewPaymentTopBar(
    onBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.payment_review_title),
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(
                        R.string.payment_edit_details
                    )
                )
            }
        }
    )
}

@Composable
private fun PaymentSummaryCard(
    paymentRequest: PaymentRequest,
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = recipientInitials(
                            paymentRequest.recipientEmail.value
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = paymentRequest.recipientEmail.value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Text(
                text = stringResource(R.string.payment_amount_label),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = PaymentAmountFormatter.format(paymentRequest.amount),
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = paymentRequest.amount.currency.code,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun PaymentProcessingNotice(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = stringResource(R.string.payment_processing_notice),
            modifier = Modifier.padding(20.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun PaymentSubmissionError(
    message: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.payment_submission_failed),
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ReviewPaymentActions(
    isSubmitting: Boolean,
    onConfirmClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CashiPrimaryButton(
                text = stringResource(R.string.payment_send_action),
                onClick = onConfirmClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
                isLoading = isSubmitting
            )

            OutlinedButton(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(R.string.payment_edit_details))
            }
        }
    }
}

private fun recipientInitials(email: String): String {
    val localPart = email.substringBefore('@')
    val words = localPart.split('.', '-', '_').filter { it.isNotBlank() }

    return when {
        words.size >= 2 -> {
            "${words[0].first()}${words[1].first()}".uppercase()
        }

        localPart.length >= 2 -> localPart.take(2).uppercase()
        else -> localPart.uppercase()
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ReviewPaymentScreenPreview() {
    CashiPayTheme {
        ReviewPaymentScreen(
            paymentRequest = previewPaymentRequest(),
            isSubmitting = false,
            submissionError = null,
            onConfirmClick = {},
            onEditClick = {}
        )
    }
}

private fun previewPaymentRequest(): PaymentRequest {
    return PaymentRequest(
        recipientEmail = requireNotNull(RecipientEmail.from("amina@studio.co")),
        amount = Money(
            amountMinor = requireNotNull(MinorUnits.fromPositive(25_000L)),
            currency = PaymentCurrency.USD
        )
    )
}
