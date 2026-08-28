import { by, device, element, expect } from "detox";

async function openLogin() {
  await device.launchApp({ newInstance: true, delete: true });
  await expect(element(by.text("Acesse sua conta"))).toBeVisible();
}

async function login(password = process.env.E2E_USER_PASSWORD!) {
  await element(by.id("login-identifier")).typeText(process.env.E2E_USER_IDENTIFIER!);
  await element(by.id("login-password")).typeText(password);
  await element(by.id("login-submit")).tap();
}

describe("iOS — autenticação", () => {
  beforeAll(() => {
    if (!process.env.E2E_USER_IDENTIFIER || !process.env.E2E_USER_PASSWORD) {
      throw new Error("Configure E2E_USER_IDENTIFIER e E2E_USER_PASSWORD.");
    }
  });

  it("aceita credencial válida e abre a conta", async () => {
    await openLogin();
    await login();
    if (process.env.E2E_MFA_CODE) {
      await expect(element(by.text("Confirme sua identidade"))).toBeVisible();
      await element(by.id("login-mfa-code")).typeText(process.env.E2E_MFA_CODE);
      await element(by.id("login-submit")).tap();
    }
    await expect(element(by.text("Minha conta"))).toBeVisible();
  });

  it("recusa senha inválida sem expor token", async () => {
    await openLogin();
    await login(process.env.E2E_INVALID_PASSWORD ?? "senha-e2e-invalida-000");
    await expect(
      element(by.text("Não foi possível autenticar. Verifique os dados e tente novamente.")),
    ).toBeVisible();
    await expect(element(by.text("Acesse sua conta"))).toBeVisible();
  });

  it("faz logout e remove a sessão local", async () => {
    await openLogin();
    await login();
    if (process.env.E2E_MFA_CODE) {
      await expect(element(by.text("Confirme sua identidade"))).toBeVisible();
      await element(by.id("login-mfa-code")).typeText(process.env.E2E_MFA_CODE);
      await element(by.id("login-submit")).tap();
    }
    await expect(element(by.text("Minha conta"))).toBeVisible();
    await element(by.text("Sair")).tap();
    await expect(element(by.text("Acesse sua conta"))).toBeVisible();
  });
});
