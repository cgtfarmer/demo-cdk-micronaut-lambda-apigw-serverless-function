import { test, expect } from "@playwright/test";

test("add", async ({ request }) => {
  const response = await request.post("/add");

  expect(response.ok()).toBeTruthy();

  const body = (await response.json()) as { result: number };

  console.log(body);

  expect(body.result).toEqual(7);
});
