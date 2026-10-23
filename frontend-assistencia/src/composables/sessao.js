import { computed, reactive } from "vue";

/** Quem entrou: perfil da rota (cliente, atendente, tecnico, admin) e a pessoa devolvida pela API. */
const CHAVE = "ponto-de-solda:sessao";

export const PERFIS = {
  cliente: { api: "clientes", rotulo: "Cliente", inicio: "/cliente" },
  atendente: { api: "atendentes", rotulo: "Atendente", inicio: "/balcao" },
  tecnico: { api: "tecnicos", rotulo: "Técnico", inicio: "/bancada" },
  admin: { api: "administradores", rotulo: "Administração", inicio: "/gerencia" },
};

function ler() {
  try {
    return JSON.parse(localStorage.getItem(CHAVE)) ?? null;
  } catch {
    return null;
  }
}

const estado = reactive({ atual: ler() });

export function useSessao() {
  return {
    sessao: computed(() => estado.atual),
    entrar(perfil, usuario) {
      estado.atual = { perfil, usuario };
      try {
        localStorage.setItem(CHAVE, JSON.stringify(estado.atual));
      } catch {
        // navegador sem armazenamento: a sessão vale só nesta aba
      }
    },
    sair() {
      estado.atual = null;
      try {
        localStorage.removeItem(CHAVE);
      } catch {
        // idem
      }
    },
  };
}
