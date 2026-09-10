package com.peterwachira.cashipay.data.remote

import com.peterwachira.cashipay.sharedLogic.core.result.AppResult
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction

/**
 * Defines the remote operation used to process a payment.
 */
internal interface RemotePaymentDataSource {
    suspend fun sendPayment(
        request: PaymentRequest
    ): AppResult<PaymentTransaction>
}
