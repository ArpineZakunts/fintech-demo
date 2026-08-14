package com.example.fintechdemo.data.remote

import com.example.fintechdemo.data.remote.dto.AiChatRequest
import com.example.fintechdemo.data.remote.dto.AiChatResponse
import com.example.fintechdemo.data.remote.dto.AiMessageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Wrapper around our backend's AI proxy endpoint, which in turn calls the model provider.
 * The API key never lives on the client.
 */
class AiApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val model: String = "claude-sonnet-5",
) {
    suspend fun chat(history: List<AiMessageDto>): AiChatResponse =
        httpClient.post("$baseUrl/ai/chat") {
            contentType(ContentType.Application.Json)
            setBody(AiChatRequest(model = model, messages = history))
        }.body()
}
