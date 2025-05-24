import { test, expect } from "@playwright/test";

test("add", async ({ request }) => {
  const response = await request.post("/add");

  expect(response.status()).toEqual(200);

  const body = (await response.json()) as { result: number };

  console.log(body);

  expect(body.result).toEqual("7");
});
