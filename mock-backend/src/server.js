import { createPaymentServer } from "./app.js";

const host = process.env.HOST || "0.0.0.0";
const port = readPort(process.env.PORT);
const server = createPaymentServer();

server.listen(port, host, () => {
  console.log(`CashiPay mock backend listening on http://${host}:${port}`);
});

function readPort(value) {
  if (value === undefined) {
    return 8080;
  }

  const port = Number(value);

  if (!Number.isInteger(port) || port < 1 || port > 65_535) {
    throw new Error("PORT must be an integer between 1 and 65535");
  }

  return port;
}
