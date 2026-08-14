package com.example.fintechdemo.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintechdemo.domain.usecase.ConfirmPaymentUseCase
import com.example.fintechdemo.domain.usecase.CreatePaymentIntentUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val USD = "usd"
private const val CENTS_PER_UNIT = 100

class PaymentViewModel(
    private val createPaymentIntent: CreatePaymentIntentUseCase,
    private val confirmPayment: ConfirmPaymentUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state: StateFlow<PaymentState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<PaymentEffect>()
    val effects: SharedFlow<PaymentEffect> = _effects

    fun onAction(action: PaymentAction) {
        when (action) {
            is PaymentAction.AmountChanged -> _state.update { it.copy(amountInput = action.value) }
            PaymentAction.SubmitPayment -> submitPayment()
            PaymentAction.ConfirmPayment -> confirmCurrentPayment()
        }
    }

    private fun submitPayment() {
        val amount = state.value.amountInput.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            viewModelScope.launch { _effects.emit(PaymentEffect.ShowError("Enter a valid amount")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            createPaymentIntent(amountMinorUnits = (amount * CENTS_PER_UNIT).toLong(), currency = USD)
                .onSuccess { intent ->
                    _state.update { it.copy(isLoading = false, paymentIntent = intent) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                    _effects.emit(PaymentEffect.ShowError(error.message ?: "Payment failed"))
                }
        }
    }

    private fun confirmCurrentPayment() {
        val intentId = state.value.paymentIntent?.id ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            confirmPayment(intentId)
                .onSuccess { intent ->
                    _state.update { it.copy(isLoading = false, paymentIntent = intent) }
                    _effects.emit(PaymentEffect.PaymentSucceeded)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                    _effects.emit(PaymentEffect.ShowError(error.message ?: "Confirmation failed"))
                }
        }
    }
}
