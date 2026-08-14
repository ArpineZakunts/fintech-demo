package com.example.fintechdemo.presentation.payment

import com.example.fintechdemo.domain.model.PaymentIntent
import com.example.fintechdemo.domain.model.PaymentStatus
import com.example.fintechdemo.domain.repository.PaymentRepository
import com.example.fintechdemo.domain.usecase.ConfirmPaymentUseCase
import com.example.fintechdemo.domain.usecase.CreatePaymentIntentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submitting a valid amount stores the returned payment intent`() = runTest {
        val fakeRepository = FakePaymentRepository(
            createResult = Result.success(
                PaymentIntent(
                    id = "pi_123",
                    clientSecret = "secret",
                    amountMinorUnits = 500,
                    currency = "USD",
                    status = PaymentStatus.REQUIRES_CONFIRMATION,
                ),
            ),
        )
        val viewModel = PaymentViewModel(
            createPaymentIntent = CreatePaymentIntentUseCase(fakeRepository),
            confirmPayment = ConfirmPaymentUseCase(fakeRepository),
        )

        viewModel.onAction(PaymentAction.AmountChanged("5.00"))
        viewModel.onAction(PaymentAction.SubmitPayment)
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("pi_123", state.paymentIntent?.id)
        assertEquals(500L, fakeRepository.lastRequestedAmount)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `submitting a non-numeric amount does not call the repository`() = runTest {
        val fakeRepository = FakePaymentRepository(createResult = Result.success(SAMPLE_INTENT))
        val viewModel = PaymentViewModel(
            createPaymentIntent = CreatePaymentIntentUseCase(fakeRepository),
            confirmPayment = ConfirmPaymentUseCase(fakeRepository),
        )

        viewModel.onAction(PaymentAction.AmountChanged("not a number"))
        viewModel.onAction(PaymentAction.SubmitPayment)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, fakeRepository.lastRequestedAmount)
    }

    private companion object {
        val SAMPLE_INTENT = PaymentIntent(
            id = "pi_000",
            clientSecret = "secret",
            amountMinorUnits = 100,
            currency = "USD",
            status = PaymentStatus.REQUIRES_CONFIRMATION,
        )
    }
}

private class FakePaymentRepository(
    private val createResult: Result<PaymentIntent>,
) : PaymentRepository {

    var lastRequestedAmount: Long? = null
        private set

    override suspend fun createPaymentIntent(amountMinorUnits: Long, currency: String): Result<PaymentIntent> {
        lastRequestedAmount = amountMinorUnits
        return createResult
    }

    override suspend fun confirmPayment(paymentIntentId: String): Result<PaymentIntent> = createResult
}
