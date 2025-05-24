import { test, expect } from "@playwright/test";

test("nonexistent route", async ({ request }) => {
  const response = await request.get("/does-not-exist");

  expect(response.status()).toEqual(404);

  const body = (await response.json()) as { message: string };

  expect(body.message).toEqual("Not Found");
});
