package com.example.fintechdemo.domain.usecase

import com.example.fintechdemo.domain.model.PaymentIntent
import com.example.fintechdemo.domain.repository.PaymentRepository

class ConfirmPaymentUseCase(
    private val paymentRepository: PaymentRepository,
) {
    suspend operator fun invoke(paymentIntentId: String): Result<PaymentIntent> =
        paymentRepository.confirmPayment(paymentIntentId)
}
