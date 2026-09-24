# ADR 0005: Use Koin at the Android composition root

- Status: Accepted
- Date: 2026-09-10

## Context

The Android layer constructs Firebase SDK objects, Ktor adapters, the
repository, shared use cases, and multiple ViewModels. Manual construction in
`MainActivity` would make ownership and testing less clear. Hilt is
Android-focused and would not demonstrate the KMP-friendly DI framework
requested for review.

## Decision

Use one small Koin module initialized by `CashiPayApplication`. Register
SDK/data objects as singletons, use cases as factories, and ViewModels with the
Koin ViewModel DSL.

## Consequences

- The composition root is centralized and replaceable dependencies remain
  constructor-injected.
- Production classes do not call a service locator directly.
- Koin adds a runtime dependency and defers some graph errors to startup, which
  is acceptable for this project size and covered by app/build verification.
