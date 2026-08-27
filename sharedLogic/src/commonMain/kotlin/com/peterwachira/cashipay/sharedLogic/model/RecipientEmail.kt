package com.peterwachira.cashipay.sharedLogic.model

import kotlin.jvm.JvmInline

/**
 * Represents a normalized and syntactically valid payment recipient email.
 */
@JvmInline
value class RecipientEmail private constructor(
    val value: String
) {

    companion object {

        private val emailRegex = Regex(
            pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )

        fun from(value: String): RecipientEmail? {
            val normalizedValue = value.trim()

            return if (emailRegex.matches(normalizedValue)) {
                RecipientEmail(normalizedValue)
            } else {
                null
            }
        }
    }
}