package com.peterwachira.cashipay.data.auth

/**
 * Provides the Firebase user ID needed to access user-owned data.
 */
internal interface AuthSessionProvider {
    suspend fun getOrCreateUserId(): String
}