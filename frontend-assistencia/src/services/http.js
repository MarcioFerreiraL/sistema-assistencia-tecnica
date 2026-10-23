/**
 * Cliente da API Spring Boot. Mesmas funções do backend de demonstração (./demo/api.js).
 * Erros de regra de negócio chegam como texto no corpo (ManipuladorExcecoesGlobal).
 */
import axios from "axios";
import { roteiroDe } from "./demo/roteiros.js";

const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

http.interceptors.response.use(
  (r) => r,
  (err) => {
    const corpo = err.response?.data;
    const msg = typeof corpo === "string" && corpo ? corpo : corpo?.message || "Não foi possível falar com o servidor.";
    return Promise.reject(new Error(msg));
  },
);

const ROTAS_PESSOA = {
  clientes: "/clientes",
  tecnicos: "/tecnicos",
  atendentes: "/atendente",
  administradores: "/administrador",
};

const dados = (p) => p.then((r) => r.data);

/** Corpo do PUT /os/{id} (OrdemServicoDto) a partir da OS atual. */
function dto(os, mudancas) {
  return {
    valorOrcamento: os.valorOrcamento,
    descricao: os.descricao,
    clienteId: os.cliente.id,
    hardwareId: os.hardware.numeroSerie,
    tecnicoId: os.tecnicoResponsavel?.id ?? null,
    ...mudancas,
  };
}

export function createHttpApi() {
  return {
    listarPessoas: (perfil) => dados(http.get(ROTAS_PESSOA[perfil])),
    buscarPorCpf: (perfil, cpf) => dados(http.get(`${ROTAS_PESSOA[perfil]}/cpf/${cpf}`)),
    criarPessoa: (perfil, p) => dados(http.post(ROTAS_PESSOA[perfil], p)),
    excluirPessoa: (perfil, id) => dados(http.delete(`${ROTAS_PESSOA[perfil]}/${id}`)),

    listarHardwares: () => dados(http.get("/hardware")),
    criarHardware: (h) => dados(http.post("/hardware", h)),

    async listarPecas() {
      const [pecas, ordens] = await Promise.all([dados(http.get("/pecas")), dados(http.get("/os"))]);
      return pecas.map((p) => ({
        ...p,
        usos: ordens.filter((o) => o.pecasUtilizadas?.some((u) => u.numeroSerie === p.numeroSerie)).length,
      }));
    },
    criarPeca: (p) => dados(http.post("/pecas", p)),
    excluirPeca: (id) => dados(http.delete(`/pecas/${id}`)),

    listarOS: () => dados(http.get("/os")),
    buscarOS: (id) => dados(http.get(`/os/${id}`)),
    listarOSDoCliente: (id) => dados(http.get(`/os/cliente/${id}`)),
    listarOSDoTecnico: (id) => dados(http.get(`/os/tecnico/${id}`)),
    listarOSPorEstado: (estado) => dados(http.get(`/os/estado/${estado}`)),
    criarOS: (os) => dados(http.post("/os", { valorOrcamento: 0, ...os })),

    async assumirOS(id, tecnicoId) {
      const os = await dados(http.get(`/os/${id}`));
      return dados(http.put(`/os/${id}`, dto(os, { tecnicoId })));
    },
    async enviarOrcamento(id, valor) {
      const os = await dados(http.get(`/os/${id}`));
      await http.put(`/os/${id}`, dto(os, { valorOrcamento: valor }));
      return dados(http.post(`/os/${id}/orcamentar`));
    },
    aprovarOS: (id) => dados(http.post(`/os/${id}/aprovar`)),
    cancelarOS: (id) => dados(http.post(`/os/${id}/cancelar`)),
    finalizarOS: (id) => dados(http.post(`/os/${id}/finalizar`)),
    async executarOS(id) {
      const os = await dados(http.post(`/os/${id}/executar`));
      return { os, roteiro: roteiroDe(os.hardware.tipoHardware) };
    },
    salvarObservacoes: (id, texto) =>
      dados(http.patch(`/os/${id}/observacoes`, texto, { headers: { "Content-Type": "text/plain" } })),
    excluirOS: (id) => dados(http.delete(`/os/${id}`)),

    async resetDemo() {},
  };
}
