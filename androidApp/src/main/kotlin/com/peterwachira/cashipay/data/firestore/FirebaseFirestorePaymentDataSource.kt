package com.peterwachira.cashipay.data.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.peterwachira.cashipay.data.auth.AuthSessionProvider
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Stores payments in Firestore and exposes real-time transaction updates.
 */
internal class FirebaseFirestorePaymentDataSource(
    private val firestore: FirebaseFirestore,
    private val authSessionProvider: AuthSessionProvider
) : FirestorePaymentDataSource {

    override suspend fun savePayment(transaction: PaymentTransaction) {
        val userId = authSessionProvider.getOrCreateUserId()
        val document = transaction.toFirestoreDocument()

        val fields = mapOf(
            RECIPIENT_EMAIL_FIELD to document.recipientEmail,
            AMOUNT_MINOR_FIELD to document.amountMinor,
            CURRENCY_CODE_FIELD to document.currencyCode,
            CREATED_AT_MILLIS_FIELD to document.createdAtMillis
        )

        paymentsCollection(userId)
            .document(transaction.id.value)
            .set(fields)
            .await()
    }

    override fun observeTransactions(): Flow<List<PaymentTransaction>> {
        return flow {
            val userId = authSessionProvider.getOrCreateUserId()
            val transactionFlow = observeTransactionsForUser(userId)

            transactionFlow.collect { transactions ->
                emit(transactions)
            }
        }
    }

    private fun observeTransactionsForUser(
        userId: String
    ): Flow<List<PaymentTransaction>> {
        return callbackFlow {
            val query = paymentsCollection(userId).orderBy(
                CREATED_AT_MILLIS_FIELD,
                Query.Direction.DESCENDING
            )

            val listenerRegistration = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    return@addSnapshotListener
                }

                val transactions = snapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toPaymentTransaction()
                }

                trySend(transactions)
            }

            awaitClose {
                listenerRegistration.remove()
            }
        }
    }

    private fun paymentsCollection(
        userId: String
    ): CollectionReference {
        return firestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(PAYMENTS_COLLECTION)
    }

    private fun DocumentSnapshot.toPaymentTransaction(): PaymentTransaction? {
        val recipientEmail = getString(RECIPIENT_EMAIL_FIELD) ?: return null
        val amountMinor = getLong(AMOUNT_MINOR_FIELD) ?: return null
        val currencyCode = getString(CURRENCY_CODE_FIELD) ?: return null
        val createdAtMillis = getLong(CREATED_AT_MILLIS_FIELD) ?: return null

        val document = FirestorePaymentDocument(
            recipientEmail = recipientEmail,
            amountMinor = amountMinor,
            currencyCode = currencyCode,
            createdAtMillis = createdAtMillis
        )

        return document.toDomain(documentId = id)
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val PAYMENTS_COLLECTION = "payments"
        const val RECIPIENT_EMAIL_FIELD = "recipientEmail"
        const val AMOUNT_MINOR_FIELD = "amountMinor"
        const val CURRENCY_CODE_FIELD = "currencyCode"
        const val CREATED_AT_MILLIS_FIELD = "createdAtMillis"
    }
}