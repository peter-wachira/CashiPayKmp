# ADR 0001: Use an Android-first KMP architecture

- Status: Accepted
- Date: 2026-08-01

## Context

The challenge requires a complete Android app and shared business logic that
could support another platform. Sharing the Compose UI or Firebase integration
would increase delivery and learning complexity without improving the required
Android outcome.

## Decision

Keep domain models, validation, repository contracts, use cases, DTO mapping,
and the Ktor client in `sharedLogic`. Keep Compose, ViewModels, navigation,
Koin, Firebase, and the Android repository implementation in `androidApp`. Do
not maintain an unused `sharedUI` module.

## Consequences

- Business rules and networking compile for Android and iOS.
- The Android product can use platform-native lifecycle and Firebase APIs
  directly.
- A future iOS app can reuse the shared domain/API layer but must provide its
  own repository implementation and UI.
- Some platform data code is intentionally not shared until a second client
  proves the abstraction useful.
