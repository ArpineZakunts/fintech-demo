package com.example.fintechdemo.di

import com.example.fintechdemo.data.remote.AiApi
import com.example.fintechdemo.data.remote.StripeApi
import com.example.fintechdemo.data.repository.AiRepositoryImpl
import com.example.fintechdemo.data.repository.PaymentRepositoryImpl
import com.example.fintechdemo.domain.repository.AiRepository
import com.example.fintechdemo.domain.repository.PaymentRepository
import com.example.fintechdemo.domain.usecase.ConfirmPaymentUseCase
import com.example.fintechdemo.domain.usecase.CreatePaymentIntentUseCase
import com.example.fintechdemo.domain.usecase.SendAiMessageUseCase
import com.example.fintechdemo.presentation.aichat.AiChatViewModel
import com.example.fintechdemo.presentation.payment.PaymentViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val BASE_URL = "https://api.example-fintech-demo.com"

val networkModule = module {
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    single { StripeApi(httpClient = get(), baseUrl = BASE_URL) }
    single { AiApi(httpClient = get(), baseUrl = BASE_URL) }
}

val repositoryModule = module {
    single<PaymentRepository> { PaymentRepositoryImpl(stripeApi = get()) }
    single<AiRepository> { AiRepositoryImpl(aiApi = get()) }
}

val useCaseModule = module {
    factory { CreatePaymentIntentUseCase(paymentRepository = get()) }
    factory { ConfirmPaymentUseCase(paymentRepository = get()) }
    factory { SendAiMessageUseCase(aiRepository = get()) }
}

val viewModelModule = module {
    viewModel { PaymentViewModel(createPaymentIntent = get(), confirmPayment = get()) }
    viewModel { AiChatViewModel(sendAiMessage = get()) }
}

val appModules = listOf(networkModule, repositoryModule, useCaseModule, viewModelModule)
