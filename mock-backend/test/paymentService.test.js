import assert from "node:assert/strict";
import test from "node:test";

import { processPayment } from "../src/paymentService.js";

test("when payment request is valid then payment is processed", () => {
  // Given
  const input = {
    recipientEmail: "  peterwachira@email.com  ",
    amountMinor: 10_050,
    currencyCode: "usd"
  };

  // When
  const result = processPayment(
    input,
    "transaction-1",
    1_000
  );

  // Then
  assert.equal(result.isSuccess, true);
  assert.deepEqual(result.payment, {
    id: "transaction-1",
    recipientEmail: "peterwachira@email.com",
    amountMinor: 10_050,
    currencyCode: "USD",
    createdAtMillis: 1_000
  });
});

test("when recipient email is invalid then processing returns error", () => {
  // Given
  const input = {
    recipientEmail: "invalid-email",
    amountMinor: 10_050,
    currencyCode: "USD"
  };

  // When
  const result = processPayment(input);

  // Then
  assert.equal(result.isSuccess, false);
  assert.deepEqual(result.errors, [
    "Recipient email is invalid"
  ]);
});

test("when amount is not positive then processing returns error", () => {
  // Given
  const input = {
    recipientEmail: "peterwachira@email.com",
    amountMinor: 0,
    currencyCode: "USD"
  };

  // When
  const result = processPayment(input);

  // Then
  assert.equal(result.isSuccess, false);
  assert.deepEqual(result.errors, [
    "Amount minor units must be a positive integer"
  ]);
});

test("when currency is unsupported then processing returns error", () => {
  // Given
  const input = {
    recipientEmail: "peterwachira@email.com",
    amountMinor: 10_050,
    currencyCode: "KES"
  };

  // When
  const result = processPayment(input);

  // Then
  assert.equal(result.isSuccess, false);
  assert.deepEqual(result.errors, [
    "Currency is not supported"
  ]);
});
