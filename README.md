# Fintech Demo (Android / Kotlin)

Small reference app showing how I structure a production Android client that talks to
payment (Stripe-style) and AI APIs. Built as a portfolio sample — not a production repo,
so the network layer talks to a `baseUrl` placeholder rather than live services and the
API keys stay server-side, as they should.

## Stack

- **Kotlin + Jetpack Compose** for UI
- **Clean Architecture as separate Gradle modules**, not just packages:
  - `:domain` — pure Kotlin/JVM module (models, repository interfaces, use cases). No Android
    or framework dependency, so it compiles and tests without an emulator.
  - `:data` — Android library module (Ktor client, DTOs, mappers, repository implementations).
    Depends on `:domain` only.
  - `:app` — presentation (MVI ViewModels + Compose screens) and DI wiring. Depends on both
    `:domain` and `:data`.
- **MVI**: each screen has a `State`, an `Action` sealed interface, and (where needed) a
  one-shot `Effect` channel for navigation/snackbars
- **Koin** for dependency injection
- **Ktor Client** with `kotlinx.serialization` for networking
- **Coroutines/Flow** throughout; `kotlinx-coroutines-test` for deterministic ViewModel tests

## What it demonstrates

- `domain/usecase/CreatePaymentIntentUseCase.kt` — a payment-intent flow modeled after
  Stripe's PaymentIntent API (create → confirm), with validation at the use-case boundary.
- `domain/usecase/SendAiMessageUseCase.kt` — a chat-style use case for an AI API integration
  (model name is configurable; the client never talks to the provider directly, only to our
  own backend proxy).
- `data/remote/dto/PaymentIntentMapper.kt` — mappers own DTO validation so repositories don't
  duplicate field checks.
- `presentation/payment/PaymentViewModel.kt` + `PaymentViewModelTest.kt` — MVI ViewModel with a
  fake-repository unit test (no mocking framework needed).

## Running

Open in Android Studio (Koala+), let Gradle sync, run the `app` configuration. The API calls
will fail against the placeholder `baseUrl` since there's no live backend behind this demo —
the point is the architecture and code shape, not a working payment flow.
