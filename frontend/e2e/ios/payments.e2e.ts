import { by, device, element, expect } from "detox";

const paymentData = {
  billCode: process.env.E2E_BILL_CODE,
  operatorId: process.env.E2E_TOPUP_OPERATOR_ID,
  productId: process.env.E2E_TOPUP_PRODUCT_ID,
  phone: process.env.E2E_TOPUP_PHONE,
  topUpAmount: process.env.E2E_TOPUP_AMOUNT ?? "20.00",
  vehicleDocument: process.env.E2E_VEHICLE_DOCUMENT,
  renavam: process.env.E2E_VEHICLE_RENAVAM,
};

function requireUser() {
  if (!process.env.E2E_USER_IDENTIFIER || !process.env.E2E_USER_PASSWORD) {
    throw new Error("Defina E2E_USER_IDENTIFIER e E2E_USER_PASSWORD no secret store da CI.");
  }
}

async function signInAndOpenPayments() {
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
  await element(by.text("Boletos e recargas")).tap();
  await expect(element(by.text("Boletos e pagamentos"))).toBeVisible();
}

function requireBillData() {
  if (!paymentData.billCode) throw new Error("Defina E2E_BILL_CODE no secret store da CI.");
}

function requireTopUpData() {
  const names = Object.entries({
    E2E_TOPUP_OPERATOR_ID: paymentData.operatorId,
    E2E_TOPUP_PRODUCT_ID: paymentData.productId,
    E2E_TOPUP_PHONE: paymentData.phone,
  })
    .filter(([, value]) => !value)
    .map(([name]) => name);
  if (names.length > 0) throw new Error(`Defina a massa de recarga: ${names.join(", ")}.`);
}

function requireVehicleData() {
  if (!paymentData.vehicleDocument || !paymentData.renavam) {
    throw new Error("Defina E2E_VEHICLE_DOCUMENT e E2E_VEHICLE_RENAVAM no secret store da CI.");
  }
}

describe("iOS — boletos, recargas e débitos", () => {
  beforeEach(async () => {
    await device.launchApp({ newInstance: true, delete: true });
    await signInAndOpenPayments();
  });

  it("recusa código de boleto inválido antes da consulta", async () => {
    await element(by.label("Linha digitável")).typeText("123");
    await element(by.text("Consultar boleto")).tap();
    await expect(
      element(by.text("Informe uma linha digitável ou código de barras válido.")),
    ).toBeVisible();
  });

  it("consulta e paga boleto com massa isolada", async () => {
    requireBillData();
    await element(by.label("Linha digitável")).typeText(paymentData.billCode!);
    await element(by.text("Consultar boleto")).tap();
    await expect(element(by.text(/^Beneficiário:/))).toBeVisible();
    await element(by.text("Autorizar e pagar")).tap();
    await expect(element(by.text(/^Boleto /))).toBeVisible();
  });

  it("valida campos obrigatórios de recarga", async () => {
    await element(by.text("Realizar recarga")).tap();
    await expect(element(by.text("Confira operadora, produto, telefone e valor."))).toBeVisible();
  });

  it("realiza recarga de celular com massa isolada", async () => {
    requireTopUpData();
    await element(by.label("Operadora")).typeText(paymentData.operatorId!);
    await element(by.label("Produto da recarga")).typeText(paymentData.productId!);
    await element(by.label("Telefone da recarga")).typeText(paymentData.phone!);
    await element(by.label("Valor da recarga")).typeText(paymentData.topUpAmount);
    await element(by.text("Realizar recarga")).tap();
    await expect(element(by.text(/^Recarga /))).toBeVisible();
  });

  it("valida documento e RENAVAM antes da consulta", async () => {
    await element(by.text("Consultar débitos")).tap();
    await expect(element(by.text("Informe documento e RENAVAM."))).toBeVisible();
  });

  it("consulta e paga débitos veiculares abertos", async () => {
    requireVehicleData();
    await element(by.label("Documento veicular")).typeText(paymentData.vehicleDocument!);
    await element(by.label("RENAVAM")).typeText(paymentData.renavam!);
    await element(by.text("Consultar débitos")).tap();
    await expect(element(by.text("Débitos consultados."))).toBeVisible();
    await element(by.text("Pagar débitos selecionados")).tap();
    await expect(element(by.text(/^Débitos /))).toBeVisible();
  });
});
