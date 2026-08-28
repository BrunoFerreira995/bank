import { by, device, element, expect } from "detox";

const identifier = process.env.E2E_USER_IDENTIFIER;
const password = process.env.E2E_USER_PASSWORD;

function requireAccountUser() {
  if (!identifier || !password) {
    throw new Error("Defina E2E_USER_IDENTIFIER e E2E_USER_PASSWORD no secret store da CI.");
  }
}

async function signIn() {
  requireAccountUser();
  await expect(element(by.text("Acesse sua conta"))).toBeVisible();
  await element(by.id("login-identifier")).typeText(identifier!);
  await element(by.id("login-password")).typeText(password!);
  await element(by.id("login-submit")).tap();

  if (process.env.E2E_MFA_CODE) {
    await expect(element(by.text("Confirme sua identidade"))).toBeVisible();
    await element(by.id("login-mfa-code")).typeText(process.env.E2E_MFA_CODE);
    await element(by.id("login-submit")).tap();
  }

  await expect(element(by.text("Minha conta"))).toBeVisible();
}

describe("iOS — conta, saldo e movimentações", () => {
  beforeEach(async () => {
    await device.launchApp({ newInstance: true, delete: true });
    await signIn();
  });

  it("exibe saldo disponível, bloqueado e movimentações do dia", async () => {
    await expect(element(by.text("Saldo disponível"))).toBeVisible();
    await expect(element(by.text(/^Saldo bloqueado: R\$ /))).toBeVisible();
    await expect(element(by.text("Movimentações de hoje"))).toBeVisible();
    await expect(element(by.id("account-dashboard"))).toBeVisible();
  });

  it("alterna a conta ativa e atualiza o saldo", async () => {
    await expect(element(by.text(/^Usar /))).toBeVisible();
    await element(by.text(/^Usar /))
      .atIndex(0)
      .tap();
    await expect(element(by.text("Saldo disponível"))).toBeVisible();
    await expect(element(by.text(/^Saldo bloqueado: R\$ /))).toBeVisible();
  });

  it("consulta e filtra o extrato", async () => {
    await element(by.text("Ver extrato")).tap();
    await expect(element(by.text("Extrato"))).toBeVisible();
    await expect(element(by.text("Página 1"))).toBeVisible();
    await element(by.label("Filtro de status do extrato")).typeText("COMPLETED");
    await expect(element(by.text("Extrato"))).toBeVisible();
    await expect(element(by.text("Página 1"))).toBeVisible();
  });

  it("permite atualizar a conta e valida troca de senha localmente", async () => {
    await element(by.id("account-dashboard")).swipe("down", "fast");
    await expect(element(by.text("Minha conta"))).toBeVisible();
    await element(by.text("Dados cadastrais")).tap();
    await expect(element(by.text("Dados cadastrais"))).toBeVisible();
    await element(by.label("Senha atual")).typeText("senha-atual-e2e");
    await element(by.label("Nova senha")).typeText("nova-senha-e2e");
    await element(by.label("Confirmar nova senha")).typeText("outra-senha-e2e");
    await element(by.text("Alterar senha")).tap();
    await expect(element(by.text("Confira a nova senha e sua confirmação."))).toBeVisible();
  });
});
