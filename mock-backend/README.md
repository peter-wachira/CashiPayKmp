# CashiPay Mock Backend

A small Node.js HTTP service that processes payments for the CashiPay Android/KMP challenge.

## Requirements

- Node.js 20 or newer

No third-party npm dependencies are required.

## Run tests

From the repository root:

```bash
cd mock-backend
npm test
```

## Start the server

```bash
cd mock-backend
npm start
```

The server listens on port `8080` by default.

## Check server health

```bash
curl http://localhost:8080/health
```

Expected response:

```json
{"status":"ok"}
```

## Process a payment

```bash
curl \
  --request POST \
  --header "Content-Type: application/json" \
  --data '{"recipientEmail":"customer@example.com","amountMinor":10050,"currencyCode":"USD"}' \
  http://localhost:8080/payments
```

A successful request returns HTTP `201` with a generated transaction ID and timestamp:

```json
{
  "id": "generated-transaction-id",
  "recipientEmail": "customer@example.com",
  "amountMinor": 10050,
  "currencyCode": "USD",
  "createdAtMillis": 1000
}
```

## Android Emulator URL

The Android emulator reaches the host computer through:

```text
http://10.0.2.2:8080
```

Do not use `localhost` from the emulator because it refers to the emulator itself.
