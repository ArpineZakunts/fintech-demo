package com.example.fintechdemo.presentation.aichat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fintechdemo.domain.model.AiMessage
import org.koin.androidx.compose.koinViewModel

@Composable
fun AiChatScreen(
    viewModel: AiChatViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.history) { message ->
                ChatBubble(message)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = state.draftInput,
                onValueChange = { viewModel.onAction(AiChatAction.DraftChanged(it)) },
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = { viewModel.onAction(AiChatAction.SendMessage) },
                enabled = !state.isSending,
            ) {
                Text("Send")
            }
        }

        state.errorMessage?.let { message -> Text(text = message) }
    }
}

@Composable
private fun ChatBubble(message: AiMessage) {
    val prefix = if (message.role == AiMessage.Role.ASSISTANT) "AI" else "You"
    Text(text = "$prefix: ${message.content}", modifier = Modifier.padding(vertical = 4.dp))
}
