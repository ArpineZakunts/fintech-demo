package com.example.fintechdemo.presentation.aichat

import com.example.fintechdemo.domain.model.AiMessage

data class AiChatState(
    val history: List<AiMessage> = emptyList(),
    val draftInput: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface AiChatAction {
    data class DraftChanged(val value: String) : AiChatAction
    data object SendMessage : AiChatAction
}
