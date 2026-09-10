package com.peterwachira.cashipay.data.repository

import com.peterwachira.cashipay.data.firestore.FirestorePaymentDataSource
import com.peterwachira.cashipay.data.remote.RemotePaymentDataSource
import com.peterwachira.cashipay.sharedLogic.core.result.AppResult
import com.peterwachira.cashipay.sharedLogic.domain.repository.PaymentRepository
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

/**
 * Processes payments remotely and stores successful transactions in Firestore.
 */
internal class AndroidPaymentRepository(
    private val remotePaymentDataSource: RemotePaymentDataSource,
    private val firestorePaymentDataSource: FirestorePaymentDataSource
) : PaymentRepository {

    override suspend fun sendPayment(
        request: PaymentRequest
    ): AppResult<PaymentTransaction> {
        return when (
            val remoteResult = remotePaymentDataSource.sendPayment(request)
        ) {
            is AppResult.Error -> remoteResult

            is AppResult.Success -> {
                saveProcessedPayment(remoteResult.data)
            }
        }
    }

    override fun observeTransactions(): Flow<List<PaymentTransaction>> {
        return firestorePaymentDataSource.observeTransactions()
    }

    private suspend fun saveProcessedPayment(
        transaction: PaymentTransaction
    ): AppResult<PaymentTransaction> {
        return try {
            firestorePaymentDataSource.savePayment(transaction)
            AppResult.Success(transaction)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            AppResult.Error(
                message = FIRESTORE_SAVE_ERROR,
                cause = exception
            )
        }
    }

    private companion object {
        const val FIRESTORE_SAVE_ERROR =
            "Payment was processed, but transaction history could not be saved"
    }
}
