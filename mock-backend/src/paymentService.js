import { randomUUID } from "node:crypto";

const EMAIL_REGEX = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
const SUPPORTED_CURRENCIES = new Set(["USD", "EUR"]);

/**
 * Validates a payment request and creates a processed payment response.
 */
export function processPayment(
  input,
  transactionId = randomUUID(),
  createdAtMillis = Date.now()
) {
  if (input === null || typeof input !== "object" || Array.isArray(input)) {
    return {
      isSuccess: false,
      errors: ["Payment request must be a JSON object"]
    };
  }

  const recipientEmail = normalizeText(input.recipientEmail);
  const currencyCode = normalizeText(input.currencyCode).toUpperCase();
  const errors = [];

  if (!EMAIL_REGEX.test(recipientEmail)) {
    errors.push("Recipient email is invalid");
  }

  if (!Number.isSafeInteger(input.amountMinor) || input.amountMinor <= 0) {
    errors.push("Amount minor units must be a positive integer");
  }

  if (!SUPPORTED_CURRENCIES.has(currencyCode)) {
    errors.push("Currency is not supported");
  }

  if (errors.length > 0) {
    return {
      isSuccess: false,
      errors
    };
  }

  return {
    isSuccess: true,
    payment: {
      id: transactionId,
      recipientEmail,
      amountMinor: input.amountMinor,
      currencyCode,
      createdAtMillis
    }
  };
}

function normalizeText(value) {
  if (typeof value !== "string") {
    return "";
  }

  return value.trim();
}
