import { by, device, element, expect } from "detox";

const pixData = {
  key: process.env.E2E_PIX_KEY,
  amount: process.env.E2E_PIX_AMOUNT ?? "10.00",
  bankCode: process.env.E2E_PIX_BANK_CODE,
  branch: process.env.E2E_PIX_BRANCH,
  account: process.env.E2E_PIX_ACCOUNT,
  document: process.env.E2E_PIX_DOCUMENT,
  newKey: process.env.E2E_PIX_NEW_KEY,
  qrCode: process.env.E2E_PIX_QR_CODE,
};

function requireUser() {
  if (!process.env.E2E_USER_IDENTIFIER || !process.env.E2E_USER_PASSWORD) {
    throw new Error("Defina E2E_USER_IDENTIFIER e E2E_USER_PASSWORD no secret store da CI.");
  }
}

function requirePaymentData() {
  const names = Object.entries({
    E2E_PIX_KEY: pixData.key,
    E2E_PIX_BANK_CODE: pixData.bankCode,
    E2E_PIX_BRANCH: pixData.branch,
    E2E_PIX_ACCOUNT: pixData.account,
    E2E_PIX_DOCUMENT: pixData.document,
  })
    .filter(([, value]) => !value)
    .map(([name]) => name);
  if (names.length > 0) throw new Error(`Defina a massa Pix: ${names.join(", ")}.`);
}

async function signInAndOpenPix() {
  requireUser();
  await element(by.label("CPF ou CNPJ")).typeText(process.env.E2E_USER_IDENTIFIER!);
  await element(by.label("Senha")).typeText(process.env.E2E_USER_PASSWORD!);
  await element(by.text("Entrar")).tap();
  if (process.env.E2E_MFA_CODE) {
    await expect(element(by.text("Confirme sua identidade"))).toBeVisible();
    await element(by.label("Código de autenticação multifator")).typeText(process.env.E2E_MFA_CODE);
    await element(by.text("Confirmar código")).tap();
  }
  await expect(element(by.text("Minha conta"))).toBeVisible();
  await element(by.text("Pix")).tap();
  await expect(element(by.text("Pix"))).toBeVisible();
}

describe("iOS — Pix", () => {
  beforeEach(async () => {
    await device.launchApp({ newInstance: true, delete: true });
    await signInAndOpenPix();
  });

  it("valida chave e valor antes de enviar um Pix", async () => {
    await element(by.text("Pagar por chave")).tap();
    await expect(element(by.text("Informe um valor válido"))).toBeVisible();
    await element(by.label("Valor")).typeText("10");
    await element(by.text("Pagar por chave")).tap();
    await expect(element(by.text("Informe a chave Pix"))).toBeVisible();
  });

  it("recusa QR Code Pix inválido", async () => {
    await element(by.label("Valor")).typeText("10");
    await element(by.label("Código Pix")).typeText("codigo-invalido");
    await element(by.text("Validar e pagar QR Code")).tap();
    await expect(element(by.text("QR Code Pix inválido"))).toBeVisible();
  });

  it("paga QR Code Pix válido com a massa isolada", async () => {
    if (!pixData.qrCode) throw new Error("Defina E2E_PIX_QR_CODE no secret store da CI.");
    await element(by.label("Valor")).typeText(pixData.amount);
    await element(by.label("Código Pix")).typeText(pixData.qrCode);
    await element(by.text("Validar e pagar QR Code")).tap();
    await expect(element(by.text(/^Pix /))).toBeVisible();
  });

  it("paga Pix por chave com a massa isolada", async () => {
    requirePaymentData();
    await element(by.label("Chave Pix")).typeText(pixData.key!);
    await element(by.label("Valor")).typeText(pixData.amount);
    await element(by.text("Pagar por chave")).tap();
    await expect(element(by.text(/^Pix /))).toBeVisible();
  });

  it("paga Pix por dados bancários", async () => {
    requirePaymentData();
    await element(by.label("Valor")).typeText(pixData.amount);
    await element(by.label("Código do banco")).typeText(pixData.bankCode!);
    await element(by.label("Agência")).typeText(pixData.branch!);
    await element(by.label("Conta")).typeText(pixData.account!);
    await element(by.label("Documento do favorecido Pix")).typeText(pixData.document!);
    await element(by.text("Pagar por dados bancários")).tap();
    await expect(element(by.text(/^Pix /))).toBeVisible();
  });

  it("cria cobrança Pix imediata", async () => {
    await element(by.label("Valor")).typeText(pixData.amount);
    await element(by.text("Criar cobrança imediata")).tap();
    await expect(element(by.text(/^Cobrança criada:/))).toBeVisible();
  });

  it("abre a gestão de chaves e valida chave vazia", async () => {
    await element(by.text("Gerenciar chaves e operações Pix")).tap();
    await expect(element(by.text("Gestão Pix"))).toBeVisible();
    await element(by.text("Criar chave Pix")).tap();
    await expect(element(by.text("Informe a chave Pix."))).toBeVisible();
  });

  it("cria chave Pix na gestão com massa isolada", async () => {
    if (!pixData.newKey) throw new Error("Defina E2E_PIX_NEW_KEY no secret store da CI.");
    await element(by.text("Gerenciar chaves e operações Pix")).tap();
    await expect(element(by.text("Gestão Pix"))).toBeVisible();
    await element(by.label("Nova chave Pix")).typeText(pixData.newKey);
    await element(by.text("Criar chave Pix")).tap();
    await expect(element(by.text("Chave Pix criada."))).toBeVisible();
  });
});
