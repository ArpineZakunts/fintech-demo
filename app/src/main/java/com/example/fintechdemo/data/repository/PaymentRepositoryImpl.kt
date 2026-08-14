package com.example.fintechdemo.data.repository

import com.example.fintechdemo.data.remote.StripeApi
import com.example.fintechdemo.data.remote.dto.toDomain
import com.example.fintechdemo.domain.model.PaymentIntent
import com.example.fintechdemo.domain.repository.PaymentRepository

class PaymentRepositoryImpl(
    private val stripeApi: StripeApi,
) : PaymentRepository {

    override suspend fun createPaymentIntent(amountMinorUnits: Long, currency: String): Result<PaymentIntent> =
        runCatching {
            stripeApi.createPaymentIntent(amountMinorUnits, currency).toDomain()
        }

    override suspend fun confirmPayment(paymentIntentId: String): Result<PaymentIntent> =
        runCatching {
            stripeApi.confirmPaymentIntent(paymentIntentId).toDomain()
        }
}
