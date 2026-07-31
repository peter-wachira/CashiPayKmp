package com.peterwachira.cashipay.sharedLogic.validation

sealed interface PaymentValidationError {
    data object RecipientEmailRequired: PaymentValidationError
    data object InvalidRecipientEmail: PaymentValidationError
    data object AmountRequired: PaymentValidationError
    data object InvalidAmount: PaymentValidationError
    data object AmountMustBeGreaterThanZero: PaymentValidationError
    data object UnsupportedCurrency: PaymentValidationError
}