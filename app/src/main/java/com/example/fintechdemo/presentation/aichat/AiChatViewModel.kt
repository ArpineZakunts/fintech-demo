package com.example.fintechdemo.presentation.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintechdemo.domain.model.AiMessage
import com.example.fintechdemo.domain.usecase.SendAiMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AiChatViewModel(
    private val sendAiMessage: SendAiMessageUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AiChatState())
    val state: StateFlow<AiChatState> = _state.asStateFlow()

    fun onAction(action: AiChatAction) {
        when (action) {
            is AiChatAction.DraftChanged -> _state.update { it.copy(draftInput = action.value) }
            AiChatAction.SendMessage -> sendCurrentDraft()
        }
    }

    private fun sendCurrentDraft() {
        val draft = state.value.draftInput.trim()
        if (draft.isEmpty()) return

        val userMessage = AiMessage(role = AiMessage.Role.USER, content = draft)
        val updatedHistory = state.value.history + userMessage

        _state.update { it.copy(history = updatedHistory, draftInput = "", isSending = true, errorMessage = null) }

        viewModelScope.launch {
            sendAiMessage(updatedHistory)
                .onSuccess { reply ->
                    _state.update { it.copy(history = it.history + reply, isSending = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isSending = false, errorMessage = error.message) }
                }
        }
    }
}
