package com.example.fintechdemo.domain.repository

import com.example.fintechdemo.domain.model.PaymentIntent

interface PaymentRepository {

    suspend fun createPaymentIntent(amountMinorUnits: Long, currency: String): Result<PaymentIntent>

    suspend fun confirmPayment(paymentIntentId: String): Result<PaymentIntent>
}
