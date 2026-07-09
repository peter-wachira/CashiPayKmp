# CashiPay KMP Challenge

A Kotlin Multiplatform Android-first payment app built for the Cashi Mobile Android engineering challenge.

## Current Scope

- Android app built with Jetpack Compose
- Shared Kotlin Multiplatform module for payment models, validation, API-facing logic, and use cases
- Firebase Firestore for transaction history
- Mock REST backend for payment processing
- Unit tests for shared business logic
- Additional test documentation for BDD, JMeter, and optional UI automation

## Architecture Direction

The project uses Kotlin Multiplatform for reusable business logic while keeping Android-specific platform concerns, such as Jetpack Compose UI and Firebase Android SDK integration, inside the Android app module.