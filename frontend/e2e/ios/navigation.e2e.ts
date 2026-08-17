import { by, device, element, expect } from "detox";

describe("iOS — navegação pública", () => {
  beforeEach(async () => {
    await device.launchApp({ newInstance: true, delete: true });
  });

  it("abre a jornada de abertura de conta", async () => {
    await element(by.text("Abrir uma conta")).tap();
    await expect(element(by.text("Abra sua conta"))).toBeVisible();
  });

  it("abre a rota de login por deep link", async () => {
    await device.openURL({ url: "celcoin://login" });
    await expect(element(by.text("Acesse sua conta"))).toBeVisible();
  });
});
