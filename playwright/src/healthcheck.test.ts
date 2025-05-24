import { test, expect } from "@playwright/test";

test("healthcheck", async ({ request }) => {
  const response = await request.get("/health");

  expect(response.status()).toEqual(200);

  const body = await response.json() as { message: string };

  expect(body.message).toBe("Healthy");
});
