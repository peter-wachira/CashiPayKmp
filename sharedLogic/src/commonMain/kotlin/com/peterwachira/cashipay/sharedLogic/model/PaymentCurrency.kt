package com.peterwachira.cashipay.sharedLogic.model

enum class PaymentCurrency(val code: String){
    USD("USD"),
    EUR("EUR");

    companion object {
        fun fromCode(code: String): PaymentCurrency?{
            return entries.firstOrNull(){it.code.equals(code.trim(), ignoreCase = true)}
        }

        fun fromSupportedCodes(): List<String>{
            return entries.map{it.code}
        }
    }
}
