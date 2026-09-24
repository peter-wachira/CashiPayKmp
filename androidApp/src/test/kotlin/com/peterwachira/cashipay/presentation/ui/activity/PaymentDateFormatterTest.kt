package com.peterwachira.cashipay.presentation.ui.activity

import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

/** Verifies deterministic transaction timestamp presentation. */
internal class PaymentDateFormatterTest {

    @Test
    fun `when timestamp is formatted then requested locale and zone are used`() {
        // Given
        val timestampMillis = 1_745_578_240_000L

        // When
        val result = PaymentDateFormatter.format(
            timestampMillis = timestampMillis,
            locale = Locale.US,
            timeZone = TimeZone.getTimeZone("UTC")
        )

        // Then
        assertEquals("Apr 25, 2025 • 10:50 AM", result)
    }
}
