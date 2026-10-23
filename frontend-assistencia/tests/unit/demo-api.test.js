import { beforeEach, describe, expect, it } from "vitest";
import { createDemoApi } from "../../src/services/demo/api.js";
import { DEMO_USERS } from "../../src/services/demo/seed.js";

function memoria() {
  const m = new Map();
  return {
    getItem: (k) => (m.has(k) ? m.get(k) : null),
    setItem: (k, v) => m.set(k, String(v)),
    removeItem: (k) => m.delete(k),
  };
}

const AGORA = new Date("2026-10-21T10:30:00-03:00");
let api;

beforeEach(() => {
  api = createDemoApi({ storage: memoria(), now: () => AGORA });
});

async function osNoEstado(estado) {
  const todas = await api.listarOS();
  const os = todas.find((o) => o.estado === estado);
  if (!os) throw new Error(`seed sem OS em ${estado}`);
  return os;
}

async function novaOS(tipo = "NOTEBOOK") {
  const [cliente] = await api.listarPessoas("clientes");
  const hw = await api.criarHardware({ tipoHardware: tipo, clienteId: cliente.id });
  return api.criarOS({ descricao: "Não liga", clienteId: cliente.id, hardwareId: hw.numeroSerie });
}

describe("entrada por perfil e CPF", () => {
  it("encontra os usuários de demonstração de cada perfil", async () => {
    for (const u of Object.values(DEMO_USERS)) {
      const achado = await api.buscarPorCpf(u.perfil, u.cpf);
      expect(achado.nome).toBe(u.nome);
    }
  });

  it("CPF desconhecido dá a mensagem da API", async () => {
    await expect(api.buscarPorCpf("clientes", "99999999999")).rejects.toThrow(
      "Cliente com CPF 99999999999 não encontrado.",
    );
  });
});

describe("máquina de estados da OS (padrão State)", () => {
  it("OS nova nasce ABERTA, sem técnico e sem valor", async () => {
    const os = await novaOS();
    expect(os.estado).toBe("ABERTA");
    expect(os.tecnicoResponsavel).toBeNull();
    expect(os.valorOrcamento).toBe(0);
    expect(os.hardware.tipoHardware).toBe("NOTEBOOK");
  });

  it("caminho feliz: assumir, orçar, aprovar, executar e finalizar", async () => {
    const os = await novaOS("CELULAR");
    const [tecnico] = await api.listarPessoas("tecnicos");

    let atual = await api.assumirOS(os.id, tecnico.id);
    expect(atual.estado).toBe("AGUARDANDO_ORCAMENTO");
    expect(atual.tecnicoResponsavel.id).toBe(tecnico.id);

    atual = await api.enviarOrcamento(os.id, 180);
    expect(atual.estado).toBe("AGUARDANDO_APROVACAO");
    expect(atual.valorOrcamento).toBe(180);

    atual = await api.aprovarOS(os.id);
    expect(atual.estado).toBe("EM_REPARO");

    const { os: depois, roteiro } = await api.executarOS(os.id);
    expect(depois.estado).toBe("EM_REPARO");
    expect(roteiro.map((e) => e.etapa)).toEqual(["Diagnóstico", "Materiais", "Reparo", "Testes", "Limpeza"]);
    expect(roteiro[0].passos[0]).toBe("Verificando integridade do display e touch.");

    atual = await api.finalizarOS(os.id);
    expect(atual.estado).toBe("FINALIZADA");
  });

  it("assumir só muda o estado se a OS estiver ABERTA", async () => {
    const os = await osNoEstado("EM_REPARO");
    const [, outro] = await api.listarPessoas("tecnicos");
    const atual = await api.assumirOS(os.id, outro.id);
    expect(atual.estado).toBe("EM_REPARO");
  });

  it("orçamentar também vale direto de ABERTA", async () => {
    const os = await novaOS();
    expect((await api.enviarOrcamento(os.id, 90)).estado).toBe("AGUARDANDO_APROVACAO");
  });

  it("ações fora de ordem dão a mensagem do State", async () => {
    const os = await novaOS();
    await expect(api.aprovarOS(os.id)).rejects.toThrow("Ação 'Aprovar' não é permitida neste estado.");
    await expect(api.finalizarOS(os.id)).rejects.toThrow("Ação 'Finalizar' não é permitida neste estado.");
    await expect(api.executarOS(os.id)).rejects.toThrow("Ação 'Executar' não é permitida neste estado.");
  });

  it("orçamento recusado pelo estado não muda o valor", async () => {
    const os = await osNoEstado("EM_REPARO");
    await expect(api.enviarOrcamento(os.id, 1)).rejects.toThrow("Ação 'Orçamentar' não é permitida neste estado.");
    expect((await api.buscarOS(os.id)).valorOrcamento).toBe(os.valorOrcamento);
  });

  it("cancelar vale até o reparo, mas não depois de finalizada ou cancelada", async () => {
    for (const estado of ["ABERTA", "AGUARDANDO_ORCAMENTO", "AGUARDANDO_APROVACAO", "EM_REPARO"]) {
      const os = await osNoEstado(estado);
      expect((await api.cancelarOS(os.id)).estado).toBe("CANCELADA");
    }
    for (const estado of ["FINALIZADA", "CANCELADA"]) {
      const os = await osNoEstado(estado);
      await expect(api.cancelarOS(os.id)).rejects.toThrow("Ação 'Cancelar' não é permitida neste estado.");
    }
  });

  it("executar sem técnico usa a mensagem do EstadoEmReparo", async () => {
    const os = await novaOS();
    await api.enviarOrcamento(os.id, 50);
    await api.aprovarOS(os.id);
    await expect(api.executarOS(os.id)).rejects.toThrow(
      "Não é possível executar o reparo. Nenhum técnico foi atribuído a esta OS.",
    );
  });

  it("cada tipo de aparelho tem o seu roteiro (Template Method), com limpeza própria em Outros", async () => {
    const limpezas = {};
    for (const tipo of ["NOTEBOOK", "CELULAR", "COMPUTADOR", "OUTROS"]) {
      const os = await novaOS(tipo);
      const [tecnico] = await api.listarPessoas("tecnicos");
      await api.assumirOS(os.id, tecnico.id);
      await api.enviarOrcamento(os.id, 100);
      await api.aprovarOS(os.id);
      const { roteiro } = await api.executarOS(os.id);
      limpezas[tipo] = roteiro.at(-1).passos[0];
    }
    expect(limpezas.NOTEBOOK).toBe("Executando limpeza padrão do equipamento.");
    expect(limpezas.OUTROS).toBe("Limpeza externa com álcool isopropílico e remoção de etiquetas antigas.");
  });

  it("só exclui OS ABERTA", async () => {
    const os = await osNoEstado("EM_REPARO");
    await expect(api.excluirOS(os.id)).rejects.toThrow("Não é possível excluir uma OS em andamento.");
    const nova = await novaOS();
    await api.excluirOS(nova.id);
    await expect(api.buscarOS(nova.id)).rejects.toThrow(`Ordem de Serviço não encontrada com ID: ${nova.id}`);
  });

  it("salva o laudo técnico", async () => {
    const os = await osNoEstado("EM_REPARO");
    expect((await api.salvarObservacoes(os.id, "Conector trocado.")).observacoesTecnicas).toBe("Conector trocado.");
  });

  it("filtra por cliente, técnico e estado", async () => {
    const cliente = await api.buscarPorCpf("clientes", DEMO_USERS.cliente.cpf);
    const tecnico = await api.buscarPorCpf("tecnicos", DEMO_USERS.tecnico.cpf);
    const doCliente = await api.listarOSDoCliente(cliente.id);
    expect(doCliente.length).toBeGreaterThan(0);
    expect(doCliente.every((o) => o.cliente.id === cliente.id)).toBe(true);
    const doTecnico = await api.listarOSDoTecnico(tecnico.id);
    expect(doTecnico.every((o) => o.tecnicoResponsavel?.id === tecnico.id)).toBe(true);
    const abertas = await api.listarOSPorEstado("ABERTA");
    expect(abertas.every((o) => o.estado === "ABERTA")).toBe(true);
  });

  it("OS e cliente inexistentes dão a mensagem da API", async () => {
    await expect(api.buscarOS("nao-existe")).rejects.toThrow("Ordem de Serviço não encontrada com ID: nao-existe");
    await expect(api.criarHardware({ tipoHardware: "CELULAR", clienteId: 999 })).rejects.toThrow(
      "Cliente não encontrado com ID: 999",
    );
  });
});

describe("pessoas e peças", () => {
  const pessoa = (cpf, dataNascimento = "1990-05-10") => ({ nome: "Teste", cpf, dataNascimento, endereco: "Rua A" });

  it("cadastra cliente maior de idade", async () => {
    const c = await api.criarPessoa("clientes", pessoa("10120230344"));
    expect((await api.buscarPorCpf("clientes", "10120230344")).id).toBe(c.id);
  });

  it("recusa CPF repetido no mesmo perfil", async () => {
    await expect(api.criarPessoa("clientes", pessoa(DEMO_USERS.cliente.cpf))).rejects.toThrow(
      `Já existe um Cliente com o CPF: ${DEMO_USERS.cliente.cpf}`,
    );
    await expect(api.criarPessoa("tecnicos", pessoa(DEMO_USERS.tecnico.cpf))).rejects.toThrow(
      `Já existe um Técnico com o CPF: ${DEMO_USERS.tecnico.cpf}`,
    );
  });

  it("recusa menor de 18 anos e data em branco", async () => {
    await expect(api.criarPessoa("clientes", pessoa("10120230355", "2010-01-01"))).rejects.toThrow(
      "A pessoa deve ter pelo menos 18 anos. Idade calculada: 16",
    );
    await expect(api.criarPessoa("clientes", pessoa("10120230366", ""))).rejects.toThrow(
      "A data de nascimento é obrigatória.",
    );
  });

  it("não exclui cliente nem técnico com OS", async () => {
    const cliente = await api.buscarPorCpf("clientes", DEMO_USERS.cliente.cpf);
    const n = (await api.listarOSDoCliente(cliente.id)).length;
    await expect(api.excluirPessoa("clientes", cliente.id)).rejects.toThrow(
      `Não é possível excluir este cliente, pois ele está associado a ${n} Ordem(ns) de Serviço.`,
    );
    const tecnico = await api.buscarPorCpf("tecnicos", DEMO_USERS.tecnico.cpf);
    await expect(api.excluirPessoa("tecnicos", tecnico.id)).rejects.toThrow(/Não é possível excluir este técnico/);
  });

  it("exclui cliente sem OS", async () => {
    const c = await api.criarPessoa("clientes", pessoa("10120230377"));
    await api.excluirPessoa("clientes", c.id);
    await expect(api.buscarPorCpf("clientes", "10120230377")).rejects.toThrow();
  });

  it("não exclui peça usada em OS, mas exclui peça nova", async () => {
    const pecas = await api.listarPecas();
    const usada = pecas.find((p) => p.usos > 0);
    await expect(api.excluirPeca(usada.numeroSerie)).rejects.toThrow(
      `Não é possível excluir esta peça, pois ela está associada a ${usada.usos} Ordem(ns) de Serviço.`,
    );
    const nova = await api.criarPeca({ nome: "Tela 6.1", tipoPeca: "CELULAR" });
    await api.excluirPeca(nova.numeroSerie);
    expect((await api.listarPecas()).some((p) => p.numeroSerie === nova.numeroSerie)).toBe(false);
  });
});

describe("dados de exemplo", () => {
  it("têm OS em todos os estados", async () => {
    const estados = new Set((await api.listarOS()).map((o) => o.estado));
    for (const e of ["ABERTA", "AGUARDANDO_ORCAMENTO", "AGUARDANDO_APROVACAO", "EM_REPARO", "FINALIZADA", "CANCELADA"]) {
      expect(estados.has(e)).toBe(true);
    }
  });

  it("a cliente de demonstração tem um orçamento esperando aprovação", async () => {
    const cliente = await api.buscarPorCpf("clientes", DEMO_USERS.cliente.cpf);
    const minhas = await api.listarOSDoCliente(cliente.id);
    expect(minhas.some((o) => o.estado === "AGUARDANDO_APROVACAO")).toBe(true);
  });

  it("nenhum CPF de exemplo é um CPF válido (não pode ser de alguém de verdade)", async () => {
    const valido = (cpf) => {
      const d = cpf.split("").map(Number);
      const dv = (n) => {
        const s = d.slice(0, n).reduce((t, x, i) => t + x * (n + 1 - i), 0);
        const r = (s * 10) % 11;
        return r === 10 ? 0 : r;
      };
      return dv(9) === d[9] && dv(10) === d[10];
    };
    for (const perfil of ["clientes", "tecnicos", "atendentes", "administradores"]) {
      for (const p of await api.listarPessoas(perfil)) expect(valido(p.cpf), p.cpf).toBe(false);
    }
  });

  it("persiste entre instâncias e restaura com resetDemo", async () => {
    const storage = memoria();
    const a = createDemoApi({ storage, now: () => AGORA });
    const os = (await a.listarOSPorEstado("ABERTA"))[0];
    await a.cancelarOS(os.id);
    const b = createDemoApi({ storage, now: () => AGORA });
    expect((await b.buscarOS(os.id)).estado).toBe("CANCELADA");
    await b.resetDemo();
    expect((await b.buscarOS(os.id)).estado).toBe("ABERTA");
  });
});
