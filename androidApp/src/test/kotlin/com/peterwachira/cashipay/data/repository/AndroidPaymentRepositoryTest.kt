package com.peterwachira.cashipay.data.repository

import com.peterwachira.cashipay.data.firestore.FirestorePaymentDataSource
import com.peterwachira.cashipay.data.remote.RemotePaymentDataSource
import com.peterwachira.cashipay.sharedLogic.core.result.AppResult
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Verifies payment processing and Firestore persistence orchestration.
 */
internal class AndroidPaymentRepositoryTest {

    @Test
    fun `when remote payment succeeds then transaction is saved and returned`() = runTest {
        // Given
        val request = validRequest()
        val transaction = validTransaction()
        val remoteDataSource = FakeRemotePaymentDataSource(
            result = AppResult.Success(transaction)
        )
        val firestoreDataSource = FakeFirestorePaymentDataSource()
        val repository = AndroidPaymentRepository(
            remotePaymentDataSource = remoteDataSource,
            firestorePaymentDataSource = firestoreDataSource
        )

        // When
        val result = repository.sendPayment(request)

        // Then
        assertEquals(AppResult.Success(transaction), result)
        assertEquals(request, remoteDataSource.receivedRequest)
        assertEquals(listOf(transaction), firestoreDataSource.savedTransactions)
    }

    @Test
    fun `when remote payment fails then transaction is not saved`() = runTest {
        // Given
        val remoteError = AppResult.Error(
            message = "Backend Unavailable"
        )
        val remoteDataSource = FakeRemotePaymentDataSource(
            result = remoteError
        )
        val firestoreDataSource = FakeFirestorePaymentDataSource()
        val repository = AndroidPaymentRepository(
            remotePaymentDataSource = remoteDataSource,
            firestorePaymentDataSource = firestoreDataSource
        )

        // When
        val result = repository.sendPayment(validRequest())

        // Then
        assertEquals(remoteError, result)
        assertTrue(firestoreDataSource.savedTransactions.isEmpty())
    }

    @Test
    fun `when Firestore save fails then repository returns storage error`() = runTest {
        // Given
        val storageException = IllegalStateException("Firestore unavailable")
        val remoteDataSource = FakeRemotePaymentDataSource(
            result = AppResult.Success(validTransaction())
        )
        val firestoreDataSource = FakeFirestorePaymentDataSource(
            saveException = storageException
        )
        val repository = AndroidPaymentRepository(
            remotePaymentDataSource = remoteDataSource,
            firestorePaymentDataSource = firestoreDataSource
        )

        // When
        val result = repository.sendPayment(validRequest())

        // Then
        assertTrue(result is AppResult.Error)

        val errorResult = result as AppResult.Error
        assertEquals(
            "Payment was processed, but transaction history could not be saved",
            errorResult.message
        )
        assertEquals(storageException, errorResult.cause)
    }

    @Test
    fun `when transactions are observed then Firestore history is returned`() = runTest {
        // Given
        val transaction = validTransaction()
        val remoteDataSource = FakeRemotePaymentDataSource(
            result = AppResult.Success(transaction)
        )
        val firestoreDataSource = FakeFirestorePaymentDataSource(
            observedTransactions = listOf(transaction)
        )
        val repository = AndroidPaymentRepository(
            remotePaymentDataSource = remoteDataSource,
            firestorePaymentDataSource = firestoreDataSource
        )

        // When
        val result = repository.observeTransactions().first()

        // Then
        assertEquals(listOf(transaction), result)
    }

    @Test
    fun `when Firestore save is cancelled then cancellation is rethrown`() = runTest {
        // Given
        val cancellationException = CancellationException("Test cancellation")
        val remoteDataSource = FakeRemotePaymentDataSource(
            result = AppResult.Success(validTransaction())
        )
        val firestoreDataSource = FakeFirestorePaymentDataSource(
            saveException = cancellationException
        )
        val repository = AndroidPaymentRepository(
            remotePaymentDataSource = remoteDataSource,
            firestorePaymentDataSource = firestoreDataSource
        )

        // When
        val thrownException = try {
            repository.sendPayment(validRequest())
            fail("Expected payment saving to be cancelled")
        } catch (exception: CancellationException) {
            exception
        }

        // Then
        assertSame(cancellationException, thrownException)
    }

    private fun validRequest(): PaymentRequest {
        return PaymentRequest(
            recipientEmail = requireNotNull(
                RecipientEmail.from("peterwachira@email.com")
            ),
            amount = validMoney()
        )
    }

    private fun validTransaction(): PaymentTransaction {
        return PaymentTransaction(
            id = requireNotNull(
                TransactionId.from("transaction-1")
            ),
            recipientEmail = requireNotNull(
                RecipientEmail.from("peterwachira@email.com")
            ),
            amount = validMoney(),
            createdAtMillis = 1_000L
        )
    }

    private fun validMoney(): Money {
        return Money(
            amountMinor = requireNotNull(
                MinorUnits.fromPositive(10_050L)
            ),
            currency = PaymentCurrency.USD
        )
    }

    private class FakeRemotePaymentDataSource(
        private val result: AppResult<PaymentTransaction>
    ) : RemotePaymentDataSource {

        var receivedRequest: PaymentRequest? = null
            private set

        override suspend fun sendPayment(
            request: PaymentRequest
        ): AppResult<PaymentTransaction> {
            receivedRequest = request
            return result
        }
    }

    private class FakeFirestorePaymentDataSource(
        private val observedTransactions: List<PaymentTransaction> = emptyList(),
        private val saveException: Throwable? = null
    ) : FirestorePaymentDataSource {

        val savedTransactions = mutableListOf<PaymentTransaction>()

        override suspend fun savePayment(transaction: PaymentTransaction) {
            val exception = saveException

            if (exception != null) {
                throw exception
            }

            savedTransactions.add(transaction)
        }

        override fun observeTransactions(): Flow<List<PaymentTransaction>> {
            return flowOf(observedTransactions)
        }
    }
}
