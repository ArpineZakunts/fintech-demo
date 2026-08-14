package com.example.fintechdemo.presentation.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fintechdemo.domain.model.PaymentStatus
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaymentScreen(
    onPaymentSucceeded: () -> Unit,
    viewModel: PaymentViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            if (effect is PaymentEffect.PaymentSucceeded) onPaymentSucceeded()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = state.amountInput,
            onValueChange = { viewModel.onAction(PaymentAction.AmountChanged(it)) },
            label = { Text("Amount (USD)") },
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = { viewModel.onAction(PaymentAction.SubmitPayment) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Create payment intent")
        }

        if (state.paymentIntent?.status == PaymentStatus.REQUIRES_CONFIRMATION) {
            Button(
                onClick = { viewModel.onAction(PaymentAction.ConfirmPayment) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Confirm payment")
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.errorMessage?.let { message ->
            Text(text = message)
        }
    }
}
