import assert from "node:assert/strict";
import { afterEach, test } from "node:test";

import { createPaymentServer } from "../src/app.js";

let server;

afterEach(async () => {
  if (server === undefined) {
    return;
  }

  await new Promise((resolve) => {
    server.close(resolve);
  });

  server = undefined;
});

test("when valid payment is posted then server returns created payment", async () => {
  // Given
  const baseUrl = await startServer();
  const requestBody = {
    recipientEmail: "customer@example.com",
    amountMinor: 10_050,
    currencyCode: "USD"
  };

  // When
  const response = await fetch(`${baseUrl}/payments`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(requestBody)
  });
  const responseBody = await response.json();

  // Then
  assert.equal(response.status, 201);
  assert.equal(typeof responseBody.id, "string");
  assert.ok(responseBody.id.length > 0);
  assert.equal(responseBody.recipientEmail, "customer@example.com");
  assert.equal(responseBody.amountMinor, 10_050);
  assert.equal(responseBody.currencyCode, "USD");
  assert.ok(Number.isSafeInteger(responseBody.createdAtMillis));
});

test("when invalid payment is posted then server returns bad request", async () => {
  // Given
  const baseUrl = await startServer();
  const requestBody = {
    recipientEmail: "invalid-email",
    amountMinor: 0,
    currencyCode: "KES"
  };

  // When
  const response = await fetch(`${baseUrl}/payments`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(requestBody)
  });
  const responseBody = await response.json();

  // Then
  assert.equal(response.status, 400);
  assert.deepEqual(responseBody.errors, [
    "Recipient email is invalid",
    "Amount minor units must be a positive integer",
    "Currency is not supported"
  ]);
});

test("when request content type is unsupported then server returns unsupported media type", async () => {
  // Given
  const baseUrl = await startServer();

  // When
  const response = await fetch(`${baseUrl}/payments`, {
    method: "POST",
    headers: {
      "Content-Type": "text/plain"
    },
    body: "payment"
  });
  const responseBody = await response.json();

  // Then
  assert.equal(response.status, 415);
  assert.equal(
    responseBody.message,
    "Content-Type must be application/json"
  );
});

test("when health endpoint is requested then server returns healthy status", async () => {
  // Given
  const baseUrl = await startServer();

  // When
  const response = await fetch(`${baseUrl}/health`);
  const responseBody = await response.json();

  // Then
  assert.equal(response.status, 200);
  assert.deepEqual(responseBody, {
    status: "ok"
  });
});

test("when unknown route is requested then server returns not found", async () => {
  // Given
  const baseUrl = await startServer();

  // When
  const response = await fetch(`${baseUrl}/unknown`);
  const responseBody = await response.json();

  // Then
  assert.equal(response.status, 404);
  assert.equal(responseBody.message, "Route not found");
});

async function startServer() {
  server = createPaymentServer();

  await new Promise((resolve) => {
    server.listen(0, "127.0.0.1", resolve);
  });

  const address = server.address();

  if (address === null || typeof address === "string") {
    throw new Error("Test server did not expose a TCP port");
  }

  return `http://127.0.0.1:${address.port}`;
}
