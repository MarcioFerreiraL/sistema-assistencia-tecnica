/** Estados da OS (EstadoOSEnum) em linguagem de balcão. */

/** As cinco etapas que a etiqueta mostra, na ordem do padrão State. Cancelada fica fora. */
export const ETAPAS = [
  { estado: "ABERTA", etapa: "Recebido" },
  { estado: "AGUARDANDO_ORCAMENTO", etapa: "Diagnóstico" },
  { estado: "AGUARDANDO_APROVACAO", etapa: "Orçamento" },
  { estado: "EM_REPARO", etapa: "Reparo" },
  { estado: "FINALIZADA", etapa: "Pronto" },
];

const ROTULOS = {
  ABERTA: "Na fila",
  AGUARDANDO_ORCAMENTO: "Em diagnóstico",
  AGUARDANDO_APROVACAO: "Orçamento enviado",
  EM_REPARO: "Em reparo",
  FINALIZADA: "Pronto",
  CANCELADA: "Cancelada",
};

export const ESTADOS = Object.keys(ROTULOS);

export function rotuloEstado(estado, perfil) {
  if (perfil === "cliente" && estado === "AGUARDANDO_APROVACAO") return "Esperando sua aprovação";
  return ROTULOS[estado] ?? estado;
}

export function etapaAtual(estado) {
  return ETAPAS.findIndex((e) => e.estado === estado);
}

/** Código curto para falar no balcão: os 6 primeiros caracteres do UUID. */
export function codigoOS(id) {
  return String(id).replace(/-/g, "").slice(0, 6).toUpperCase();
}
