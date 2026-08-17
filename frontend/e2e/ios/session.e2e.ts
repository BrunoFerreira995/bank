import { by, device, element, expect } from "detox";

const required = ["E2E_USER_IDENTIFIER", "E2E_USER_PASSWORD"];
const missing = required.filter((name) => !process.env[name]);

describe("iOS — sessão e entrada no app", () => {
  beforeAll(async () => {
    if (missing.length > 0) {
      throw new Error(
        `Defina ${missing.join(", ")} para executar os E2E iOS contra o BFF de staging/sandbox.`,
      );
    }
    await device.launchApp({ newInstance: true, delete: true });
  });

  afterEach(async () => {
    await device.takeScreenshot('ios-session');
  });

  it("inicia sem sessão e permite login válido", async () => {
    await expect(element(by.text("Acesse sua conta"))).toBeVisible();
    await element(by.label("CPF ou CNPJ")).typeText(process.env.E2E_USER_IDENTIFIER!);
    await element(by.label("Senha")).typeText(process.env.E2E_USER_PASSWORD!);
    await element(by.text("Entrar")).tap();

    if (process.env.E2E_MFA_CODE) {
      await expect(element(by.text("Confirme sua identidade"))).toBeVisible();
      await element(by.label("Código de autenticação multifator")).typeText(
        process.env.E2E_MFA_CODE,
      );
      await element(by.text("Confirmar código")).tap();
    }

    await expect(element(by.text("Saldo disponível"))).toBeVisible();
  });

  it("encerra a sessão e retorna ao login", async () => {
    await device.launchApp({ newInstance: true });
    await expect(element(by.text("Saldo disponível"))).toBeVisible();
    await element(by.text("Sair")).tap();
    await expect(element(by.text("Acesse sua conta"))).toBeVisible();
  });
});
