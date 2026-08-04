package com.peterwachira.cashipay.sharedLogic.model

/**
 * Defines the currencies supported for payment amounts.
 */
enum class PaymentCurrency(
    val code: String,
    val fractionDigits: Int
) {
    USD(code = "USD", fractionDigits = 2),
    EUR(code = "EUR", fractionDigits = 2);

    companion object {
        fun fromCode(code: String): PaymentCurrency? {
            return entries.firstOrNull {
                it.code.equals(code.trim(), ignoreCase = true)
            }
        }

        fun fromSupportedCodes(): List<String> {
            return entries.map { it.code }
        }
    }
}
