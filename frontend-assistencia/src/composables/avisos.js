import { reactive } from "vue";

/** Avisos curtos no canto da tela. Somem sozinhos em 5 s; erro fica até 8 s. */
const estado = reactive({ itens: [] });
let seq = 0;

function remover(id) {
  const i = estado.itens.findIndex((a) => a.id === id);
  if (i > -1) estado.itens.splice(i, 1);
}

function avisar(texto, tipo) {
  const id = ++seq;
  estado.itens.push({ id, texto, tipo });
  setTimeout(() => remover(id), tipo === "erro" ? 8000 : 5000);
}

export function useAvisos() {
  return {
    itens: estado.itens,
    remover,
    ok: (t) => avisar(t, "ok"),
    erro: (e) => avisar(e instanceof Error ? e.message : String(e), "erro"),
  };
}
