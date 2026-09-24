package com.peterwachira.cashipay.presentation.activity

import com.peterwachira.cashipay.presentation.MainDispatcherRule
import com.peterwachira.cashipay.sharedLogic.domain.usecase.ObserveTransactionsUseCase
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/** Verifies ID-based resolution of transaction details from the domain stream. */
@OptIn(ExperimentalCoroutinesApi::class)
internal class TransactionDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when matching transaction is emitted then its details are exposed`() = runTest {
        // Given
        val transaction = sampleTransaction()
        val observeTransactionsUseCase = mockk<ObserveTransactionsUseCase>()
        every {
            observeTransactionsUseCase()
        } returns flowOf(listOf(transaction))

        val viewModel = TransactionDetailsViewModel(
            transactionId = transaction.id.value,
            observeTransactionsUseCase = observeTransactionsUseCase
        )

        // When
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(transaction, state.transaction)
        assertNull(state.errorMessage)
    }

    @Test
    fun `when transaction id is absent then not found state is exposed`() = runTest {
        // Given
        val observeTransactionsUseCase = mockk<ObserveTransactionsUseCase>()
        every {
            observeTransactionsUseCase()
        } returns flowOf(listOf(sampleTransaction()))

        val viewModel = TransactionDetailsViewModel(
            transactionId = "missing-transaction",
            observeTransactionsUseCase = observeTransactionsUseCase
        )

        // When
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.transaction)
        assertEquals("This payment could not be found", state.errorMessage)
    }

    @Test
    fun `when transaction stream fails then error state is exposed`() = runTest {
        // Given
        val observeTransactionsUseCase = mockk<ObserveTransactionsUseCase>()
        every {
            observeTransactionsUseCase()
        } returns flow {
            throw IllegalStateException("Firestore unavailable")
        }

        val viewModel = TransactionDetailsViewModel(
            transactionId = "transaction-1",
            observeTransactionsUseCase = observeTransactionsUseCase
        )

        // When
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.transaction)
        assertEquals("Firestore unavailable", state.errorMessage)
    }

    private fun sampleTransaction(): PaymentTransaction {
        return PaymentTransaction(
            id = requireNotNull(TransactionId.from("transaction-1")),
            recipientEmail = requireNotNull(
                RecipientEmail.from("customer@example.com")
            ),
            amount = Money(
                amountMinor = requireNotNull(MinorUnits.fromPositive(4_550L)),
                currency = PaymentCurrency.EUR
            ),
            createdAtMillis = 1_000L
        )
    }
}
