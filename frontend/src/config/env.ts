import Config from "react-native-config";

export type MobileEnvironment = "development" | "staging" | "sandbox" | "production";

function booleanFlag(value: string | undefined, fallback = false): boolean {
  if (value === undefined) return fallback;
  return value.toLowerCase() === "true";
}

const environment = (Config.ENVIRONMENT ?? "development") as MobileEnvironment;
const baseUrl = Config.BFF_BASE_URL ?? "http://localhost:8080";

if (!/^https?:\/\//.test(baseUrl)) {
  throw new Error("BFF_BASE_URL must be an absolute HTTP(S) URL");
}

export const env = Object.freeze({
  environment,
  bffBaseUrl: baseUrl.replace(/\/$/, ""),
  flags: Object.freeze({
    pix: booleanFlag(Config.ENABLE_PIX, true),
    openFinance: booleanFlag(Config.ENABLE_OPEN_FINANCE),
    credit: booleanFlag(Config.ENABLE_CREDIT),
  }),
});
