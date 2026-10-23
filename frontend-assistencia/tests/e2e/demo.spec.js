import { expect, test } from "@playwright/test";
import { abrir, entrarComo, semRolagemHorizontal, semViolacoesGraves } from "./ajuda.js";

test("home mostra a etiqueta, o caminho do aparelho e o aviso da demo", async ({ page }) => {
  await abrir(page, "/");
  await expect(page.getByRole("heading", { level: 1 })).toHaveText("Conserto que você acompanha furo por furo.");
  await expect(page.locator("[data-faixa-demo]")).toContainText("dados fictícios");
  await expect(page.locator("#caminho li")).toHaveCount(5);
  await semRolagemHorizontal(page);
  await semViolacoesGraves(page);
});

test("entrada pelo CPF: erro claro e acesso com o CPF certo", async ({ page }) => {
  await abrir(page, "/entrar");
  await page.getByLabel("CPF", { exact: true }).fill("111.222.333-44");
  await page.getByRole("button", { name: "Entrar", exact: true }).click();
  await expect(page.getByRole("alert")).toContainText("Não achamos ninguém com esse CPF");
  await page.getByLabel("CPF", { exact: true }).fill("52147836900");
  await page.getByRole("button", { name: "Entrar", exact: true }).click();
  await expect(page).toHaveURL(/\/cliente$/);
  await expect(page.getByRole("heading", { level: 1 })).toHaveText("Olá, Rita");
  await semViolacoesGraves(page);
});

test("cliente aprova o orçamento e a OS vai para o reparo", async ({ page }) => {
  await entrarComo(page, "cliente");
  const etiqueta = page.locator("[data-esperando]").first();
  await expect(etiqueta).toContainText("R$ 280,00");
  await semRolagemHorizontal(page);
  await semViolacoesGraves(page);
  const id = await etiqueta.getAttribute("data-os");
  await etiqueta.locator("[data-aprovar]").click();
  await expect(page.locator(`[data-os="${id}"]`)).toHaveAttribute("data-estado", "EM_REPARO");
  await expect(page.locator("[data-aviso=ok]")).toContainText("foi para o reparo");
});

test("cliente recusa o orçamento em dois toques", async ({ page }) => {
  await entrarComo(page, "cliente");
  const etiqueta = page.locator("[data-esperando]").first();
  const id = await etiqueta.getAttribute("data-os");
  await etiqueta.getByRole("button", { name: "Recusar orçamento" }).click();
  await etiqueta.getByRole("button", { name: "Sim, recusar" }).click();
  await expect(page.locator(`[data-os="${id}"]`)).toHaveAttribute("data-estado", "CANCELADA");
});

test("balcão: cadastra cliente novo (com a regra dos 18 anos) e abre a OS", async ({ page }) => {
  await entrarComo(page, "atendente");
  await expect(page).toHaveURL(/\/balcao$/);
  await semRolagemHorizontal(page);
  await semViolacoesGraves(page);

  await page.getByLabel("CPF do cliente").fill("10120230344");
  await page.getByRole("button", { name: "Buscar" }).click();
  const cadastro = page.locator("[data-cadastro]");
  await cadastro.getByLabel("Nome completo").fill("Maria das Dores");
  await cadastro.getByLabel("Data de nascimento").fill("2012-03-01");
  await cadastro.getByRole("button", { name: "Cadastrar cliente" }).click();
  await expect(page.locator("[data-aviso=erro]")).toContainText("pelo menos 18 anos");

  await cadastro.getByLabel("Data de nascimento").fill("1980-03-01");
  await cadastro.getByRole("button", { name: "Cadastrar cliente" }).click();
  await expect(page.locator("[data-cliente-escolhido]")).toContainText("Maria das Dores");

  await page.getByText("Notebook", { exact: true }).click();
  await page.getByLabel("Defeito relatado").fill("Tela piscando.");
  await page.locator("[data-abrir-os]").click();
  await expect(page.locator("[data-aviso=ok]").last()).toContainText("aberta para Maria das Dores");
  const linha = page.locator("[data-lista-os] li").first();
  await expect(linha).toContainText("Maria das Dores");
  await expect(linha).toContainText("Na fila");
});

test("bancada: assume, orça e segue o roteiro na ordem até finalizar", async ({ page }) => {
  await entrarComo(page, "tecnico");
  await expect(page).toHaveURL(/\/bancada$/);
  await semRolagemHorizontal(page);
  await semViolacoesGraves(page);

  const primeira = page.locator("[data-fila] [data-os]").first();
  const id = await primeira.getAttribute("data-os");
  await primeira.locator("[data-assumir]").click();
  const minha = page.locator(`[data-os="${id}"]`);
  await expect(minha).toHaveAttribute("data-estado", "AGUARDANDO_ORCAMENTO");
  await minha.getByLabel("Diagnóstico").fill("Fonte queimada.");
  await minha.getByLabel("Valor (R$)").fill("150");
  await minha.locator("[data-enviar-orcamento]").click();
  await expect(minha).toHaveAttribute("data-estado", "AGUARDANDO_APROVACAO");

  const reparo = page.locator('[data-estado="EM_REPARO"]').first();
  await reparo.locator("[data-iniciar-roteiro]").click();
  const caixas = reparo.locator("[data-roteiro] input[type=checkbox]");
  await expect(caixas).toHaveCount(5);
  await expect(caixas.nth(2)).toBeDisabled();
  await expect(reparo.locator("[data-finalizar]")).toBeDisabled();
  for (let i = 0; i < 5; i++) await caixas.nth(i).check();
  const idReparo = await reparo.getAttribute("data-os");
  await reparo.locator("[data-finalizar]").click();
  await expect(page.locator(`[data-os="${idReparo}"]`)).toHaveAttribute("data-estado", "FINALIZADA");
});

test("gerência: regras de exclusão da API aparecem como aviso", async ({ page }) => {
  await entrarComo(page, "admin");
  await expect(page).toHaveURL(/\/gerencia$/);
  await expect(page.locator("[data-numeros]")).toContainText("Na fila do balcão");
  await semRolagemHorizontal(page);
  await semViolacoesGraves(page);

  await page.locator('[data-aba="pecas"]').click();
  const usada = page.locator("[data-tabela=pecas] tr", { hasText: "SSD SATA" });
  await usada.getByRole("button", { name: "Excluir" }).click();
  await usada.getByRole("button", { name: "Sim, excluir" }).click();
  await expect(page.locator("[data-aviso=erro]")).toContainText("Não é possível excluir esta peça");
  await semRolagemHorizontal(page);

  await page.locator('[data-aba="clientes"]').click();
  const rita = page.locator("[data-tabela=clientes] tr", { hasText: "Rita Araújo" });
  await rita.getByRole("button", { name: "Excluir" }).click();
  await rita.getByRole("button", { name: "Sim, excluir" }).click();
  await expect(page.locator("[data-aviso=erro]").last()).toContainText("Não é possível excluir este cliente");
  await semRolagemHorizontal(page);
});

test("rotas protegidas mandam para a entrada ou para a área do perfil", async ({ page }) => {
  await abrir(page, "/gerencia");
  await expect(page).toHaveURL(/\/entrar\?volta=/);
  await page.locator('[data-entrar-demo][data-perfil="cliente"]').click();
  await expect(page).toHaveURL(/\/cliente$/);
  await page.goto("/bancada");
  await expect(page).toHaveURL(/\/cliente$/);
});

test("restaurar dados de exemplo desfaz as mudanças", async ({ page }) => {
  await entrarComo(page, "cliente");
  await page.locator("[data-esperando] [data-aprovar]").first().click();
  await expect(page.locator("[data-esperando]")).toHaveCount(0);
  await page.getByRole("button", { name: "Restaurar dados de exemplo" }).click();
  await expect(page).toHaveURL("/");
  await entrarComo(page, "cliente");
  await expect(page.locator("[data-esperando]")).toHaveCount(1);
});

test("página inexistente mostra a 404 da loja", async ({ page }) => {
  await abrir(page, "/nao-existe");
  await expect(page.getByRole("heading", { level: 1 })).toHaveText("Essa página não está na bancada.");
  await semViolacoesGraves(page);
});
