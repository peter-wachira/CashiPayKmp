package com.peterwachira.cashipay.sharedLogic.domain.usecase

import com.peterwachira.cashipay.sharedLogic.domain.repository.PaymentRepository
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import kotlinx.coroutines.flow.Flow

/**
 * Provides the stream of payment transactions.
 */
class ObserveTransactionsUseCase(
    private val paymentRepository: PaymentRepository
) {
    operator fun invoke(): Flow<List<PaymentTransaction>> =
        paymentRepository.observeTransactions()
}