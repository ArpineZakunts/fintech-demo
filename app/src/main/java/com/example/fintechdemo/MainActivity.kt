package com.example.fintechdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.fintechdemo.presentation.aichat.AiChatScreen
import com.example.fintechdemo.presentation.payment.PaymentScreen

private enum class Destination { PAYMENT, AI_CHAT }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier) {
                    RootScreen()
                }
            }
        }
    }
}

@Composable
private fun RootScreen() {
    var destination by remember { mutableStateOf(Destination.PAYMENT) }

    when (destination) {
        Destination.PAYMENT -> PaymentScreen(onPaymentSucceeded = { destination = Destination.AI_CHAT })
        Destination.AI_CHAT -> AiChatScreen()
    }
}
