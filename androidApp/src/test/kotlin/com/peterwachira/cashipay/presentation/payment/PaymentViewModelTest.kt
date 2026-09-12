package com.peterwachira.cashipay.presentation.payment

import com.peterwachira.cashipay.presentation.MainDispatcherRule
import com.peterwachira.cashipay.sharedLogic.domain.usecase.SendPaymentResult
import com.peterwachira.cashipay.sharedLogic.domain.usecase.SendPaymentUseCase
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Verifies that payment actions produce the expected immutable UI state.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaymentViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when form actions are received then form state is updated`() {
        // Given
        val sendPaymentUseCase = mockk<SendPaymentUseCase>()
        val viewModel = PaymentViewModel(sendPaymentUseCase)

        // When
        viewModel.onAction(
            PaymentUiAction.RecipientEmailChanged("customer@example.com")
        )
        viewModel.onAction(
            PaymentUiAction.AmountChanged("45.50")
        )
        viewModel.onAction(
            PaymentUiAction.CurrencySelected(PaymentCurrency.EUR)
        )

        // Then
        val state = viewModel.uiState.value
        assertEquals("customer@example.com", state.recipientEmail)
        assertEquals("45.50", state.amount)
        assertEquals(PaymentCurrency.EUR, state.selectedCurrency)
    }

    @Test
    fun `when valid payment is submitted then success state is exposed`() = runTest {
        // Given
        val transaction = validTransaction()
        val paymentInput = slot<PaymentInput>()
        val sendPaymentUseCase = mockk<SendPaymentUseCase>()

        coEvery {
            sendPaymentUseCase(capture(paymentInput))
        } returns SendPaymentResult.Success(transaction)

        val viewModel = PaymentViewModel(sendPaymentUseCase)
        enterValidPayment(viewModel)

        // When
        viewModel.onAction(PaymentUiAction.Submit)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isSubmitting)
        assertEquals(transaction, state.submittedTransaction)
        assertEquals("", state.recipientEmail)
        assertEquals("", state.amount)
        assertNull(state.submissionError)
        assertEquals("customer@example.com", paymentInput.captured.recipientEmail)
        assertEquals("45.50", paymentInput.captured.amount)
        assertEquals("EUR", paymentInput.captured.currencyCode)

        coVerify(exactly = 1) {
            sendPaymentUseCase(any())
        }
    }

    @Test
    fun `when payment validation fails then validation errors are exposed`() = runTest {
        // Given
        val errors = listOf(PaymentValidationError.InvalidRecipientEmail)
        val sendPaymentUseCase = mockk<SendPaymentUseCase>()

        coEvery {
            sendPaymentUseCase(any())
        } returns SendPaymentResult.ValidationError(errors)

        val viewModel = PaymentViewModel(sendPaymentUseCase)
        enterValidPayment(viewModel)

        // When
        viewModel.onAction(PaymentUiAction.Submit)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isSubmitting)
        assertEquals(errors, state.validationErrors)
        assertNull(state.submittedTransaction)
        assertNull(state.submissionError)
    }

    @Test
    fun `when payment submission fails then error state is exposed`() = runTest {
        // Given
        val sendPaymentUseCase = mockk<SendPaymentUseCase>()

        coEvery {
            sendPaymentUseCase(any())
        } returns SendPaymentResult.Failure("Unable to send payment")

        val viewModel = PaymentViewModel(sendPaymentUseCase)
        enterValidPayment(viewModel)

        // When
        viewModel.onAction(PaymentUiAction.Submit)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isSubmitting)
        assertEquals("Unable to send payment", state.submissionError)
        assertNull(state.submittedTransaction)
    }

    @Test
    fun `when submit is repeated while processing then payment is sent once`() = runTest {
        // Given
        val sendGate = CompletableDeferred<Unit>()
        val sendPaymentUseCase = mockk<SendPaymentUseCase>()

        coEvery {
            sendPaymentUseCase(any())
        } coAnswers {
            sendGate.await()
            SendPaymentResult.Success(validTransaction())
        }

        val viewModel = PaymentViewModel(sendPaymentUseCase)
        enterValidPayment(viewModel)

        // When
        viewModel.onAction(PaymentUiAction.Submit)
        runCurrent()
        viewModel.onAction(PaymentUiAction.Submit)
        runCurrent()

        // Then
        assertTrue(viewModel.uiState.value.isSubmitting)
        coVerify(exactly = 1) {
            sendPaymentUseCase(any())
        }

        sendGate.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `when feedback is dismissed then feedback state is cleared`() = runTest {
        // Given
        val sendPaymentUseCase = mockk<SendPaymentUseCase>()

        coEvery {
            sendPaymentUseCase(any())
        } returns SendPaymentResult.Success(validTransaction())

        val viewModel = PaymentViewModel(sendPaymentUseCase)
        enterValidPayment(viewModel)
        viewModel.onAction(PaymentUiAction.Submit)
        advanceUntilIdle()

        // When
        viewModel.onAction(PaymentUiAction.DismissFeedback)

        // Then
        val state = viewModel.uiState.value
        assertNull(state.submittedTransaction)
        assertNull(state.submissionError)
    }

    private fun enterValidPayment(viewModel: PaymentViewModel) {
        viewModel.onAction(
            PaymentUiAction.RecipientEmailChanged("customer@example.com")
        )
        viewModel.onAction(
            PaymentUiAction.AmountChanged("45.50")
        )
        viewModel.onAction(
            PaymentUiAction.CurrencySelected(PaymentCurrency.EUR)
        )
    }

    private fun validTransaction(): PaymentTransaction {
        return PaymentTransaction(
            id = requireNotNull(TransactionId.from("transaction-1")),
            recipientEmail = requireNotNull(
                RecipientEmail.from("customer@example.com")
            ),
            amount = Money(
                amountMinor = requireNotNull(
                    MinorUnits.fromPositive(4_550L)
                ),
                currency = PaymentCurrency.EUR
            ),
            createdAtMillis = 1_000L
        )
    }
}
