import AxeBuilder from "@axe-core/playwright";
import { expect } from "@playwright/test";

export const AGORA = new Date("2026-10-21T10:30:00-03:00");

/** Abre uma rota com o relógio fixo e sem dados de visitas anteriores. */
export async function abrir(page, rota) {
  await page.clock.setFixedTime(AGORA);
  await page.goto(rota);
}

export async function entrarComo(page, perfil) {
  await abrir(page, "/entrar");
  await page.locator(`[data-entrar-demo][data-perfil="${perfil}"]`).click();
}

export async function semRolagemHorizontal(page) {
  const sobra = await page.evaluate(() => document.documentElement.scrollWidth - document.documentElement.clientWidth);
  expect(sobra).toBe(0);
}

export async function semViolacoesGraves(page) {
  const { violations } = await new AxeBuilder({ page }).analyze();
  const graves = violations.filter((v) => ["serious", "critical"].includes(v.impact));
  expect(graves.map((v) => `${v.id}: ${v.nodes.map((n) => n.target).join(", ")}`)).toEqual([]);
}
