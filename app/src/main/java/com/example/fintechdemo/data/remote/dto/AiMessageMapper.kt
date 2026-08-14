package com.example.fintechdemo.data.remote.dto

import com.example.fintechdemo.domain.model.AiMessage

fun AiMessageDto.toDomain(): AiMessage {
    require(content.isNotBlank()) { "AiMessageDto.content must not be blank" }

    return AiMessage(
        role = if (role == "assistant") AiMessage.Role.ASSISTANT else AiMessage.Role.USER,
        content = content,
    )
}

fun AiMessage.toDto(): AiMessageDto = AiMessageDto(
    role = if (role == AiMessage.Role.ASSISTANT) "assistant" else "user",
    content = content,
)
