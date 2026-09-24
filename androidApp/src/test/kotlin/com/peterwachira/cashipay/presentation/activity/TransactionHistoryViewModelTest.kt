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
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Verifies transaction history stream states and retry behavior. */
@OptIn(ExperimentalCoroutinesApi::class)
internal class TransactionHistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when view model starts then loading state is exposed`() {
        // Given
        val observeTransactionsUseCase = mockk<ObserveTransactionsUseCase>()
        every {
            observeTransactionsUseCase()
        } returns flow { }

        // When
        val viewModel = TransactionHistoryViewModel(
            observeTransactionsUseCase = observeTransactionsUseCase
        )

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.transactions.isEmpty())
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `when transactions are emitted then populated state is exposed`() = runTest {
        // Given
        val transactions = listOf(sampleTransaction())
        val observeTransactionsUseCase = mockk<ObserveTransactionsUseCase>()
        every {
            observeTransactionsUseCase()
        } returns flowOf(transactions)

        val viewModel = TransactionHistoryViewModel(
            observeTransactionsUseCase = observeTransactionsUseCase
        )

        // When
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(transactions, state.transactions)
        assertNull(state.errorMessage)
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

        val viewModel = TransactionHistoryViewModel(
            observeTransactionsUseCase = observeTransactionsUseCase
        )

        // When
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Firestore unavailable", state.errorMessage)
    }

    @Test
    fun `when retry is selected then transaction stream is observed again`() = runTest {
        // Given
        val transactions = listOf(sampleTransaction())
        val observeTransactionsUseCase = mockk<ObserveTransactionsUseCase>()
        every {
            observeTransactionsUseCase()
        } returnsMany listOf(
            flow {
                throw IllegalStateException("Firestore unavailable")
            },
            flowOf(transactions)
        )

        val viewModel = TransactionHistoryViewModel(
            observeTransactionsUseCase = observeTransactionsUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.onAction(TransactionHistoryUiAction.Retry)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(transactions, state.transactions)
        assertNull(state.errorMessage)
        verify(exactly = 2) {
            observeTransactionsUseCase()
        }
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
