package com.peterwachira.cashipay.sharedLogic.domain.usecase

import com.peterwachira.cashipay.sharedLogic.core.result.AppResult
import com.peterwachira.cashipay.sharedLogic.domain.repository.PaymentRepository
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentInput
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.validation.PaymentValidationError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

internal class SendPaymentUseCaseTest {

    @Test
    fun `when input is valid then payment succeeds`() = runTest {
        // Given
        val transaction = sampleTransaction()
        val repository = FakePaymentRepository(
            sendPaymentResult = AppResult.Success(transaction)
        )
        val useCase = SendPaymentUseCase(repository)

        // When
        val result = useCase(validInput())

        // Then
        assertIs<SendPaymentResult.Success>(result)
        assertEquals(transaction, result.transaction)
        assertEquals(1, repository.sendPaymentCallCount)
    }

    @Test
    fun `when email is invalid then repository is not called`() = runTest {
        // Given
        val repository = FakePaymentRepository(
            sendPaymentResult = AppResult.Success(sampleTransaction())
        )
        val useCase = SendPaymentUseCase(repository)

        // When
        val result = useCase(
            validInput().copy(recipientEmail = "invalid-email")
        )

        // Then
        assertIs<SendPaymentResult.ValidationError>(result)
        assertEquals(
            listOf(PaymentValidationError.InvalidRecipientEmail),
            result.errors
        )
        assertEquals(0, repository.sendPaymentCallCount)
        assertNull(repository.lastRequest)
    }

    @Test
    fun `when repository fails then payment returns failure`() = runTest {
        // Given
        val repository = FakePaymentRepository(
            sendPaymentResult = AppResult.Error("Unable to send payment")
        )
        val useCase = SendPaymentUseCase(repository)

        // When
        val result = useCase(validInput())

        // Then
        assertIs<SendPaymentResult.Failure>(result)
        assertEquals("Unable to send payment", result.message)
        assertEquals(1, repository.sendPaymentCallCount)
    }

    @Test
    fun `when amount is invalid then repository is not called`() = runTest {
        // Given
        val repository = FakePaymentRepository(
            sendPaymentResult = AppResult.Success(sampleTransaction())
        )
        val useCase = SendPaymentUseCase(repository)

        // When
        val result = useCase(
            validInput().copy(amount = "0")
        )

        // Then
        assertIs<SendPaymentResult.ValidationError>(result)
        assertEquals(
            listOf(PaymentValidationError.AmountMustBeGreaterThanZero),
            result.errors
        )
        assertEquals(0, repository.sendPaymentCallCount)
        assertNull(repository.lastRequest)
    }

    private fun validInput() = PaymentInput(
        recipientEmail = "customer@example.com",
        amount = "100.50",
        currencyCode = "USD"
    )

    private fun sampleTransaction() = PaymentTransaction(
        id = "transaction-1",
        recipientEmail = "customer@example.com",
        amount = Money(
            amountMinor = 10_050L,
            currency = PaymentCurrency.USD
        ),
        createdAtMillis = 1_000L
    )
}

private class FakePaymentRepository(
    private val sendPaymentResult: AppResult<PaymentTransaction>
) : PaymentRepository {

    var sendPaymentCallCount = 0
        private set

    var lastRequest: PaymentRequest? = null
        private set

    override suspend fun sendPayment(
        request: PaymentRequest
    ): AppResult<PaymentTransaction> {
        sendPaymentCallCount++
        lastRequest = request
        return sendPaymentResult
    }

    override fun observeTransactions(): Flow<List<PaymentTransaction>> {
        return flowOf(emptyList())
    }
}