package com.example.fintechdemo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentIntentDto(
    val id: String,
    @SerialName("client_secret") val clientSecret: String,
    @SerialName("amount") val amountMinorUnits: Long,
    val currency: String,
    val status: String,
)

@Serializable
data class CreatePaymentIntentRequest(
    val amount: Long,
    val currency: String,
)
