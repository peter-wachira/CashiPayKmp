package com.peterwachira.cashipay.sharedLogic.data.remote

import com.peterwachira.cashipay.sharedLogic.core.result.AppResult
import com.peterwachira.cashipay.sharedLogic.model.MinorUnits
import com.peterwachira.cashipay.sharedLogic.model.Money
import com.peterwachira.cashipay.sharedLogic.model.PaymentCurrency
import com.peterwachira.cashipay.sharedLogic.model.PaymentRequest
import com.peterwachira.cashipay.sharedLogic.model.PaymentTransaction
import com.peterwachira.cashipay.sharedLogic.model.RecipientEmail
import com.peterwachira.cashipay.sharedLogic.model.TransactionId
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class PaymentApiTest {

    private var paymentApi: PaymentApi? = null

    @AfterTest
    fun tearDown() {
        paymentApi?.close()
    }

    @Test
    fun `when server returns successful payment then api returns transaction`() = runTest {
        // Given
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/payments", request.url.encodedPath)

            val requestBody = assertIs<TextContent>(request.body)
            assertEquals(ContentType.Application.Json, requestBody.contentType)
            assertEquals(
                """
                {"recipientEmail":"customer@example.com","amountMinor":10050,"currencyCode":"USD"}
                """.trimIndent(),
                requestBody.text
            )

            respond(
                content = successfulResponseJson(),
                status = HttpStatusCode.Created,
                headers = jsonHeaders()
            )
        }

        val api = createPaymentApi(engine)

        // When
        val result = api.sendPayment(validRequest())

        // Then
        val success = assertIs<AppResult.Success<PaymentTransaction>>(result)
        assertEquals(validTransaction(), success.data)
    }

    @Test
    fun `when server returns error status then api returns error`() = runTest {
        // Given
        val engine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val api = createPaymentApi(engine)

        // When
        val result = api.sendPayment(validRequest())

        // Then
        val error = assertIs<AppResult.Error>(result)
        assertEquals("Payment request failed with status 400", error.message)
        assertNull(error.cause)
    }

    @Test
    fun `when server response is invalid then api returns error`() = runTest {
        // Given
        val engine = MockEngine {
            respond(
                content = """
                    {
                      "id": "transaction-1",
                      "recipientEmail": "invalid-email",
                      "amountMinor": 10050,
                      "currencyCode": "USD",
                      "createdAtMillis": 1000
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = jsonHeaders()
            )
        }
        val api = createPaymentApi(engine)

        // When
        val result = api.sendPayment(validRequest())

        // Then
        val error = assertIs<AppResult.Error>(result)
        assertEquals(
            "Payment service returned invalid data",
            error.message
        )
    }

    @Test
    fun `when network request fails then api returns error`() = runTest {
        // Given
        val engine = MockEngine {
            throw IllegalStateException("Network unavailable")
        }
        val api = createPaymentApi(engine)

        // When
        val result = api.sendPayment(validRequest())

        // Then
        val error = assertIs<AppResult.Error>(result)
        assertEquals("Unable to reach payment service", error.message)
        assertNotNull(error.cause)
    }

    @Test
    fun `when payment request is cancelled then api rethrows cancellation`() = runTest {
        // Given
        val engine = MockEngine {
            throw CancellationException("Request cancelled")
        }
        val api = createPaymentApi(engine)

        // When / Then
        assertFailsWith<CancellationException> {
            api.sendPayment(validRequest())
        }
    }

    private fun createPaymentApi(engine: MockEngine): PaymentApi {
        val httpClient = HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }

        val api = PaymentApi(
            httpClient = httpClient,
            baseUrl = BASE_URL
        )

        paymentApi = api
        return api
    }

    private fun validRequest(): PaymentRequest {
        return PaymentRequest(
            recipientEmail = requireNotNull(
                RecipientEmail.from("customer@example.com")
            ),
            amount = Money(
                amountMinor = requireNotNull(
                    MinorUnits.fromPositive(10_050L)
                ),
                currency = PaymentCurrency.USD
            )
        )
    }

    private fun validTransaction(): PaymentTransaction {
        return PaymentTransaction(
            id = requireNotNull(
                TransactionId.from("transaction-1")
            ),
            recipientEmail = requireNotNull(
                RecipientEmail.from("customer@example.com")
            ),
            amount = Money(
                amountMinor = requireNotNull(
                    MinorUnits.fromPositive(10_050L)
                ),
                currency = PaymentCurrency.USD
            ),
            createdAtMillis = 1_000L
        )
    }

    private fun successfulResponseJson(): String {
        return """
            {
              "id": "transaction-1",
              "recipientEmail": "customer@example.com",
              "amountMinor": 10050,
              "currencyCode": "USD",
              "createdAtMillis": 1000
            }
        """.trimIndent()
    }

    private fun jsonHeaders() = headersOf(
        HttpHeaders.ContentType,
        ContentType.Application.Json.toString()
    )

    private companion object {
        const val BASE_URL = "https://api.cashipay.test"
    }
}
