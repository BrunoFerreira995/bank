import { by, device, element, expect } from "detox";

const kycData = {
  pf: {
    document: process.env.E2E_KYC_PF_CPF,
    email: process.env.E2E_KYC_PF_EMAIL,
    phone: process.env.E2E_KYC_PF_PHONE,
  },
  pj: {
    document: process.env.E2E_KYC_PJ_CNPJ,
    email: process.env.E2E_KYC_PJ_EMAIL,
    phone: process.env.E2E_KYC_PJ_PHONE,
    representativeName: process.env.E2E_KYC_REPRESENTATIVE_NAME,
    representativeDocument: process.env.E2E_KYC_REPRESENTATIVE_CPF,
  },
};

function requireKycData() {
  const names = Object.entries({
    E2E_KYC_PF_CPF: kycData.pf.document,
    E2E_KYC_PF_EMAIL: kycData.pf.email,
    E2E_KYC_PF_PHONE: kycData.pf.phone,
    E2E_KYC_PJ_CNPJ: kycData.pj.document,
    E2E_KYC_PJ_EMAIL: kycData.pj.email,
    E2E_KYC_PJ_PHONE: kycData.pj.phone,
    E2E_KYC_REPRESENTATIVE_NAME: kycData.pj.representativeName,
    E2E_KYC_REPRESENTATIVE_CPF: kycData.pj.representativeDocument,
  })
    .filter(([, value]) => !value)
    .map(([name]) => name);

  if (names.length > 0) {
    throw new Error(`Defina a massa KYC no secret store: ${names.join(", ")}.`);
  }
}

async function openOnboarding() {
  await element(by.text("Abrir uma conta")).tap();
  await expect(element(by.text("Abra sua conta"))).toBeVisible();
}

async function fillAddress() {
  await element(by.label("CEP")).typeText("01311000");
  await element(by.label("Rua")).typeText("Avenida Paulista");
  await element(by.label("Número")).typeText("1000");
  await element(by.label("Cidade")).typeText("São Paulo");
  await element(by.label("Estado")).typeText("SP");
}

async function acceptTerms() {
  await element(by.text("Aceitar termos e privacidade")).tap();
  await expect(element(by.text("Consentimentos aceitos"))).toBeVisible();
}

describe("iOS — identidade e onboarding KYC", () => {
  beforeEach(async () => {
    await device.launchApp({ newInstance: true, delete: true });
  });

  it("recusa CPF inválido antes de chamar o BFF", async () => {
    await openOnboarding();
    await element(by.label("CPF")).typeText("11111111111");
    await element(by.text("Continuar")).tap();
    await expect(
      element(by.text("Confira documento, contato e CEP antes de continuar.")),
    ).toBeVisible();
  });

  it("inicia cadastro PF com endereço e consentimentos", async () => {
    requireKycData();
    await openOnboarding();
    await element(by.label("CPF")).typeText(kycData.pf.document!);
    await element(by.label("E-mail")).typeText(kycData.pf.email!);
    await element(by.label("Telefone")).typeText(kycData.pf.phone!);
    await fillAddress();
    await acceptTerms();
    await element(by.text("Continuar")).tap();
    await expect(element(by.text(/^Cadastro iniciado:/))).toBeVisible();
  });

  it("exige representante válido no cadastro PJ", async () => {
    await openOnboarding();
    await element(by.text("Pessoa jurídica")).tap();
    await element(by.label("CNPJ")).typeText("11222333000181");
    await element(by.label("Nome do representante")).typeText("Representante E2E");
    await element(by.label("CPF do representante")).typeText("11111111111");
    await element(by.text("Continuar")).tap();
    await expect(
      element(by.text("Confira documento, contato e CEP antes de continuar.")),
    ).toBeVisible();
  });

  it("inicia cadastro PJ com representante e consentimentos", async () => {
    requireKycData();
    await openOnboarding();
    await element(by.text("Pessoa jurídica")).tap();
    await element(by.label("CNPJ")).typeText(kycData.pj.document!);
    await element(by.label("E-mail")).typeText(kycData.pj.email!);
    await element(by.label("Telefone")).typeText(kycData.pj.phone!);
    await fillAddress();
    await element(by.label("Nome do representante")).typeText(kycData.pj.representativeName!);
    await element(by.label("CPF do representante")).typeText(kycData.pj.representativeDocument!);
    await acceptTerms();
    await element(by.text("Continuar")).tap();
    await expect(element(by.text(/^Cadastro iniciado:/))).toBeVisible();
  });
});
