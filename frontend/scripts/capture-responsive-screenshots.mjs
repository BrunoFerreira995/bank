import fs from "node:fs";
import path from "node:path";
import { chromium } from "@playwright/test";

const baseUrl = process.env.WEB_SCREENSHOT_URL ?? "http://127.0.0.1:4173";
const outputDirectory = path.resolve("artifacts", "responsive-screenshots");
const viewports = [
  { name: "mobile", width: 375, height: 812 },
  { name: "mobile-large", width: 430, height: 932 },
  { name: "tablet", width: 768, height: 1024 },
  { name: "tablet-landscape", width: 1024, height: 768 },
  { name: "notebook", width: 1280, height: 800 },
  { name: "desktop", width: 1440, height: 1000 },
  { name: "desktop-large", width: 1920, height: 1080 },
];
const pages = [
  ["Início", "dashboard"],
  ["Extrato", "extrato"],
  ["Pix", "pix"],
  ["Pagamentos", "pagamentos"],
  ["Cartões e crédito", "produtos"],
  ["Open Finance", "open-finance"],
  ["Serviços", "servicos"],
  ["Suporte", "suporte"],
  ["Perfil", "perfil"],
];

function installBffMock(page) {
  return page.route("**/mobile/v1/**", async (route) => {
    const { pathname } = new URL(route.request().url());
    const response = pathname.endsWith("/session")
      ? { accessToken: "screenshot-token", refreshToken: "screenshot-refresh" }
      : pathname.endsWith("/accounts")
      ? [{ id: "account-1", name: "Conta principal", branch: "0001", number: "12345-6", status: "ACTIVE" }]
      : pathname.endsWith("/balance")
      ? { available: 1250.5, blocked: 0, currency: "BRL" }
      : pathname.endsWith("/movements/today")
      ? []
      : pathname.endsWith("/statement")
      ? { items: [], page: 0, size: 20, totalPages: 0, totalItems: 0 }
      : [];
    await route.fulfill({ status: 200, contentType: "application/json", body: JSON.stringify(response) });
  });
}

async function capture(page, name) {
  await page.waitForTimeout(200);
  const overflow = await page.evaluate(() => document.documentElement.scrollWidth > window.innerWidth);
  if (overflow) throw new Error(`Horizontal overflow detected while capturing ${name}`);
  await page.screenshot({ path: path.join(outputDirectory, name), fullPage: true });
}

async function login(page) {
  await page.goto(baseUrl, { waitUntil: "networkidle" });
  await page.getByLabel("CPF ou CNPJ").fill("52998224725");
  await page.getByLabel("Senha").fill("Senha123");
  await page.getByRole("button", { name: "Entrar" }).click();
  await page.getByRole("heading", { name: "Olá, Conta principal" }).waitFor();
}

fs.mkdirSync(outputDirectory, { recursive: true });
const browser = await chromium.launch({ headless: true });
try {
  for (const viewport of viewports) {
    const context = await browser.newContext({ viewport });
    const page = await context.newPage();
    await installBffMock(page);

    await page.goto(`${baseUrl}/login`, { waitUntil: "networkidle" });
    await capture(page, `${viewport.name}-login.png`);
    await page.getByRole("button", { name: "Abrir uma conta" }).click();
    await capture(page, `${viewport.name}-cadastro.png`);

    await login(page);
    for (const [index, [label, name]] of pages.entries()) {
      if (viewport.width < 600 && index >= 4) await page.getByRole("button", { name: "Mais opções" }).click();
      await page.getByRole("button", { name: label, exact: true }).first().click();
      await capture(page, `${viewport.name}-${name}.png`);
    }
    await context.close();
  }
} finally {
  await browser.close();
}

console.log(`Screenshots saved to ${outputDirectory}`);
