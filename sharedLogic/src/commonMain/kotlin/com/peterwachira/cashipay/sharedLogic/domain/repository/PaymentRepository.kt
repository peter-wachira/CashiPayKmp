package com.peterwachira.cashipay.sharedLogic.domain.repository

import com.peterwachira.cashipay.sharedLogic.core.result.AppResult
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import kotlinx.coroutines.flow.Flow

/**
 * Defines the payment data operations required by the domain layer.
 */
interface PaymentRepository {
    suspend fun sendPayment(request: PaymentRequest): AppResult<PaymentTransaction>

    fun observeTransactions(): Flow<List<PaymentTransaction>>
}
