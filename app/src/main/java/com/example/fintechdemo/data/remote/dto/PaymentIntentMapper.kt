package com.example.fintechdemo.data.remote.dto

import com.example.fintechdemo.domain.model.PaymentIntent
import com.example.fintechdemo.domain.model.PaymentStatus

/**
 * Owns validation of the raw DTO. Callers (repositories) trust this mapper
 * and must not re-check these fields themselves.
 */
fun PaymentIntentDto.toDomain(): PaymentIntent {
    require(id.isNotBlank()) { "PaymentIntentDto.id must not be blank" }
    require(clientSecret.isNotBlank()) { "PaymentIntentDto.clientSecret must not be blank" }
    require(amountMinorUnits > 0) { "PaymentIntentDto.amountMinorUnits must be positive" }

    return PaymentIntent(
        id = id,
        clientSecret = clientSecret,
        amountMinorUnits = amountMinorUnits,
        currency = currency.uppercase(),
        status = status.toPaymentStatus(),
    )
}

private fun String.toPaymentStatus(): PaymentStatus = when (this) {
    "requires_payment_method" -> PaymentStatus.REQUIRES_PAYMENT_METHOD
    "requires_confirmation" -> PaymentStatus.REQUIRES_CONFIRMATION
    "processing" -> PaymentStatus.PROCESSING
    "succeeded" -> PaymentStatus.SUCCEEDED
    else -> PaymentStatus.FAILED
}
