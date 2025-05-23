import { test, expect } from "@playwright/test";

test("healthcheck", async ({ request }) => {
  const response = await request.get("/health");

  expect(response.ok()).toBeTruthy();

  const body = await response.json() as { message: string };

  expect(body.message).toBe("Healthy");
});
