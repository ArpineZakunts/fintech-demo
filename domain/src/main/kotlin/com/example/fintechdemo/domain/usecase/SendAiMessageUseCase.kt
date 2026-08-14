package com.example.fintechdemo.domain.usecase

import com.example.fintechdemo.domain.model.AiMessage
import com.example.fintechdemo.domain.repository.AiRepository

class SendAiMessageUseCase(
    private val aiRepository: AiRepository,
) {
    suspend operator fun invoke(history: List<AiMessage>): Result<AiMessage> {
        require(history.isNotEmpty()) { "Conversation history must not be empty" }
        return aiRepository.sendMessage(history)
    }
}
