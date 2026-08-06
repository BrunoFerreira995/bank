import path from "node:path";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  root: "web",
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"),
      "react-native": "react-native-web",
      "react-native-config": path.resolve(__dirname, "src/web/react-native-config.ts"),
      "react-native-keychain": path.resolve(__dirname, "src/web/react-native-keychain.ts"),
      "react-native-gesture-handler": path.resolve(__dirname, "src/web/gesture-handler.ts"),
      "react-native-safe-area-context": path.resolve(__dirname, "src/web/safe-area-context.tsx"),
      "react-native-screens": path.resolve(__dirname, "src/web/screens.tsx"),
    },
  },
  define: { global: "globalThis" },
  server: { port: 8081 },
  build: { outDir: path.resolve(__dirname, "dist-web"), emptyOutDir: true },
});
