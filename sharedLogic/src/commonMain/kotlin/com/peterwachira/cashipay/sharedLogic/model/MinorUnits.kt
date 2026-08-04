package com.peterwachira.cashipay.sharedLogic.model

import kotlin.jvm.JvmInline

/**
 * Represents a positive payment amount in the currency's smallest unit.
 */
@JvmInline
value class MinorUnits private constructor(
    val value: Long
) {
    companion object {
        fun fromPositive(value: Long): MinorUnits? {
            return if (value > 0L) {
                MinorUnits(value)
            } else {
                null
            }
        }
    }
}
