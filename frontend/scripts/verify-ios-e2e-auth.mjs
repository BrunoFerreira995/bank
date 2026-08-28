import fs from "node:fs";

const required = ["E2E_USER_IDENTIFIER", "E2E_USER_PASSWORD"];
const placeholderPattern = /^(CPF_OU_CNPJ|SENHA|CODIGO|SEU_|.*DA_MASSA|.*REAL)/i;
const envFile = fs.existsSync(".env.e2e-ios") ? fs.readFileSync(".env.e2e-ios", "utf8") : "";
const fileValues = Object.fromEntries(
  envFile
    .split(/\r?\n/)
    .map((line) => line.match(/^([A-Z0-9_]+)=(.*)$/))
    .filter(Boolean)
    .map(([, name, setting]) => [name, setting]),
);

function value(name) {
  return (process.env[name] ?? fileValues[name] ?? "").trim();
}

const missing = required.filter((name) => !value(name));
const placeholders = required.filter((name) => placeholderPattern.test(value(name)));
const bff = value("BFF_BASE_URL");

if (!bff) missing.push("BFF_BASE_URL");
if (bff.includes("example.invalid") || bff.includes("SEU-BFF-REAL")) {
  throw new Error("BFF_BASE_URL ainda aponta para um placeholder.");
}

try {
  const parsed = new URL(bff);
  if (!/^https?:$/.test(parsed.protocol)) throw new Error("protocolo inválido");
} catch {
  throw new Error("BFF_BASE_URL deve ser uma URL HTTP(S) válida.");
}

if (placeholders.length > 0) {
  throw new Error(`Substitua os placeholders das variáveis: ${placeholders.join(", ")}.`);
}

if (missing.length > 0) {
  throw new Error(`Variáveis E2E ausentes: ${missing.join(", ")}.`);
}

console.log("iOS E2E auth preflight: configuração presente (segredos ocultos).");
