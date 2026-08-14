package com.example.fintechdemo.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AiMessageDto(
    val role: String,
    val content: String,
)

@Serializable
data class AiChatRequest(
    val model: String,
    val messages: List<AiMessageDto>,
)

@Serializable
data class AiChatResponse(
    val message: AiMessageDto,
)
