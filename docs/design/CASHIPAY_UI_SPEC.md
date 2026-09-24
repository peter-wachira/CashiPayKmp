# CashiPay UI Direction

The `cashipay-screen-direction-v2.png` image is the final visual direction.
Jetpack Compose is the implementation source of truth, so small copy and layout
differences favor accessibility, current Material behavior, and implemented
product scope.

## Screen set

1. Home dashboard
2. Send payment form
3. Review payment
4. Payment success
5. Transaction history
6. Transaction details

Required state variants: empty form, invalid email, invalid amount, submitting,
submission error, empty history, and history loading.

## Android canvas and spacing

- Reference frame: 390 x 844 dp
- Horizontal screen gutter: 20-24 dp depending on the destination
- Base spacing unit: 4 dp
- Common spacing: 8, 12, 16, 24, and 32 dp
- Minimum touch target: 48 x 48 dp
- Primary button: 56 dp high
- Text field: 64 dp minimum height, excluding supporting/error text
- Cards: 16-24 dp corner radius
- Buttons and inputs: 16 dp corner radius
- Bottom navigation: 80 dp plus system navigation inset

## Color tokens

- `CashiPrimary`: `#0B6B57`
- `CashiPrimaryDark`: `#075244`
- `CashiMint`: `#BDF4DF`
- `CashiMintSoft`: `#EAFBF4`
- `CashiBackground`: `#F6FAF8`
- `CashiSurface`: `#FFFFFF`
- `CashiInk`: `#102A2A`
- `CashiMuted`: `#627878`
- `CashiBorder`: `#D9E6E1`
- `CashiSuccess`: `#177A55`
- `CashiError`: `#C94444`

## Type hierarchy

- Display amount: 32 sp, bold
- Screen title: 24 sp, bold
- Section title: 18 sp, semibold
- Body: 16 sp, regular
- Label: 14 sp, semibold
- Supporting text: 12 sp, regular/medium

## Interaction principles

- Always show recipient, amount, and currency together before submission.
- Use a review step before calling the payment API.
- Disable repeated submission while `isSubmitting` is true.
- Explain validation errors next to their corresponding field.
- Success and failure states must use text and iconography, not color alone.
- Transaction history should visibly communicate Firestore synchronization and
  real-time updates.

## Attribution

Use “Product concept by Peter Wachira” only in portfolio/README presentation
material. Keep attribution and hard-coded personal names out of transactional
production screens.

## Implementation notes

- Home uses a concise welcome message rather than a hard-coded customer name.
- The send flow hides bottom navigation while the user enters, reviews, and
  confirms a payment.
- Home and Activity expose the primary navigation bar; details screens use
  focused back navigation.
- Details resolve a transaction from its ID rather than receiving an entire
  object through navigation.
- Loading, empty, error, submitting, validation, and success states are
  represented explicitly.
