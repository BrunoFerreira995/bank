import fs from "node:fs";
import path from "node:path";

const root = process.cwd();
const required = [
  "src/components/AsyncState.tsx",
  "src/core/http/api-client.ts",
  "src/core/operations/push-handler.ts",
  "src/core/security/redaction.ts",
  "test/fixtures/sandbox/bff-contract.json",
  "e2e/README.md",
  "../docs/mobile-release-checklist.md",
];
const missing = required.filter((file) => !fs.existsSync(path.resolve(root, file)));
if (missing.length) throw new Error(`Missing frontend definition artifacts: ${missing.join(", ")}`);

const sourceRoot = path.resolve(root, "src");
const source = collect(sourceRoot)
  .map((file) => fs.readFileSync(file, "utf8"))
  .join("\n");
const forbidden = [
  /client_secret\s*[:=]/i,
  /clientSecret\s*[:=]/i,
  /BEGIN (RSA |EC )?PRIVATE KEY/i,
  /cvv\s*[:=]\s*["'`]/i,
];
const violations = forbidden.filter((pattern) => pattern.test(source));
if (violations.length)
  throw new Error("Potential secret or sensitive card data found in frontend source");

if (
  !source.includes("Idempotency-Key") ||
  !source.includes("processPushEvent") ||
  !source.includes("redactProperties")
) {
  throw new Error("Frontend security invariants are incomplete");
}
console.log("Frontend 100% definition gate: structural checks passed");

function collect(directory) {
  return fs.readdirSync(directory, { withFileTypes: true }).flatMap((entry) => {
    const file = path.join(directory, entry.name);
    return entry.isDirectory() ? collect(file) : /\.(ts|tsx)$/.test(entry.name) ? [file] : [];
  });
}
