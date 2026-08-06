import { expect, test, type Page } from "@playwright/test";

async function mockBff(page: Page, loginStatus = 200) {
  await page.route("**/mobile/v1/**", async (route) => {
    const url = new URL(route.request().url());
    if (url.pathname.endsWith("/session")) {
      if (loginStatus !== 200)
        return route.fulfill({
          status: loginStatus,
          contentType: "application/json",
          body: JSON.stringify({ code: "AUTH_INVALID", message: "Credenciais inválidas" }),
        });
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ accessToken: "e2e-token", refreshToken: "e2e-refresh" }),
      });
    }
    const responses: Record<string, unknown> = {
      "/mobile/v1/accounts": [
        {
          id: "account-1",
          name: "Conta principal",
          branch: "0001",
          number: "12345-6",
          status: "ACTIVE",
        },
      ],
      "/mobile/v1/accounts/account-1/balance": {
        available: 1250.5,
        blocked: 0,
        currency: "BRL",
        asOf: "2026-08-06T12:00:00Z",
      },
      "/mobile/v1/accounts/account-1/movements/today": [],
    };
    return route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(responses[url.pathname] ?? []),
    });
  });
}

test("faz login e navega do dashboard para Pix", async ({ page }) => {
  await mockBff(page);
  await page.goto("/");
  await page.getByLabel("CPF ou CNPJ").fill("52998224725");
  await page.getByLabel("Senha").fill("Senha123");
  await page.getByRole("button", { name: "Entrar" }).dispatchEvent("click");

  await expect(page.getByRole("heading", { name: "Minha conta" })).toBeVisible();
  await expect(page.getByText("R$ 1250.50")).toBeVisible();
  await page.getByRole("button", { name: "Pix" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Pix" }).last()).toBeVisible();
});

test("exibe erro seguro quando o login é recusado", async ({ page }) => {
  await mockBff(page, 401);
  await page.goto("/");
  await page.getByLabel("CPF ou CNPJ").fill("00000000000");
  await page.getByLabel("Senha").fill("senha-incorreta");
  await page.getByRole("button", { name: "Entrar" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Não foi possível autenticar");
  await expect(page.getByText("e2e-token")).toHaveCount(0);
});
