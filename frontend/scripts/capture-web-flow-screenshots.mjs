import { spawnSync } from "node:child_process";
import path from "node:path";
import { fileURLToPath } from "node:url";

const frontendDirectory = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const screenshotDirectory = path.join(frontendDirectory, "artifacts", "web-flows");
const playwright = process.platform === "win32" ? "npx.cmd" : "npx";

console.log(`Capturing web flow screenshots in ${screenshotDirectory}`);

const result = spawnSync(
  playwright,
  ["playwright", "test", "--config=playwright.config.ts", "e2e/web/app.spec.ts"],
  {
    cwd: frontendDirectory,
    env: {
      ...process.env,
      CAPTURE_WEB_FLOW_SCREENSHOTS: "1",
      WEB_FLOW_SCREENSHOT_DIR: screenshotDirectory,
    },
    stdio: "inherit",
  },
);

if (result.error) throw result.error;
process.exit(result.status ?? 1);
