# ADR 0002: Use MVVM with sealed UI actions

- Status: Accepted
- Date: 2026-09-12

## Context

Payment screens need immutable state, lifecycle-aware observation, and clear
user-event handling. Directly exposing many ViewModel callbacks is workable,
while full reducer-based MVI would add machinery that this small challenge does
not need.

## Decision

Use Android ViewModels that expose read-only `StateFlow` and accept feature
events through sealed action types such as `PaymentUiAction`. Route composables
connect lifecycle/ViewModel state to stateless screen composables.

## Consequences

- The event surface is explicit and exhaustive in `when` expressions.
- Screens remain previewable and do not depend on ViewModels or `NavController`.
- State transitions are easy to test through public actions.
- This is MVVM with action-based dispatch, not reducer/store MVI.
