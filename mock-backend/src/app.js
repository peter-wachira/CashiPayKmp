import { createServer } from "node:http";

import { processPayment } from "./paymentService.js";

const MAX_REQUEST_BODY_BYTES = 16_384;

/**
 * Creates the HTTP server that exposes the payment API.
 */
export function createPaymentServer() {
  return createServer((request, response) => {
    handleRequest(request, response).catch((error) => {
      console.error("Unexpected payment server error", error);

      sendJson(response, 500, {
        message: "Unexpected server error"
      });
    });
  });
}

async function handleRequest(request, response) {
  if (request.method === "GET" && request.url === "/health") {
    sendJson(response, 200, {
      status: "ok"
    });
    return;
  }

  if (request.method !== "POST" || request.url !== "/payments") {
    sendJson(response, 404, {
      message: "Route not found"
    });
    return;
  }

  if (!hasJsonContentType(request)) {
    sendJson(response, 415, {
      message: "Content-Type must be application/json"
    });
    return;
  }

  const bodyResult = await readJsonBody(request);

  if (!bodyResult.isSuccess) {
    sendJson(response, 400, {
      errors: [bodyResult.error]
    });
    return;
  }

  const paymentResult = processPayment(bodyResult.data);

  if (!paymentResult.isSuccess) {
    sendJson(response, 400, {
      errors: paymentResult.errors
    });
    return;
  }

  sendJson(response, 201, paymentResult.payment);
}

function hasJsonContentType(request) {
  const contentType = request.headers["content-type"];

  if (typeof contentType !== "string") {
    return false;
  }

  return contentType
    .toLowerCase()
    .startsWith("application/json");
}

async function readJsonBody(request) {
  const chunks = [];
  let totalBytes = 0;

  for await (const chunk of request) {
    totalBytes += chunk.length;

    if (totalBytes > MAX_REQUEST_BODY_BYTES) {
      return {
        isSuccess: false,
        error: "Request body is too large"
      };
    }

    chunks.push(chunk);
  }

  const rawBody = Buffer.concat(chunks).toString("utf8");

  if (rawBody.trim() === "") {
    return {
      isSuccess: false,
      error: "Request body is required"
    };
  }

  try {
    return {
      isSuccess: true,
      data: JSON.parse(rawBody)
    };
  } catch {
    return {
      isSuccess: false,
      error: "Request body must contain valid JSON"
    };
  }
}

function sendJson(response, statusCode, body) {
  const jsonBody = JSON.stringify(body);

  response.writeHead(statusCode, {
    "Content-Type": "application/json; charset=utf-8",
    "Content-Length": Buffer.byteLength(jsonBody)
  });
  response.end(jsonBody);
}
