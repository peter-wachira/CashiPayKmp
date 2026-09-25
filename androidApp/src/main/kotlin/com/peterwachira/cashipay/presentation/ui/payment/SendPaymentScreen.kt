package com.peterwachira.cashipay.presentation.ui.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peterwachira.cashipay.R
import com.peterwachira.cashipay.presentation.payment.PaymentUiAction
import com.peterwachira.cashipay.presentation.payment.PaymentUiState
import com.peterwachira.cashipay.presentation.theme.CashiPayTheme
import com.peterwachira.cashipay.presentation.ui.component.CashiPrimaryButton
import com.peterwachira.cashipay.presentation.ui.component.CashiHeroDecoration
import com.peterwachira.cashipay.presentation.ui.component.CurrencySelector
import com.peterwachira.cashipay.presentation.ui.component.PaymentTextField
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError

/** Displays the stateless payment form. */
@Composable
internal fun SendPaymentScreen(
    state: PaymentUiState,
    onAction: (PaymentUiAction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val recipientError = state.validationErrors.recipientEmailError()
    val amountError = state.validationErrors.amountError()

    val recipientErrorMessage = if (recipientError != null) {
        stringResource(recipientError.messageResourceId())
    } else {
        null
    }

    val amountErrorMessage = if (amountError != null) {
        stringResource(amountError.messageResourceId())
    } else {
        null
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SendPaymentTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            SendPaymentBottomBar(
                enabled = !state.isSubmitting,
                onReviewClick = {
                    onAction(PaymentUiAction.ReviewPayment)
                }
            )
        }
    ) { contentPadding ->
        SendPaymentForm(
            recipientEmail = state.recipientEmail,
            amount = state.amount,
            selectedCurrency = state.selectedCurrency,
            recipientErrorMessage = recipientErrorMessage,
            amountErrorMessage = amountErrorMessage,
            onRecipientEmailChanged = { value ->
                onAction(PaymentUiAction.RecipientEmailChanged(value))
            },
            onAmountChanged = { value ->
                onAction(PaymentUiAction.AmountChanged(value))
            },
            onCurrencySelected = { currency ->
                onAction(PaymentUiAction.CurrencySelected(currency))
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
    }
}

@Composable
private fun SendPaymentTopBar(
    onBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.payment_send_title),
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
private fun SendPaymentBottomBar(
    enabled: Boolean,
    onReviewClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            CashiPrimaryButton(
                text = stringResource(R.string.payment_review_action),
                onClick = onReviewClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled
            )
        }
    }
}

@Composable
private fun SendPaymentForm(
    recipientEmail: String,
    amount: String,
    selectedCurrency: PaymentCurrency,
    recipientErrorMessage: String?,
    amountErrorMessage: String?,
    onRecipientEmailChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onCurrencySelected: (PaymentCurrency) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        PaymentFormIntro()

        PaymentTextField(
            value = recipientEmail,
            onValueChange = onRecipientEmailChanged,
            label = stringResource(R.string.payment_recipient_email_label),
            supportingText = recipientErrorMessage,
            isError = recipientErrorMessage != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        PaymentAmountSection(
            amount = amount,
            selectedCurrency = selectedCurrency,
            amountErrorMessage = amountErrorMessage,
            onAmountChanged = onAmountChanged,
            onCurrencySelected = onCurrencySelected
        )

        PaymentReviewReminder()
    }
}

@Composable
private fun PaymentFormIntro(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(modifier = Modifier.heightIn(min = 144.dp)) {
            CashiHeroDecoration(modifier = Modifier.fillMaxSize())
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.payment_form_intro_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.payment_form_intro_message),
                    modifier = Modifier.fillMaxWidth(0.78f),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun PaymentAmountSection(
    amount: String,
    selectedCurrency: PaymentCurrency,
    amountErrorMessage: String?,
    onAmountChanged: (String) -> Unit,
    onCurrencySelected: (PaymentCurrency) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        PaymentTextField(
            value = amount,
            onValueChange = onAmountChanged,
            label = stringResource(R.string.payment_amount_label),
            modifier = Modifier.weight(1f),
            supportingText = amountErrorMessage,
            isError = amountErrorMessage != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
        )

        CurrencySelector(
            selectedCurrency = selectedCurrency,
            label = stringResource(R.string.payment_currency_label),
            onCurrencySelected = onCurrencySelected,
            modifier = Modifier.width(112.dp)
        )
    }
}

@Composable
private fun PaymentReviewReminder(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clearAndSetSemantics {},
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = stringResource(R.string.payment_review_reminder),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SendPaymentScreenPreview() {
    CashiPayTheme {
        SendPaymentScreen(
            state = PaymentUiState(
                recipientEmail = "amina@studio.co",
                amount = "250.00"
            ),
            onAction = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SendPaymentScreenErrorPreview() {
    CashiPayTheme {
        SendPaymentScreen(
            state = PaymentUiState(
                recipientEmail = "invalid-email",
                amount = "0",
                validationErrors = listOf(
                    PaymentValidationError.InvalidRecipientEmail,
                    PaymentValidationError.AmountMustBeGreaterThanZero
                )
            ),
            onAction = {},
            onBackClick = {}
        )
    }
}
