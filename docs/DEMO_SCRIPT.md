# CashiPay demo script

Target length: five to seven minutes.

## Preparation

1. Start the mock backend with `cd mock-backend && npm start`.
2. Launch `androidApp` on an Android emulator with network access.
3. Confirm Firebase Anonymous Auth and Firestore are enabled.
4. Keep the repository README and `firestore.rules` open for the architecture
   discussion.

## Product walkthrough

1. Open Home and point out the primary send action plus Firestore-backed recent
   activity.
2. Open Send and submit an invalid email or zero amount. Explain that validation
   is implemented in `sharedLogic`, not in the composable.
3. Enter a valid recipient, amount, and USD/EUR currency.
4. Select Review payment and explain why a financial action has a deliberate
   confirmation step.
5. Confirm the payment. Point out the disabled/loading submission state that
   prevents duplicate taps.
6. On Payment sent, open View details and show the backend-generated transaction
   ID and timestamp.
7. Return to Activity and show that the saved transaction arrived through the
   Firestore snapshot listener.

## Architecture discussion

Use the diagrams in the README to explain:

- why domain logic and Ktor live in `sharedLogic`;
- why Compose and Firebase stay in `androidApp`;
- how dependency inversion lets the shared use case call the Android repository;
- how Koin wires the graph without hiding dependencies from constructors;
- why money uses integer minor units rather than `Double`;
- why the app passes a transaction ID through navigation instead of a full
  object;
- why this is MVVM with sealed action dispatch rather than reducer-based MVI.

## Testing discussion

Show one Given/When/Then unit test, the backend black-box tests,
`testing/bdd/payment.feature`, the JMeter plan, and the GitHub Actions workflow.
Explain that test doubles are selected by purpose: focused fakes for stateful
repository behavior and MockK where interaction verification is valuable.

## Trade-offs and next steps

Close by acknowledging the challenge boundaries: anonymous auth, local mock
processing, Android-only production UI, and no idempotent server-side Firestore
reconciliation. Describe the production path from the README rather than
claiming those concerns are already solved.
