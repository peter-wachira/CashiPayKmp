package com.peterwachira.cashipay.data.remote

import com.peterwachira.cashipay.sharedLogic.core.result.AppResult
import com.peterwachira.cashipay.sharedLogic.data.remote.PaymentApi
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction

/**
 * Adapts the shared Ktor payment API to the Android data-source contract.
 */
internal class KtorPaymentRemoteDataSource(
    private val paymentApi: PaymentApi
) : RemotePaymentDataSource {

    override suspend fun sendPayment(
        request: PaymentRequest
    ): AppResult<PaymentTransaction> {
        return paymentApi.sendPayment(request)
    }
}
