package com.example.fintechdemo.domain.repository

import com.example.fintechdemo.domain.model.AiMessage

interface AiRepository {

    suspend fun sendMessage(history: List<AiMessage>): Result<AiMessage>
}
