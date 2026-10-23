/**
 * Backend de demonstração que roda no navegador. Mesmas funções do cliente HTTP
 * (src/services/http.js) e mesmas regras e mensagens da API Spring Boot:
 * OrdemServicoServico + estados do padrão State, roteiros do Template Method e
 * as validações de PessoaServicoAbstrato, ClienteServico, TecnicoServico e PecaServico.
 */
import { roteiroDe } from "./roteiros.js";
import { createSeed } from "./seed.js";

const CHAVE = "ponto-de-solda-demo:dados";

const NOMES = {
  clientes: "Cliente",
  tecnicos: "Técnico",
  atendentes: "Atendente",
  administradores: "Administrador",
};

/** Transições do padrão State: ação → estados em que ela vale → próximo estado. */
const TRANSICOES = {
  Orçamentar: { de: ["ABERTA", "AGUARDANDO_ORCAMENTO"], para: "AGUARDANDO_APROVACAO" },
  Aprovar: { de: ["AGUARDANDO_APROVACAO"], para: "EM_REPARO" },
  Finalizar: { de: ["EM_REPARO"], para: "FINALIZADA" },
  Cancelar: { de: ["ABERTA", "AGUARDANDO_ORCAMENTO", "AGUARDANDO_APROVACAO", "EM_REPARO"], para: "CANCELADA" },
};

const copia = (x) => JSON.parse(JSON.stringify(x));

function idade(nascimento, hoje) {
  const [a, m, d] = nascimento.split("-").map(Number);
  let anos = hoje.getFullYear() - a;
  if (hoje.getMonth() + 1 < m || (hoje.getMonth() + 1 === m && hoje.getDate() < d)) anos -= 1;
  return anos;
}

export function createDemoApi({ storage, now = () => new Date() }) {
  let dados = carregar();

  function carregar() {
    try {
      const salvo = storage.getItem(CHAVE);
      if (salvo) return JSON.parse(salvo);
    } catch {
      // dado corrompido: volta aos exemplos
    }
    return createSeed(now());
  }

  function salvar() {
    try {
      storage.setItem(CHAVE, JSON.stringify(dados));
    } catch {
      // armazenamento cheio ou bloqueado: a demo segue só em memória
    }
  }

  function agoraISO() {
    return now().toISOString();
  }

  // --- leitura no formato da API ---

  const clientePorId = (id) => dados.clientes.find((c) => c.id === id);

  function hardwareJSON(h) {
    return { numeroSerie: h.numeroSerie, tipoHardware: h.tipoHardware, cliente: copia(clientePorId(h.clienteId)) };
  }

  function osJSON(o) {
    const tecnico = o.tecnicoId != null ? dados.tecnicos.find((t) => t.id === o.tecnicoId) : null;
    return {
      id: o.id,
      valorOrcamento: o.valorOrcamento,
      descricao: o.descricao,
      observacoesTecnicas: o.observacoesTecnicas,
      dataCriacao: o.dataCriacao,
      dataAtualizacao: o.dataAtualizacao,
      estado: o.estado,
      cliente: copia(clientePorId(o.clienteId)),
      hardware: hardwareJSON(dados.hardwares.find((h) => h.numeroSerie === o.hardwareId)),
      tecnicoResponsavel: tecnico ? copia(tecnico) : null,
      pecasUtilizadas: o.pecasIds.map((id) => copia(dados.pecas.find((p) => p.numeroSerie === id))).filter(Boolean),
    };
  }

  function osBruta(id) {
    const o = dados.ordens.find((x) => x.id === id);
    if (!o) throw new Error(`Ordem de Serviço não encontrada com ID: ${id}`);
    return o;
  }

  function lista(filtro = () => true) {
    return dados.ordens.filter(filtro).map(osJSON);
  }

  function transicionar(id, acao) {
    const o = osBruta(id);
    const t = TRANSICOES[acao];
    if (!t.de.includes(o.estado)) throw new Error(`Ação '${acao}' não é permitida neste estado.`);
    o.estado = t.para;
    o.dataAtualizacao = agoraISO();
    salvar();
    return osJSON(o);
  }

  function validarIdade(dataNascimento) {
    if (!dataNascimento) throw new Error("A data de nascimento é obrigatória.");
    const anos = idade(dataNascimento, now());
    if (anos < 18) throw new Error(`A pessoa deve ter pelo menos 18 anos. Idade calculada: ${anos}`);
  }

  const api = {
    // --- pessoas ---
    async listarPessoas(perfil) {
      return copia(dados[perfil]);
    },

    async buscarPorCpf(perfil, cpf) {
      const p = dados[perfil].find((x) => x.cpf === cpf);
      if (!p) throw new Error(`${NOMES[perfil]} com CPF ${cpf} não encontrado.`);
      return copia(p);
    },

    async criarPessoa(perfil, dto) {
      if (dados[perfil].some((x) => x.cpf === dto.cpf)) {
        throw new Error(`Já existe um ${NOMES[perfil]} com o CPF: ${dto.cpf}`);
      }
      validarIdade(dto.dataNascimento);
      const nova = {
        id: dados.proximo.pessoa++,
        nome: dto.nome,
        cpf: dto.cpf,
        dataNascimento: dto.dataNascimento,
        endereco: dto.endereco ?? "",
      };
      dados[perfil].push(nova);
      salvar();
      return copia(nova);
    },

    async excluirPessoa(perfil, id) {
      const p = dados[perfil].find((x) => x.id === id);
      if (!p) throw new Error(`Objeto não encontrado(a) com ID: ${id}`);
      const campo = perfil === "clientes" ? "clienteId" : perfil === "tecnicos" ? "tecnicoId" : null;
      if (campo) {
        const n = dados.ordens.filter((o) => o[campo] === id).length;
        if (n > 0) {
          const quem = perfil === "clientes" ? "este cliente" : "este técnico";
          throw new Error(`Não é possível excluir ${quem}, pois ele está associado a ${n} Ordem(ns) de Serviço.`);
        }
      }
      dados[perfil] = dados[perfil].filter((x) => x.id !== id);
      if (perfil === "clientes") dados.hardwares = dados.hardwares.filter((h) => h.clienteId !== id);
      salvar();
    },

    // --- aparelhos e peças ---
    async listarHardwares() {
      return dados.hardwares.map(hardwareJSON);
    },

    async criarHardware({ tipoHardware, clienteId }) {
      if (!clientePorId(clienteId)) throw new Error(`Cliente não encontrado com ID: ${clienteId}`);
      const h = { numeroSerie: dados.proximo.hardware++, tipoHardware, clienteId };
      dados.hardwares.push(h);
      salvar();
      return hardwareJSON(h);
    },

    /** Peças com `usos`: em quantas OS cada uma aparece (o admin usa para avisar antes de excluir). */
    async listarPecas() {
      return dados.pecas.map((p) => ({
        ...copia(p),
        usos: dados.ordens.filter((o) => o.pecasIds.includes(p.numeroSerie)).length,
      }));
    },

    async criarPeca({ nome, tipoPeca }) {
      const p = { numeroSerie: dados.proximo.peca++, nome, tipoPeca };
      dados.pecas.push(p);
      salvar();
      return copia(p);
    },

    async excluirPeca(id) {
      const p = dados.pecas.find((x) => x.numeroSerie === id);
      if (!p) throw new Error(`Peça não encontrada com ID: ${id}`);
      const n = dados.ordens.filter((o) => o.pecasIds.includes(id)).length;
      if (n > 0) {
        throw new Error(`Não é possível excluir esta peça, pois ela está associada a ${n} Ordem(ns) de Serviço.`);
      }
      dados.pecas = dados.pecas.filter((x) => x.numeroSerie !== id);
      salvar();
    },

    // --- ordens de serviço ---
    async listarOS() {
      return lista();
    },

    async buscarOS(id) {
      return osJSON(osBruta(id));
    },

    async listarOSDoCliente(clienteId) {
      return lista((o) => o.clienteId === clienteId);
    },

    async listarOSDoTecnico(tecnicoId) {
      return lista((o) => o.tecnicoId === tecnicoId);
    },

    async listarOSPorEstado(estado) {
      return lista((o) => o.estado === estado);
    },

    async criarOS({ descricao, clienteId, hardwareId, tecnicoId = null, valorOrcamento = 0 }) {
      if (!clientePorId(clienteId)) throw new Error(`Cliente não encontrado com ID: ${clienteId}`);
      if (!dados.hardwares.some((h) => h.numeroSerie === hardwareId)) {
        throw new Error(`Hardware não encontrado com ID: ${hardwareId}`);
      }
      if (tecnicoId != null && !dados.tecnicos.some((t) => t.id === tecnicoId)) {
        throw new Error(`Técnico não encontrado com ID: ${tecnicoId}`);
      }
      const o = {
        id: crypto.randomUUID(),
        valorOrcamento,
        descricao,
        observacoesTecnicas: null,
        dataCriacao: agoraISO(),
        dataAtualizacao: agoraISO(),
        estado: "ABERTA",
        clienteId,
        hardwareId,
        tecnicoId,
        pecasIds: [],
      };
      dados.ordens.push(o);
      salvar();
      return osJSON(o);
    },

    /** PUT /os/{id} com tecnicoId: atribui o técnico e, se a OS estava ABERTA, passa a AGUARDANDO_ORCAMENTO. */
    async assumirOS(id, tecnicoId) {
      const o = osBruta(id);
      if (!dados.tecnicos.some((t) => t.id === tecnicoId)) throw new Error(`Técnico não encontrado com ID: ${tecnicoId}`);
      o.tecnicoId = tecnicoId;
      if (o.estado === "ABERTA") o.estado = "AGUARDANDO_ORCAMENTO";
      o.dataAtualizacao = agoraISO();
      salvar();
      return osJSON(o);
    },

    /** Grava o valor e aplica "orçamentar". Se o estado não permite, nada muda. */
    async enviarOrcamento(id, valor) {
      const o = osBruta(id);
      if (!TRANSICOES.Orçamentar.de.includes(o.estado)) {
        throw new Error("Ação 'Orçamentar' não é permitida neste estado.");
      }
      o.valorOrcamento = valor;
      return transicionar(id, "Orçamentar");
    },

    async aprovarOS(id) {
      return transicionar(id, "Aprovar");
    },

    async cancelarOS(id) {
      return transicionar(id, "Cancelar");
    },

    async finalizarOS(id) {
      return transicionar(id, "Finalizar");
    },

    /** POST /os/{id}/executar: roda o Template Method do tipo de aparelho e devolve o roteiro. */
    async executarOS(id) {
      const o = osBruta(id);
      if (o.estado !== "EM_REPARO") throw new Error("Ação 'Executar' não é permitida neste estado.");
      if (o.tecnicoId == null) {
        throw new Error("Não é possível executar o reparo. Nenhum técnico foi atribuído a esta OS.");
      }
      const tipo = dados.hardwares.find((h) => h.numeroSerie === o.hardwareId).tipoHardware;
      return { os: osJSON(o), roteiro: roteiroDe(tipo) };
    },

    async salvarObservacoes(id, texto) {
      const o = osBruta(id);
      o.observacoesTecnicas = texto;
      o.dataAtualizacao = agoraISO();
      salvar();
      return osJSON(o);
    },

    async excluirOS(id) {
      const o = osBruta(id);
      if (o.estado !== "ABERTA") throw new Error("Não é possível excluir uma OS em andamento.");
      dados.ordens = dados.ordens.filter((x) => x.id !== id);
      salvar();
    },

    async resetDemo() {
      dados = createSeed(now());
      salvar();
    },
  };

  return api;
}
