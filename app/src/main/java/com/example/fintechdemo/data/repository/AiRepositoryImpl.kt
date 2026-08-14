package com.example.fintechdemo.data.repository

import com.example.fintechdemo.data.remote.AiApi
import com.example.fintechdemo.data.remote.dto.toDomain
import com.example.fintechdemo.data.remote.dto.toDto
import com.example.fintechdemo.domain.model.AiMessage
import com.example.fintechdemo.domain.repository.AiRepository

class AiRepositoryImpl(
    private val aiApi: AiApi,
) : AiRepository {

    override suspend fun sendMessage(history: List<AiMessage>): Result<AiMessage> =
        runCatching {
            aiApi.chat(history.map { it.toDto() }).message.toDomain()
        }
}
