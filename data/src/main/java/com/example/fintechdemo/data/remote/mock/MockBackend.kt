package com.example.fintechdemo.data.remote.mock

import com.example.fintechdemo.data.remote.dto.AiChatRequest
import com.example.fintechdemo.data.remote.dto.AiChatResponse
import com.example.fintechdemo.data.remote.dto.AiMessageDto
import com.example.fintechdemo.data.remote.dto.CreatePaymentIntentRequest
import com.example.fintechdemo.data.remote.dto.PaymentIntentDto
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory stand-in for our backend's `/payments` and `/ai` proxies.
 * Swapped in at the HTTP-engine level so the API classes, DTOs and mappers run unchanged.
 */
class MockBackend(private val latencyMillis: Long = 600) {

    private val json = Json { ignoreUnknownKeys = true }
    private val intents = ConcurrentHashMap<String, PaymentIntentDto>()

    val engine: HttpClientEngine = MockEngine { request ->
        delay(latencyMillis)
        route(request)
    }

    private fun MockRequestHandleScope.route(request: HttpRequestData): HttpResponseData {
        val segments = request.url.encodedPath.trim('/').split('/')
        return when {
            request.method == HttpMethod.Post && segments == listOf("payments", "intents") ->
                createIntent(request)

            request.method == HttpMethod.Post && segments.size == 4 &&
                segments[0] == "payments" && segments[1] == "intents" && segments[3] == "confirm" ->
                confirmIntent(segments[2])

            request.method == HttpMethod.Get && segments.size == 3 &&
                segments[0] == "payments" && segments[1] == "intents" ->
                intents[segments[2]]?.let { ok(it) } ?: notFound()

            request.method == HttpMethod.Post && segments == listOf("ai", "chat") ->
                chat(request)

            else -> notFound()
        }
    }

    private fun MockRequestHandleScope.createIntent(request: HttpRequestData): HttpResponseData {
        val body = json.decodeFromString<CreatePaymentIntentRequest>(request.bodyText())
        val id = "pi_mock_" + UUID.randomUUID().toString().replace("-", "").take(24)
        val intent = PaymentIntentDto(
            id = id,
            clientSecret = "${id}_secret_" + UUID.randomUUID().toString().take(8),
            amountMinorUnits = body.amount,
            currency = body.currency.lowercase(),
            status = "requires_confirmation",
        )
        intents[id] = intent
        return ok(intent)
    }

    private fun MockRequestHandleScope.confirmIntent(id: String): HttpResponseData {
        val confirmed = intents.computeIfPresent(id) { _, intent -> intent.copy(status = "succeeded") }
        return confirmed?.let { ok(it) } ?: notFound()
    }

    private fun MockRequestHandleScope.chat(request: HttpRequestData): HttpResponseData {
        val body = json.decodeFromString<AiChatRequest>(request.bodyText())
        val lastUserMessage = body.messages.lastOrNull { it.role == "user" }?.content.orEmpty()
        val reply = AiMessageDto(
            role = "assistant",
            content = "(mock ${body.model}) You said: \"$lastUserMessage\". " +
                "Connect a real backend to get model responses.",
        )
        return ok(AiChatResponse(message = reply))
    }

    private inline fun <reified T> MockRequestHandleScope.ok(payload: T): HttpResponseData =
        respond(
            content = json.encodeToString(payload),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
        )

    private fun MockRequestHandleScope.notFound(): HttpResponseData =
        respond(content = "Not found", status = HttpStatusCode.NotFound)

    private fun HttpRequestData.bodyText(): String = (body as? TextContent)?.text.orEmpty()
}
