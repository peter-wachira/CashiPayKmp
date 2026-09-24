package com.peterwachira.cashipay.data.firestore

import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import kotlinx.coroutines.flow.Flow

/**
 * Defines the Firestore operations needed to store and observe payment history.
 */
internal interface FirestorePaymentDataSource {
    suspend fun savePayment(transaction: PaymentTransaction)

    fun observeTransactions(): Flow<List<PaymentTransaction>>
}
