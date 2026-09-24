package com.peterwachira.cashipay.presentation.ui.payment

import com.peterwachira.cashipay.sharedLogic.model.Money
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/** Formats domain money values for Android presentation. */
internal object PaymentAmountFormatter {

    fun format(
        money: Money,
        locale: Locale = Locale.getDefault(),
    ): String {
        val formatter = NumberFormat.getCurrencyInstance(locale)
        formatter.currency = Currency.getInstance(money.currency.code)
        formatter.minimumFractionDigits = money.currency.fractionDigits
        formatter.maximumFractionDigits = money.currency.fractionDigits

        val majorAmount = BigDecimal.valueOf(money.amountMinor.value)
            .movePointLeft(money.currency.fractionDigits)
        return formatter.format(majorAmount)
    }
}
