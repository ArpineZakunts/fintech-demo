package com.example.fintechdemo.data.remote

import com.example.fintechdemo.data.remote.dto.CreatePaymentIntentRequest
import com.example.fintechdemo.data.remote.dto.PaymentIntentDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Thin wrapper around a Stripe-compatible payment backend.
 * Points at our own server's `/payments` proxy, never at Stripe directly from the client.
 */
class StripeApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    suspend fun createPaymentIntent(amountMinorUnits: Long, currency: String): PaymentIntentDto =
        httpClient.post("$baseUrl/payments/intents") {
            contentType(ContentType.Application.Json)
            setBody(CreatePaymentIntentRequest(amount = amountMinorUnits, currency = currency))
        }.body()

    suspend fun confirmPaymentIntent(paymentIntentId: String): PaymentIntentDto =
        httpClient.post("$baseUrl/payments/intents/$paymentIntentId/confirm").body()

    suspend fun getPaymentIntent(paymentIntentId: String): PaymentIntentDto =
        httpClient.get("$baseUrl/payments/intents/$paymentIntentId").body()
}
