package com.example.fintechdemo.domain.model

data class AiMessage(
    val role: Role,
    val content: String,
) {
    enum class Role { USER, ASSISTANT }
}
