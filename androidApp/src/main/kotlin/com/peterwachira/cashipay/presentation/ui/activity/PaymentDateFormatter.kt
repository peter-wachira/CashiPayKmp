package com.peterwachira.cashipay.presentation.ui.activity

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Formats transaction timestamps for Android presentation. */
internal object PaymentDateFormatter {

    fun format(
        timestampMillis: Long,
        locale: Locale = Locale.getDefault(),
        timeZone: TimeZone = TimeZone.getDefault(),
    ): String {
        val formatter = SimpleDateFormat(
            "MMM d, yyyy • h:mm a",
            locale
        )
        formatter.timeZone = timeZone

        return formatter.format(Date(timestampMillis))
    }
}
