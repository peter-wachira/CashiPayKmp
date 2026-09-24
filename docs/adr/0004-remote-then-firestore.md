# ADR 0004: Process remotely before Firestore persistence

- Status: Accepted
- Date: 2026-09-10

## Context

The challenge requires the Android app to call a payment backend and then sync
successful payments to Firestore. Firestore also supplies real-time transaction
history.

## Decision

`AndroidPaymentRepository` first calls the remote payment data source. Only a
successful backend transaction is written to the authenticated user's Firestore
collection. History is exposed as a `Flow` built from a Firestore snapshot
listener.

## Consequences

- Failed payment requests are never displayed as completed activity.
- The backend-generated ID and timestamp are preserved in Firestore.
- Firestore automatically updates Home and Activity collectors.
- A backend success followed by a Firestore failure is a partial failure; the
  app reports it, while a production system would reconcile it server-side using
  idempotency.
