import { defineConfig, devices } from "@playwright/test";

/** Testes no build estático da demo (npm run build:demo), servido como SPA. */
export default defineConfig({
  testDir: "tests/e2e",
  fullyParallel: true,
  retries: process.env.CI ? 1 : 0,
  reporter: process.env.CI ? "github" : "list",
  use: {
    baseURL: "http://localhost:3201",
    timezoneId: "America/Recife",
    locale: "pt-BR",
  },
  projects: [
    { name: "celular", use: { ...devices["Pixel 7"] } },
    { name: "computador", use: { viewport: { width: 1280, height: 800 } } },
  ],
  webServer: {
    command: "npx serve dist -s -l 3201 --no-port-switching",
    url: "http://localhost:3201",
    reuseExistingServer: !process.env.CI,
  },
});
