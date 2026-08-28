import fs from "node:fs";
import { expect, test, type Page } from "@playwright/test";

test.afterEach(async ({ page }, testInfo) => {
  if (process.env.CAPTURE_WEB_FLOW_SCREENSHOTS !== "1") return;

  const directory = process.env.WEB_FLOW_SCREENSHOT_DIR ?? "artifacts/web-flows";
  fs.mkdirSync(directory, { recursive: true });
  await page.waitForTimeout(100);
  const title = testInfo.title.replace(/[^a-zA-Z0-9._-]+/g, "-").replace(/^-|-$/g, "");
  await page.bringToFront();
  await page.emulateMedia({ media: "screen" });
  await page.evaluate(() => window.scrollTo(0, 0));
  await page.locator("#root").screenshot({
    path: `${directory}/${testInfo.testId}-${title}.png`,
  });
});

async function mockBff(
  page: Page,
  loginStatus = 200,
  options: {
    mfa?: boolean;
    mfaStatus?: number;
    expired?: boolean;
    passwordStatus?: number;
    multipleAccounts?: boolean;
    statementEmpty?: boolean;
  } = {},
) {
  await page.route("**/mobile/v1/**", async (route) => {
    const url = new URL(route.request().url());
    if (url.pathname.endsWith("/session/mfa")) {
      if (options.mfaStatus && options.mfaStatus !== 200)
        return route.fulfill({
          status: options.mfaStatus,
          contentType: "application/json",
          body: JSON.stringify({ code: "MFA_INVALID", message: "Código inválido" }),
        });
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ accessToken: "mfa-token", refreshToken: "mfa-refresh" }),
      });
    }
    if (url.pathname.endsWith("/session") && route.request().method() === "DELETE") {
      return route.fulfill({ status: 204, body: "" });
    }
    if (url.pathname.endsWith("/session")) {
      if (loginStatus !== 200)
        return route.fulfill({
          status: loginStatus,
          contentType: "application/json",
          body: JSON.stringify({ code: "AUTH_INVALID", message: "Credenciais inválidas" }),
        });
      if (options.mfa)
        return route.fulfill({
          status: 200,
          contentType: "application/json",
          body: JSON.stringify({ mfaRequired: true, challengeId: "challenge-1" }),
        });
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ accessToken: "e2e-token", refreshToken: "e2e-refresh" }),
      });
    }
    if (url.pathname.endsWith("/onboardings")) {
      return route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({ onboardingId: "onboarding-1", status: "PENDING" }),
      });
    }
    if (url.pathname.endsWith("/password")) {
      return route.fulfill({
        status: options.passwordStatus ?? 204,
        contentType: "application/json",
        body:
          options.passwordStatus && options.passwordStatus !== 204
            ? JSON.stringify({ code: "PASSWORD_INVALID", message: "Senha inválida" })
            : "",
      });
    }
    if (options.expired && url.pathname.endsWith("/accounts")) {
      return route.fulfill({
        status: 401,
        contentType: "application/json",
        body: JSON.stringify({ code: "SESSION_EXPIRED", message: "Sessão expirada" }),
      });
    }
    if (url.pathname.endsWith("/pix/payments/bank-details")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "pix-bank-1",
          status: "PENDING",
          amount: 10,
          createdAt: "2026-08-06T12:00:00Z",
        }),
      });
    }
    if (url.pathname.includes("/pix/payments/") || url.pathname.endsWith("/pix/payments/key")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "pix-payment-1",
          status: "COMPLETED",
          amount: 10,
          createdAt: "2026-08-06T12:00:00Z",
        }),
      });
    }
    if (url.pathname.endsWith("/pix/charges/immediate")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "charge-1",
          type: "IMMEDIATE",
          brCode: "000201",
          amount: 10,
          status: "ACTIVE",
        }),
      });
    }
    if (url.pathname.endsWith("/pix/keys") && route.request().method() === "GET") {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([{ key: "chave@example.com", type: "EMAIL", status: "ACTIVE" }]),
      });
    }
    if (url.pathname.endsWith("/pix/keys") && route.request().method() === "POST") {
      return route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({ key: "nova@example.com", type: "EMAIL", status: "PENDING" }),
      });
    }
    if (url.pathname.endsWith("/open-finance/institutions")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([{ id: "bank-1", name: "Banco Parceiro", status: "AVAILABLE" }]),
      });
    }
    if (url.pathname.endsWith("/open-finance/consents") && route.request().method() === "GET") {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          {
            id: "consent-1",
            institutionId: "bank-1",
            institutionName: "Banco Parceiro",
            scopes: ["ACCOUNTS", "TRANSACTIONS"],
            status: "AUTHORIZED",
            expiresAt: "2026-12-31",
          },
        ]),
      });
    }
    if (url.pathname.endsWith("/open-finance/consents") && route.request().method() === "POST") {
      return route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({
          id: "consent-1",
          institutionId: "bank-1",
          institutionName: "Banco Parceiro",
          scopes: ["ACCOUNTS"],
          status: "AUTHORIZED",
          expiresAt: "2026-12-31",
        }),
      });
    }
    if (
      url.pathname.endsWith("/open-finance/payments/immediate") ||
      url.pathname.endsWith("/open-finance/payments/scheduled") ||
      url.pathname.endsWith("/open-finance/payments/automatic")
    ) {
      return route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({
          id: "of-payment-1",
          amount: 50,
          status: "AUTHORIZED",
          createdAt: "2026-08-06T12:00:00Z",
        }),
      });
    }
    if (url.pathname.endsWith("/open-finance/sweeping/transfers")) {
      return route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({
          id: "sweep-1",
          amount: 50,
          status: "COMPLETED",
          createdAt: "2026-08-06T12:00:00Z",
        }),
      });
    }
    if (
      url.pathname.endsWith("/open-finance/bricks/sessions") ||
      url.pathname.endsWith("/open-finance/redirect-flows")
    ) {
      return route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({
          id: "flow-1",
          status: "AWAITING_CALLBACK",
          redirectUrl: "https://bank.example.com/authorize",
        }),
      });
    }
    if (url.pathname.endsWith("/notifications")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          {
            id: "notification-1",
            title: "Pagamento aprovado",
            body: "Seu pagamento foi processado.",
            category: "PAYMENT",
            createdAt: "2026-08-06T12:00:00Z",
          },
        ]),
      });
    }
    if (url.pathname.endsWith("/support/faqs")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          {
            id: "faq-1",
            question: "Como altero minha senha?",
            answer: "Pelo perfil.",
            category: "ACCOUNT",
          },
        ]),
      });
    }
    if (url.pathname.endsWith("/support/tickets") && route.request().method() === "GET") {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([]),
      });
    }
    if (url.pathname.endsWith("/support/tickets") && route.request().method() === "POST") {
      return route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({
          id: "ticket-1",
          subject: "Ajuda",
          status: "OPEN",
          updatedAt: "2026-08-06T12:00:00Z",
        }),
      });
    }
    if (url.pathname.endsWith("/operations/services")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          { service: "Pix", status: "OPERATIONAL", updatedAt: "2026-08-06T12:00:00Z" },
        ]),
      });
    }
    if (
      url.pathname.endsWith("/pix/charges/duedate") ||
      url.pathname.endsWith("/pix/charges/static")
    ) {
      return route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({
          id: "charge-2",
          type: "STATIC",
          brCode: "000201",
          amount: 10,
          status: "ACTIVE",
        }),
      });
    }
    if (url.pathname.endsWith("/bills/lookup")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "bill-1",
          digitableLine: "00190500954014481606906809350314337370000000100",
          amount: 100,
          dueDate: "2026-12-31",
          status: "AUTHORIZED",
          beneficiary: "Concessionária",
        }),
      });
    }
    if (url.pathname.endsWith("/authorization") || url.pathname.endsWith("/payment")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ id: "payment-1", status: "COMPLETED", amount: 100 }),
      });
    }
    if (url.pathname.endsWith("/topups")) {
      return route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify({ id: "topup-1", status: "COMPLETED" }),
      });
    }
    if (url.pathname.endsWith("/vehicles/debts")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          {
            id: "debt-1",
            type: "IPVA",
            description: "IPVA 2026",
            amount: 500,
            dueDate: "2026-12-31",
            status: "OPEN",
          },
        ]),
      });
    }
    if (url.pathname.endsWith("/cards") && route.request().method() === "GET") {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          {
            id: "card-1",
            type: "VIRTUAL",
            lastFour: "1234",
            brand: "Visa",
            status: "ACTIVE",
            limit: 1000,
            availableLimit: 800,
          },
        ]),
      });
    }
    if (url.pathname.endsWith("/cards/card-1/statement")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ dueDate: "2026-12-10", total: 200, minimum: 20, status: "OPEN" }),
      });
    }
    if (
      url.pathname.includes("/cards/card-1/activation") ||
      url.pathname.includes("/cards/card-1/block") ||
      url.pathname.includes("/cards/card-1/unblock")
    ) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "card-1",
          type: "VIRTUAL",
          lastFour: "1234",
          brand: "Visa",
          status: "ACTIVE",
          limit: 1000,
          availableLimit: 800,
        }),
      });
    }
    if (url.pathname.endsWith("/judicial-blocks")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          {
            id: "block-1",
            amount: 125,
            status: "ACTIVE",
            reason: "Ordem judicial",
            createdAt: "2026-08-01T12:00:00Z",
          },
        ]),
      });
    }
    if (url.pathname.endsWith("/close") || url.pathname.endsWith("/transfers/ted")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ id: "operation-1", status: "COMPLETED" }),
      });
    }
    if (url.pathname.endsWith("/reports/income/2025")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ downloadUrl: "https://files.example.com/income-2025.pdf" }),
      });
    }
    if (url.pathname.endsWith("/credit/simulations")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          amount: 1000,
          installments: 12,
          installmentAmount: 95,
          totalAmount: 1140,
          rate: 0.02,
        }),
      });
    }
    if (url.pathname === "/mobile/v1/escrow") {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          {
            id: "escrow-1",
            status: "ACTIVE",
            balance: 2500,
            currency: "BRL",
            parties: [],
            updatedAt: "2026-08-06T12:00:00Z",
          },
        ]),
      });
    }
    if (url.pathname.endsWith("/escrow/escrow-1/events")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          {
            id: "event-1",
            type: "FUNDING",
            status: "COMPLETED",
            createdAt: "2026-08-06T12:00:00Z",
          },
        ]),
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
        ...(options.multipleAccounts
          ? [
              {
                id: "account-2",
                name: "Conta investimento",
                branch: "0001",
                number: "98765-4",
                status: "ACTIVE",
              },
            ]
          : []),
      ],
      "/mobile/v1/accounts/account-1/balance": {
        available: 1250.5,
        blocked: 0,
        currency: "BRL",
        asOf: "2026-08-06T12:00:00Z",
      },
      "/mobile/v1/accounts/account-1/movements/today": [],
      "/mobile/v1/accounts/account-2/balance": {
        available: 300,
        blocked: 25,
        currency: "BRL",
        asOf: "2026-08-06T12:00:00Z",
      },
      "/mobile/v1/accounts/account-2/movements/today": [],
    };
    if (url.pathname.endsWith("/statement")) {
      return route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify(
          options.statementEmpty
            ? { items: [], page: 0, size: 20, totalPages: 0, totalItems: 0 }
            : {
                items: [
                  {
                    id: "movement-1",
                    description: "Pix recebido",
                    amount: 100,
                    direction: "CREDIT",
                    type: "PIX",
                    status: "COMPLETED",
                    occurredAt: "2026-08-06T12:00:00Z",
                  },
                ],
                page: 0,
                size: 20,
                totalPages: 1,
                totalItems: 1,
              },
        ),
      });
    }
    return route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify(responses[url.pathname] ?? []),
    });
  });
}

test("inicializa o app e resolve deep link público", async ({ page }) => {
  await mockBff(page);
  await page.goto("/login");
  await expect(page.getByRole("heading", { name: "Acesse sua conta" })).toBeVisible();
  await expect(page.getByLabel("CPF ou CNPJ")).toBeVisible();
});

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

test("conclui login com MFA", async ({ page }) => {
  await mockBff(page, 200, { mfa: true });
  await page.goto("/login");
  await page.getByLabel("CPF ou CNPJ").fill("52998224725");
  await page.getByLabel("Senha").fill("Senha123");
  await page.getByRole("button", { name: "Entrar" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Confirme sua identidade" })).toBeVisible();
  await page.getByLabel("Código de autenticação multifator").fill("123456");
  await page.getByRole("button", { name: "Confirmar código" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Minha conta" })).toBeVisible();
});

test("recusa MFA com mensagem segura", async ({ page }) => {
  await mockBff(page, 200, { mfa: true, mfaStatus: 401 });
  await page.goto("/login");
  await page.getByLabel("CPF ou CNPJ").fill("52998224725");
  await page.getByLabel("Senha").fill("Senha123");
  await page.getByRole("button", { name: "Entrar" }).dispatchEvent("click");
  await page.getByLabel("Código de autenticação multifator").fill("000000");
  await page.getByRole("button", { name: "Confirmar código" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Não foi possível autenticar");
});

test("trata MFA expirado", async ({ page }) => {
  await mockBff(page, 200, { mfa: true, mfaStatus: 410 });
  await page.goto("/login");
  await page.getByLabel("CPF ou CNPJ").fill("52998224725");
  await page.getByLabel("Senha").fill("Senha123");
  await page.getByRole("button", { name: "Entrar" }).dispatchEvent("click");
  await page.getByLabel("Código de autenticação multifator").fill("123456");
  await page.getByRole("button", { name: "Confirmar código" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Não foi possível autenticar");
});

test("solicita recuperação com mensagem neutra", async ({ page }) => {
  await mockBff(page);
  await page.goto("/login");
  await page.getByRole("button", { name: "Esqueci minha senha" }).dispatchEvent("click");
  await page.getByLabel("CPF ou CNPJ").fill("52998224725");
  await page.getByRole("button", { name: "Enviar instruções" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toHaveText(
    "Se os dados estiverem corretos, enviaremos as instruções de recuperação.",
  );
});

test("faz logout e retorna ao login", async ({ page }) => {
  await mockBff(page);
  await page.goto("/");
  await page.getByLabel("CPF ou CNPJ").fill("52998224725");
  await page.getByLabel("Senha").fill("Senha123");
  await page.getByRole("button", { name: "Entrar" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Minha conta" })).toBeVisible();
  await page.getByRole("button", { name: "Sair" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Acesse sua conta" })).toBeVisible();
});

test("encerra a sessão quando o BFF responde 401", async ({ page }) => {
  await mockBff(page, 200, { expired: true });
  await page.goto("/");
  await page.getByLabel("CPF ou CNPJ").fill("52998224725");
  await page.getByLabel("Senha").fill("Senha123");
  await page.getByRole("button", { name: "Entrar" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Acesse sua conta" })).toBeVisible();
});

async function openProfile(page: Page, passwordOptions: { passwordStatus?: number } = {}) {
  await mockBff(page, 200, passwordOptions);
  await page.goto("/");
  await page.getByLabel("CPF ou CNPJ").fill("52998224725");
  await page.getByLabel("Senha").fill("Senha123");
  await page.getByRole("button", { name: "Entrar" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Minha conta" })).toBeVisible();
  await page.getByRole("button", { name: "Dados cadastrais" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Dados cadastrais" })).toBeVisible();
}

test("altera a senha", async ({ page }) => {
  await openProfile(page);
  await page.getByLabel("Senha atual").fill("Senha123");
  await page.getByLabel("Nova senha", { exact: true }).fill("NovaSenha123");
  await page.getByLabel("Confirmar nova senha").fill("NovaSenha123");
  await page.getByRole("button", { name: "Alterar senha" }).dispatchEvent("click");
  await expect(page.getByText("Senha alterada com sucesso.")).toBeVisible();
});

test("recusa senha inválida com mensagem segura", async ({ page }) => {
  await openProfile(page, { passwordStatus: 422 });
  await page.getByLabel("Senha atual").fill("errada");
  await page.getByLabel("Nova senha", { exact: true }).fill("NovaSenha123");
  await page.getByLabel("Confirmar nova senha").fill("NovaSenha123");
  await page.getByRole("button", { name: "Alterar senha" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Não foi possível alterar a senha");
});

test("retorna ao login quando a troca de senha expira a sessão", async ({ page }) => {
  await openProfile(page, { passwordStatus: 401 });
  await page.getByLabel("Senha atual").fill("Senha123");
  await page.getByLabel("Nova senha", { exact: true }).fill("NovaSenha123");
  await page.getByLabel("Confirmar nova senha").fill("NovaSenha123");
  await page.getByRole("button", { name: "Alterar senha" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Acesse sua conta" })).toBeVisible();
});

async function openOnboarding(page: Page) {
  await mockBff(page);
  await page.goto("/");
  await page.getByRole("button", { name: "Abrir uma conta" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Abra sua conta" })).toBeVisible();
}

async function fillOnboarding(page: Page, personType: "PF" | "PJ") {
  if (personType === "PJ") {
    await page.getByRole("button", { name: "Pessoa jurídica" }).dispatchEvent("click");
  }
  await page
    .getByLabel(personType === "PF" ? "CPF" : "CNPJ", { exact: true })
    .fill(personType === "PF" ? "52998224725" : "11222333000181");
  await page.getByLabel("E-mail").fill("cliente@example.com");
  await page.getByLabel("Telefone").fill("11987654321");
  await page.getByLabel("CEP").fill("01311000");
  await page.getByLabel("Rua").fill("Avenida Paulista");
  await page.getByLabel("Número").fill("1000");
  await page.getByLabel("Cidade").fill("São Paulo");
  await page.getByLabel("Estado").fill("SP");
  if (personType === "PJ") {
    await page.getByLabel("Nome do representante").fill("Maria da Silva");
    await page.getByLabel("CPF do representante").fill("52998224725");
  }
  await page.getByRole("button", { name: "Aceitar termos e privacidade" }).dispatchEvent("click");
}

test("inicia cadastro PF com endereço e consentimentos", async ({ page }) => {
  await openOnboarding(page);
  await fillOnboarding(page, "PF");
  await page.getByRole("button", { name: "Continuar" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Cadastro iniciado");
});

test("inicia cadastro PJ com representante", async ({ page }) => {
  await openOnboarding(page);
  await fillOnboarding(page, "PJ");
  await page.getByRole("button", { name: "Continuar" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Cadastro iniciado");
});

test("bloqueia cadastro sem consentimento", async ({ page }) => {
  await openOnboarding(page);
  await fillOnboarding(page, "PF");
  await page.getByRole("button", { name: "Consentimentos aceitos" }).dispatchEvent("click");
  await page.getByRole("button", { name: "Continuar" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Confira documento");
});

async function openDashboard(page: Page, options: Parameters<typeof mockBff>[2] = {}) {
  await mockBff(page, 200, options);
  await page.goto("/");
  await page.getByLabel("CPF ou CNPJ").fill("52998224725");
  await page.getByLabel("Senha").fill("Senha123");
  await page.getByRole("button", { name: "Entrar" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Minha conta" })).toBeVisible();
}

test("exibe saldo bloqueado e alterna a conta ativa", async ({ page }) => {
  await openDashboard(page, { multipleAccounts: true });
  await expect(page.getByText("Saldo bloqueado: R$ 0.00")).toBeVisible();
  await page.getByRole("button", { name: "Usar Conta investimento" }).dispatchEvent("click");
  await expect(page.getByText("R$ 300.00")).toBeVisible();
  await expect(page.getByText("Saldo bloqueado: R$ 25.00")).toBeVisible();
});

test("consulta e filtra o extrato", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Ver extrato" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Extrato" }).last()).toBeVisible();
  await expect(page.getByText("Pix recebido")).toBeVisible();
  await page.getByPlaceholder("Status (PENDING, COMPLETED...)").fill("completed");
  await expect(page.getByText("Pix recebido")).toBeVisible();
});

test("exibe extrato vazio", async ({ page }) => {
  await openDashboard(page, { statementEmpty: true });
  await page.getByRole("button", { name: "Ver extrato" }).dispatchEvent("click");
  await expect(page.getByText("Nenhuma movimentação encontrada.")).toBeVisible();
});

test("paga Pix por dados bancários e cria cobrança", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Pix" }).dispatchEvent("click");
  await page.getByLabel("Valor", { exact: true }).fill("10");
  await page.getByLabel("Código do banco").fill("001");
  await page.getByLabel("Agência").fill("0001");
  await page.getByLabel("Conta").fill("12345-6");
  await page.getByLabel("Documento do favorecido Pix").fill("52998224725");
  await page.getByRole("button", { name: "Pagar por dados bancários" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Pix pending");
  await page.getByRole("button", { name: "Criar cobrança imediata" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Cobrança criada");
});

test("gerencia chave Pix e cobranças", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Pix" }).dispatchEvent("click");
  await page
    .getByRole("button", { name: "Gerenciar chaves e operações Pix" })
    .dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Gestão Pix" }).last()).toBeVisible();
  await expect(page.getByText("chave@example.com")).toBeVisible();
  await page.getByLabel("Nova chave Pix").fill("nova@example.com");
  await page.getByRole("button", { name: "Criar chave Pix" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Chave Pix criada");
  await page.getByLabel("Valor da cobrança").fill("10");
  await page.getByLabel("Vencimento da cobrança").fill("2026-12-31");
  await page.getByRole("button", { name: "Criar cobrança com vencimento" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Cobrança criada");
});

test("consulta e paga boleto, recarga e débito veicular", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Boletos e recargas" }).dispatchEvent("click");
  await page.getByLabel("Linha digitável").fill("00190500954014481606906809350314337370000000100");
  await page.getByRole("button", { name: "Consultar boleto" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Concessionária");
  await page.getByRole("button", { name: "Autorizar e pagar" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Boleto completed");
  await page.getByLabel("Operadora").fill("operator-1");
  await page.getByLabel("Produto da recarga").fill("product-1");
  await page.getByLabel("Telefone da recarga").fill("11987654321");
  await page.getByLabel("Valor da recarga").fill("20");
  await page.getByRole("button", { name: "Realizar recarga" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Recarga completed");
  await page.getByLabel("Documento veicular").fill("52998224725");
  await page.getByLabel("RENAVAM").fill("12345678901");
  await page.getByRole("button", { name: "Consultar débitos" }).dispatchEvent("click");
  await expect(page.getByText("IPVA 2026")).toBeVisible();
  await page.getByRole("button", { name: "Pagar débitos selecionados" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Débitos completed");
});

test("consulta cartão, fatura, simula crédito e Escrow", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Cartões, crédito e Escrow" }).dispatchEvent("click");
  await expect(page.getByText("Visa •••• 1234")).toBeVisible();
  await expect(page.getByText("Fatura: R$ 200.00")).toBeVisible();
  await page.getByLabel("Valor do crédito").fill("1000");
  await page.getByRole("button", { name: "Simular crédito" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Simulação: 12x");
  await expect(page.getByText("escrow-1 · ACTIVE · R$ 2500.00")).toBeVisible();
  await expect(page.getByText("Evento FUNDING · COMPLETED")).toBeVisible();
});

test("autoriza instituição e inicia pagamento Open Finance", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Open Finance" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Open Finance" }).last()).toBeVisible();
  await page.getByRole("button", { name: "Banco Parceiro" }).dispatchEvent("click");
  await expect(page.getByText("Banco Parceiro · AUTHORIZED")).toBeVisible();
  await page.getByLabel("Valor Open Finance").fill("50");
  await page.getByLabel("Favorecido Open Finance").fill("Favorecido");
  await page.getByRole("button", { name: "Pagar agora" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Pagamento authorized");
});

test("paga Pix por QR Code válido", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Pix" }).dispatchEvent("click");
  await page.getByLabel("Valor", { exact: true }).fill("10");
  await page.getByLabel("Código Pix").fill("000201010212", { force: true });
  await page.getByRole("button", { name: "Validar e pagar QR Code" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Pix completed");
});

test("opera serviços adicionais da conta", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Mais serviços da conta" }).dispatchEvent("click");
  await expect(page.getByText("R$ 125.00 · ACTIVE")).toBeVisible();
  await page.getByLabel("Valor da TED").fill("50");
  await page.getByLabel("Documento do favorecido").fill("52998224725");
  await page.getByRole("button", { name: "Enviar TED" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("TED enviada");
  await page.getByLabel("Motivo do encerramento").fill("Não utilizo mais a conta");
  await page.getByRole("button", { name: "Solicitar encerramento" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("encerramento registrada");
});

test("inicia pagamento automático e sweeping Open Finance", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Open Finance" }).dispatchEvent("click");
  await page.getByLabel("Valor Open Finance").fill("50");
  await page.getByLabel("Favorecido Open Finance").fill("Favorecido");
  await page.getByRole("button", { name: "Autorizar pagamento automático" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Pagamento authorized");
  await page.getByLabel("Conta de origem").fill("account-1");
  await page.getByLabel("Conta de destino").fill("account-2");
  await page.getByRole("button", { name: "Criar sweeping" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Sweeping completed");
});

test("ativa e bloqueia cartão", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Cartões, crédito e Escrow" }).dispatchEvent("click");
  await page.getByLabel("Últimos quatro dígitos").fill("1234");
  await page.getByRole("button", { name: "Ativar cartão" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Cartão ativado");
  await page.getByRole("button", { name: "Bloquear cartão" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Status do cartão atualizado");
});

test("agenda pagamento e inicia Brick Open Finance", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Open Finance" }).dispatchEvent("click");
  await page.getByLabel("Valor Open Finance").fill("50");
  await page.getByLabel("Favorecido Open Finance").fill("Favorecido");
  await page.getByLabel("Data do agendamento").fill("2026-12-31");
  await page.getByRole("button", { name: "Agendar pagamento" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Pagamento authorized");
  await page.getByRole("button", { name: "Iniciar Brick Bank" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Brick BANK");
});

test("paga Pix por QR dinâmico", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Pix" }).dispatchEvent("click");
  await page.getByLabel("Valor", { exact: true }).fill("15");
  await page.getByLabel("Código Pix").fill("00020101021263040000", { force: true });
  await page.getByRole("button", { name: "Validar e pagar QR Code" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Pix completed");
});

test("recusa QR Pix inválido com mensagem segura", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Pix" }).dispatchEvent("click");
  await page.getByLabel("Valor", { exact: true }).fill("15");
  await page.getByLabel("Código Pix").fill("codigo-invalido", { force: true });
  await page.getByRole("button", { name: "Validar e pagar QR Code" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toHaveText("QR Code Pix inválido");
});

test("revoga consentimento Open Finance", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Open Finance" }).dispatchEvent("click");
  await page.getByRole("button", { name: "Revogar" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toHaveText("Consentimento revogado.");
});

test("atualiza o perfil cadastral", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Dados cadastrais" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Dados cadastrais" }).last()).toBeVisible();
  await page.getByPlaceholder("E-mail").fill("novo@example.com");
  await page.getByRole("button", { name: "Salvar alterações" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Dados cadastrais atualizados");
});

test("consulta notificações, FAQ, status e abre ticket", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Suporte e notificações" }).dispatchEvent("click");
  await expect(page.getByRole("heading", { name: "Suporte e operação" }).last()).toBeVisible();
  await expect(page.getByRole("button", { name: "Pagamento aprovado" })).toBeVisible();
  await expect(page.getByText("Pix: OPERATIONAL")).toBeVisible();
  await expect(page.getByText("FAQ: 1 artigos disponíveis")).toBeVisible();
  await page.getByPlaceholder("Assunto do chamado").fill("Ajuda", { force: true });
  await page
    .getByPlaceholder("Descreva o problema")
    .fill("Preciso de ajuda com a conta.", { force: true });
  await page.getByRole("button", { name: "Abrir chamado" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toHaveText("Solicitação aberta.");
});

test("paga Pix por chave", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Pix" }).dispatchEvent("click");
  await page.getByLabel("Chave Pix").fill("favorecido@example.com");
  await page.getByLabel("Valor", { exact: true }).fill("10");
  await page.getByRole("button", { name: "Pagar por chave" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Pix completed");
});

test("exclui chave Pix e solicita devolução", async ({ page }) => {
  await openDashboard(page);
  await page.getByRole("button", { name: "Pix" }).dispatchEvent("click");
  await page
    .getByRole("button", { name: "Gerenciar chaves e operações Pix" })
    .dispatchEvent("click");
  await expect(page.getByText("chave@example.com")).toBeVisible();
  await page.getByRole("button", { name: "Excluir" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toHaveText("Chave Pix excluída.");
  await page.getByLabel("ID do pagamento Pix").fill("pix-payment-1");
  await page.getByRole("button", { name: "Solicitar devolução" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Devolução");
});

test("cria consentimento e abre redirecionamento Open Finance seguro", async ({ page }) => {
  await page.addInitScript(() => {
    window.open = (url) => {
      (window as Window & { __openedUrl?: string }).__openedUrl = String(url);
      return null;
    };
  });
  await openDashboard(page);
  await page.getByRole("button", { name: "Open Finance" }).dispatchEvent("click");
  await page.getByRole("button", { name: "Banco Parceiro" }).dispatchEvent("click");
  await expect(page.getByRole("alert")).toContainText("Consentimento criado");
  await page.getByRole("button", { name: "Refazer vínculo" }).dispatchEvent("click");
  await expect
    .poll(() => page.evaluate(() => (window as Window & { __openedUrl?: string }).__openedUrl))
    .toBe("https://bank.example.com/authorize");
});

test("abre informe de rendimentos somente por HTTPS", async ({ page }) => {
  await page.addInitScript(() => {
    window.open = (url) => {
      (window as Window & { __openedUrl?: string }).__openedUrl = String(url);
      return null;
    };
  });
  await openDashboard(page);
  await page.getByRole("button", { name: "Mais serviços da conta" }).dispatchEvent("click");
  await page.getByRole("button", { name: "Abrir informe de 2025" }).dispatchEvent("click");
  await expect
    .poll(() => page.evaluate(() => (window as Window & { __openedUrl?: string }).__openedUrl))
    .toBe("https://files.example.com/income-2025.pdf");
});
