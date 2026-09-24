package com.peterwachira.cashipay.presentation.payment

import com.peterwachira.cashipay.presentation.MainDispatcherRule
import com.peterwachira.cashipay.sharedLogic.domain.usecase.SendPaymentResult
import com.peterwachira.cashipay.sharedLogic.domain.usecase.SendPaymentUseCase
import com.peterwachira.cashipay.sharedLogic.domain.usecase.ValidatePaymentResult
import com.peterwachira.cashipay.sharedLogic.domain.usecase.ValidatePaymentUseCase
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
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
        val viewModel = createViewModel()

        // When
        enterValidPayment(viewModel)

        // Then
        val state = viewModel.uiState.value
        assertEquals("peterwachira@gmail.com", state.recipientEmail)
        assertEquals("45.50", state.amount)
        assertEquals(PaymentCurrency.EUR, state.selectedCurrency)
    }

    @Test
    fun `when payment is valid then review state is exposed`() {
        // Given
        val paymentInput = slot<PaymentInput>()
        val paymentRequest = validRequest()
        val validatePaymentUseCase = mockk<ValidatePaymentUseCase>()

        every {
            validatePaymentUseCase(capture(paymentInput))
        } returns ValidatePaymentResult.Valid(paymentRequest)

        val viewModel = createViewModel(
            validatePaymentUseCase = validatePaymentUseCase
        )
        enterValidPayment(viewModel)

        // When
        viewModel.onAction(PaymentUiAction.ReviewPayment)

        // Then
        assertEquals(paymentRequest, viewModel.uiState.value.paymentToReview)
        assertTrue(viewModel.uiState.value.validationErrors.isEmpty())
        assertEquals("peterwachira@gmail.com", paymentInput.captured.recipientEmail)
        assertEquals("45.50", paymentInput.captured.amount)
        assertEquals("EUR", paymentInput.captured.currencyCode)

        verify(exactly = 1) {
            validatePaymentUseCase(any())
        }
    }

    @Test
    fun `when payment is invalid then validation errors are exposed`() {
        // Given
        val errors = listOf(PaymentValidationError.InvalidRecipientEmail)
        val validatePaymentUseCase = mockk<ValidatePaymentUseCase>()

        every {
            validatePaymentUseCase(any())
        } returns ValidatePaymentResult.Invalid(errors)

        val viewModel = createViewModel(
            validatePaymentUseCase = validatePaymentUseCase
        )

        // When
        viewModel.onAction(PaymentUiAction.ReviewPayment)

        // Then
        assertEquals(errors, viewModel.uiState.value.validationErrors)
        assertNull(viewModel.uiState.value.paymentToReview)
    }

    @Test
    fun `when edit payment is selected then review state is cleared`() {
        // Given
        val paymentRequest = validRequest()
        val validatePaymentUseCase = mockk<ValidatePaymentUseCase>()

        every {
            validatePaymentUseCase(any())
        } returns ValidatePaymentResult.Valid(paymentRequest)

        val viewModel = createViewModel(
            validatePaymentUseCase = validatePaymentUseCase
        )
        enterValidPayment(viewModel)
        viewModel.onAction(PaymentUiAction.ReviewPayment)

        // When
        viewModel.onAction(PaymentUiAction.EditPayment)

        // Then
        assertNull(viewModel.uiState.value.paymentToReview)
        assertEquals("peterwachira@gmail.com", viewModel.uiState.value.recipientEmail)
        assertEquals("45.50", viewModel.uiState.value.amount)
    }

    @Test
    fun `when reviewed payment is confirmed then success state is exposed`() = runTest {
        // Given
        val transaction = validTransaction()
        val sentInput = slot<PaymentInput>()
        val validatePaymentUseCase = mockk<ValidatePaymentUseCase>()
        val sendPaymentUseCase = mockk<SendPaymentUseCase>()

        every {
            validatePaymentUseCase(any())
        } returns ValidatePaymentResult.Valid(validRequest())

        coEvery {
            sendPaymentUseCase(capture(sentInput))
        } returns SendPaymentResult.Success(transaction)

        val viewModel = createViewModel(
            validatePaymentUseCase = validatePaymentUseCase,
            sendPaymentUseCase = sendPaymentUseCase
        )
        enterValidPayment(viewModel)
        viewModel.onAction(PaymentUiAction.ReviewPayment)

        // When
        viewModel.onAction(PaymentUiAction.ConfirmPayment)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isSubmitting)
        assertNull(state.paymentToReview)
        assertEquals(transaction, state.submittedTransaction)
        assertEquals("", state.recipientEmail)
        assertEquals("", state.amount)
        assertEquals("peterwachira@gmail.com", sentInput.captured.recipientEmail)
        assertEquals("45.50", sentInput.captured.amount)

        coVerify(exactly = 1) {
            sendPaymentUseCase(any())
        }
    }

    @Test
    fun `when confirmation occurs before review then payment is not sent`() {
        // Given
        val sendPaymentUseCase = mockk<SendPaymentUseCase>()
        val viewModel = createViewModel(
            sendPaymentUseCase = sendPaymentUseCase
        )
        enterValidPayment(viewModel)

        // When
        viewModel.onAction(PaymentUiAction.ConfirmPayment)

        // Then
        assertFalse(viewModel.uiState.value.isSubmitting)

        coVerify(exactly = 0) {
            sendPaymentUseCase(any())
        }
    }

    @Test
    fun `when confirmation is repeated while processing then payment is sent once`() = runTest {
        // Given
        val sendGate = CompletableDeferred<Unit>()
        val validatePaymentUseCase = mockk<ValidatePaymentUseCase>()
        val sendPaymentUseCase = mockk<SendPaymentUseCase>()

        every {
            validatePaymentUseCase(any())
        } returns ValidatePaymentResult.Valid(validRequest())

        coEvery {
            sendPaymentUseCase(any())
        } coAnswers {
            sendGate.await()
            SendPaymentResult.Success(validTransaction())
        }

        val viewModel = createViewModel(
            validatePaymentUseCase = validatePaymentUseCase,
            sendPaymentUseCase = sendPaymentUseCase
        )
        enterValidPayment(viewModel)
        viewModel.onAction(PaymentUiAction.ReviewPayment)

        // When
        viewModel.onAction(PaymentUiAction.ConfirmPayment)
        runCurrent()
        viewModel.onAction(PaymentUiAction.ConfirmPayment)
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
    fun `when payment submission fails then review remains available`() = runTest {
        // Given
        val validatePaymentUseCase = mockk<ValidatePaymentUseCase>()
        val sendPaymentUseCase = mockk<SendPaymentUseCase>()

        every {
            validatePaymentUseCase(any())
        } returns ValidatePaymentResult.Valid(validRequest())

        coEvery {
            sendPaymentUseCase(any())
        } returns SendPaymentResult.Failure("Unable to send payment")

        val viewModel = createViewModel(
            validatePaymentUseCase = validatePaymentUseCase,
            sendPaymentUseCase = sendPaymentUseCase
        )
        enterValidPayment(viewModel)
        viewModel.onAction(PaymentUiAction.ReviewPayment)

        // When
        viewModel.onAction(PaymentUiAction.ConfirmPayment)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isSubmitting)
        assertEquals("Unable to send payment", state.submissionError)
        assertEquals(validRequest(), state.paymentToReview)
        assertNull(state.submittedTransaction)
    }

    private fun createViewModel(
        validatePaymentUseCase: ValidatePaymentUseCase = mockk(),
        sendPaymentUseCase: SendPaymentUseCase = mockk(),
    ): PaymentViewModel {
        return PaymentViewModel(
            validatePaymentUseCase = validatePaymentUseCase,
            sendPaymentUseCase = sendPaymentUseCase
        )
    }

    private fun enterValidPayment(viewModel: PaymentViewModel) {
        viewModel.onAction(
            PaymentUiAction.RecipientEmailChanged("peterwachira@gmail.com")
        )
        viewModel.onAction(
            PaymentUiAction.AmountChanged("45.50")
        )
        viewModel.onAction(
            PaymentUiAction.CurrencySelected(PaymentCurrency.EUR)
        )
    }

    private fun validRequest(): PaymentRequest {
        return PaymentRequest(
            recipientEmail = requireNotNull(
                RecipientEmail.from("peterwachira@gmail.com")
            ),
            amount = Money(
                amountMinor = requireNotNull(
                    MinorUnits.fromPositive(4_550L)
                ),
                currency = PaymentCurrency.EUR
            )
        )
    }

    private fun validTransaction(): PaymentTransaction {
        return PaymentTransaction(
            id = requireNotNull(TransactionId.from("transaction-1")),
            recipientEmail = requireNotNull(
                RecipientEmail.from("peterwachira@gmail.com")
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
