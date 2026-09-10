package com.peterwachira.cashipay.data.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Reuses the current Firebase user or creates an anonymous session.
 */
internal class FirebaseAnonymousAuthSessionProvider(
    private val firebaseAuth: FirebaseAuth
) : AuthSessionProvider {
    override suspend fun getOrCreateUserId(): String {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            return currentUser.uid
        }

        val authResult = firebaseAuth
            .signInAnonymously()
            .await()

        val signedInUser = authResult.user ?: throw IllegalStateException(
            "Firebase anonymous authentication returned no user"
        )

        return signedInUser.uid
    }
}