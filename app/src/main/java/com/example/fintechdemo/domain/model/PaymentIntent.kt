package com.example.fintechdemo.domain.model

data class PaymentIntent(
    val id: String,
    val clientSecret: String,
    val amountMinorUnits: Long,
    val currency: String,
    val status: PaymentStatus,
)

enum class PaymentStatus {
    REQUIRES_PAYMENT_METHOD,
    REQUIRES_CONFIRMATION,
    PROCESSING,
    SUCCEEDED,
    FAILED,
}
