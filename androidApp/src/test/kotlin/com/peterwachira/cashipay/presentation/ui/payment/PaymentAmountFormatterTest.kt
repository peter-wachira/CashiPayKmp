package com.peterwachira.cashipay.presentation.ui.payment

import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

/** Verifies locale-aware formatting of minor-unit payment amounts. */
internal class PaymentAmountFormatterTest {

    @Test
    fun `when USD amount is formatted then two decimal places are shown`() {
        // Given
        val money = Money(
            amountMinor = requireNotNull(MinorUnits.fromPositive(25_000L)),
            currency = PaymentCurrency.USD
        )

        // When
        val result = PaymentAmountFormatter.format(money, Locale.US)

        // Then
        assertEquals("$250.00", result)
    }

    @Test
    fun `when minor amount is formatted then decimal position is preserved`() {
        // Given
        val money = Money(
            amountMinor = requireNotNull(MinorUnits.fromPositive(455L)),
            currency = PaymentCurrency.USD
        )

        // When
        val result = PaymentAmountFormatter.format(money, Locale.US)

        // Then
        assertEquals("$4.55", result)
    }
}
