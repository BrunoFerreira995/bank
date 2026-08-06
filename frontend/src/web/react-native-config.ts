const Config = {
  ENVIRONMENT: import.meta.env.VITE_ENVIRONMENT ?? "development",
  BFF_BASE_URL: import.meta.env.VITE_BFF_BASE_URL ?? "http://localhost:8080",
  ENABLE_PIX: import.meta.env.VITE_ENABLE_PIX ?? "true",
  ENABLE_OPEN_FINANCE: import.meta.env.VITE_ENABLE_OPEN_FINANCE ?? "false",
  ENABLE_CREDIT: import.meta.env.VITE_ENABLE_CREDIT ?? "false",
};

export default Config;
