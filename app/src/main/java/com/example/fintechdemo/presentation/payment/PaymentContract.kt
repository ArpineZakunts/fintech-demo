package com.example.fintechdemo.presentation.payment

import com.example.fintechdemo.domain.model.PaymentIntent

data class PaymentState(
    val amountInput: String = "",
    val isLoading: Boolean = false,
    val paymentIntent: PaymentIntent? = null,
    val errorMessage: String? = null,
)

sealed interface PaymentAction {
    data class AmountChanged(val value: String) : PaymentAction
    data object SubmitPayment : PaymentAction
    data object ConfirmPayment : PaymentAction
}

sealed interface PaymentEffect {
    data class ShowError(val message: String) : PaymentEffect
    data object PaymentSucceeded : PaymentEffect
}
