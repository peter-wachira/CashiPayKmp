package com.peterwachira.cashipay.sharedLogic.data.remote

import com.peterwachira.cashipay.sharedLogic.core.result.AppResult
import com.peterwachira.cashipay.sharedLogic.data.remote.dto.PaymentResponseDto
import com.peterwachira.cashipay.sharedLogic.data.remote.mapper.toDomain
import com.peterwachira.cashipay.sharedLogic.data.remote.mapper.toDto
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

/**
 * Sends payment requests to the remote service and maps responses into domain results.
 */
class PaymentApi internal constructor(
    private val httpClient: HttpClient,
    baseUrl: String
) {

    private val paymentsUrl: String

    init {
        require(baseUrl.isNotBlank()) {
            "Payment API base URL must not be blank"
        }

        paymentsUrl = "${baseUrl.trimEnd('/')}/payments"
    }

    constructor(baseUrl: String) : this(
        httpClient = createPaymentHttpClient(),
        baseUrl = baseUrl
    )

    suspend fun sendPayment(
        request: PaymentRequest
    ): AppResult<PaymentTransaction> {
        return try {
            val response = httpClient.post(paymentsUrl) {
                contentType(ContentType.Application.Json)
                setBody(request.toDto())
            }

            if (!response.status.isSuccess()) {
                return AppResult.Error(
                    message = "Payment request failed with status ${response.status.value}"
                )
            }

            val responseDto = response.body<PaymentResponseDto>()
            val transaction = responseDto.toDomain()

            if (transaction == null) {
                AppResult.Error(
                    message = "Payment service returned invalid data"
                )
            } else {
                AppResult.Success(transaction)
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            AppResult.Error(
                message = "Unable to reach payment service",
                cause = exception
            )
        }
    }

    fun close() {
        httpClient.close()
    }
}

private fun createPaymentHttpClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }
}
