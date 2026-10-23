import { describe, expect, it } from "vitest";
import { ETAPAS, codigoOS, etapaAtual, rotuloEstado } from "../../src/lib/estados.js";
import { aparelho, cpf, dinheiro, haQuanto, data, primeiroNome, soDigitos } from "../../src/lib/formato.js";

const AGORA = new Date("2026-10-21T10:30:00-03:00");

describe("estados da OS", () => {
  it("as etapas da etiqueta seguem a ordem do State", () => {
    expect(ETAPAS.map((e) => e.estado)).toEqual([
      "ABERTA",
      "AGUARDANDO_ORCAMENTO",
      "AGUARDANDO_APROVACAO",
      "EM_REPARO",
      "FINALIZADA",
    ]);
  });

  it("etapa atual: índice na sequência; cancelada fica fora", () => {
    expect(etapaAtual("ABERTA")).toBe(0);
    expect(etapaAtual("FINALIZADA")).toBe(4);
    expect(etapaAtual("CANCELADA")).toBe(-1);
  });

  it("rótulo muda para o cliente quando o orçamento espera por ele", () => {
    expect(rotuloEstado("AGUARDANDO_APROVACAO")).toBe("Orçamento enviado");
    expect(rotuloEstado("AGUARDANDO_APROVACAO", "cliente")).toBe("Esperando sua aprovação");
    expect(rotuloEstado("FINALIZADA")).toBe("Pronto");
  });

  it("código curto da OS a partir do UUID", () => {
    expect(codigoOS("0d01c5a1-7e2b-4c3d-9a10-000001007919")).toBe("0D01C5");
  });
});

describe("formatação", () => {
  it("dinheiro em reais", () => {
    expect(dinheiro(280)).toBe("R$ 280,00");
    expect(dinheiro(0)).toBe("R$ 0,00");
  });

  it("CPF com máscara e só dígitos", () => {
    expect(cpf("52147836900")).toBe("521.478.369-00");
    expect(soDigitos("521.478.369-00")).toBe("52147836900");
  });

  it("tempo relativo curto", () => {
    expect(haQuanto("2026-10-21T13:00:00Z", AGORA)).toBe("agora há pouco");
    expect(haQuanto("2026-10-21T10:30:00Z", AGORA)).toBe("há 3 horas");
    expect(haQuanto("2026-10-20T09:00:00Z", AGORA)).toBe("ontem");
    expect(haQuanto("2026-10-11T12:00:00Z", AGORA)).toBe("há 10 dias");
  });

  it("data por extenso curta", () => {
    expect(data("2026-10-21T13:30:00Z")).toBe("21 de out. de 2026");
  });

  it("nomes de aparelho e primeiro nome", () => {
    expect(aparelho("NOTEBOOK")).toBe("Notebook");
    expect(aparelho("OUTROS")).toBe("Outro aparelho");
    expect(primeiroNome("Rita Araújo")).toBe("Rita");
  });
});
