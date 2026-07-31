package com.peterwachira.cashipay.sharedLogic.core.result

/**
 * Represents either successful data or a recoverable application error.
 */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>

    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : AppResult<Nothing>
}
