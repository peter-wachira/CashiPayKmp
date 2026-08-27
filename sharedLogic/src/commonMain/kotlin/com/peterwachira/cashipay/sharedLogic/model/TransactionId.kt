package com.peterwachira.cashipay.sharedLogic.model

import kotlin.jvm.JvmInline

/**
 * Represents a non-empty identifier assigned to a completed transaction.
 */
@JvmInline
value class TransactionId private constructor(
    val value: String
) {

    companion object {

        fun from(value: String): TransactionId? {
            val trimmedValue = value.trim()

            return if (trimmedValue.isNotEmpty()) {
                TransactionId(trimmedValue)
            } else {
                null
            }
        }
    }
}
