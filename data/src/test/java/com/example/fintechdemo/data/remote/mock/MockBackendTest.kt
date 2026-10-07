package com.example.fintechdemo.data.remote.mock

import com.example.fintechdemo.data.remote.AiApi
import com.example.fintechdemo.data.remote.StripeApi
import com.example.fintechdemo.data.remote.dto.AiMessageDto
import com.example.fintechdemo.data.remote.dto.toDomain
import com.example.fintechdemo.domain.model.PaymentStatus
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockBackendTest {

    private val client = HttpClient(MockBackend(latencyMillis = 0).engine) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }
    private val baseUrl = "https://mock.local"

    @Test
    fun `create then confirm payment intent`() = runBlocking {
        val api = StripeApi(client, baseUrl)

        val created = api.createPaymentIntent(amountMinorUnits = 50_000, currency = "usd").toDomain()
        assertEquals(50_000, created.amountMinorUnits)
        assertEquals("USD", created.currency)
        assertEquals(PaymentStatus.REQUIRES_CONFIRMATION, created.status)

        val confirmed = api.confirmPaymentIntent(created.id).toDomain()
        assertEquals(PaymentStatus.SUCCEEDED, confirmed.status)
        assertEquals(PaymentStatus.SUCCEEDED, api.getPaymentIntent(created.id).toDomain().status)
    }

    @Test
    fun `ai chat echoes last user message`() = runBlocking {
        val response = AiApi(client, baseUrl).chat(listOf(AiMessageDto(role = "user", content = "hi")))

        assertEquals("assistant", response.message.role)
        assertTrue(response.message.content.contains("\"hi\""))
    }
}
