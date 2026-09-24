package com.peterwachira.cashipay.presentation.ui.payment

import androidx.annotation.StringRes
import com.peterwachira.cashipay.R
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError

/** Maps domain validation errors to Android UI resources. */
@StringRes
internal fun PaymentValidationError.messageResourceId(): Int {
    return when (this) {
        PaymentValidationError.RecipientEmailRequired -> {
            R.string.payment_error_recipient_required
        }

        PaymentValidationError.InvalidRecipientEmail -> {
            R.string.payment_error_recipient_invalid
        }

        PaymentValidationError.AmountRequired -> {
            R.string.payment_error_amount_required
        }

        PaymentValidationError.InvalidAmount -> {
            R.string.payment_error_amount_invalid
        }

        PaymentValidationError.AmountMustBeGreaterThanZero -> {
            R.string.payment_error_amount_positive
        }

        PaymentValidationError.UnsupportedCurrency -> {
            R.string.payment_error_currency_unsupported
        }
    }
}

internal fun List<PaymentValidationError>.recipientEmailError():
    PaymentValidationError? {
    return firstOrNull { error ->
        error == PaymentValidationError.RecipientEmailRequired ||
            error == PaymentValidationError.InvalidRecipientEmail
    }
}

internal fun List<PaymentValidationError>.amountError():
    PaymentValidationError? {
    return firstOrNull { error ->
        error == PaymentValidationError.AmountRequired ||
            error == PaymentValidationError.InvalidAmount ||
            error == PaymentValidationError.AmountMustBeGreaterThanZero
    }
}
