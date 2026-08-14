package com.example.fintechdemo.domain.usecase

import com.example.fintechdemo.domain.model.PaymentIntent
import com.example.fintechdemo.domain.repository.PaymentRepository

class CreatePaymentIntentUseCase(
    private val paymentRepository: PaymentRepository,
) {
    suspend operator fun invoke(amountMinorUnits: Long, currency: String): Result<PaymentIntent> {
        require(amountMinorUnits > 0) { "Amount must be positive" }
        return paymentRepository.createPaymentIntent(amountMinorUnits, currency)
    }
}
