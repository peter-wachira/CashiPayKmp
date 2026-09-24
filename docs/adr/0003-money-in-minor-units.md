# ADR 0003: Represent money in minor units

- Status: Accepted
- Date: 2026-08-04

## Context

Binary floating-point values such as `Double` cannot represent every decimal
fraction exactly. Financial domain code also benefits from preventing
negative/zero amounts and mismatched amount/currency primitives.

## Decision

Represent an amount with `Money(amountMinor, currency)`. Store the amount as a
positive `Long` in `MinorUnits`, and perform decimal parsing/formatting only at
UI and network boundaries.

## Consequences

- USD 25.00 is represented exactly as `2500` minor units.
- Value factories reject invalid values before they become domain objects.
- Arithmetic remains predictable for the supported two-decimal currencies.
- Supporting currencies with other minor-unit scales requires adding explicit
  currency metadata.
