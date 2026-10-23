const FUSO = "America/Recife";

const moeda = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });

export function dinheiro(valor) {
  return moeda.format(valor ?? 0);
}

export function soDigitos(texto) {
  return String(texto ?? "").replace(/\D/g, "");
}

export function cpf(valor) {
  const d = soDigitos(valor).slice(0, 11);
  return d
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

export function data(iso) {
  if (!iso) return "";
  return new Intl.DateTimeFormat("pt-BR", { day: "numeric", month: "short", year: "numeric", timeZone: FUSO }).format(
    new Date(iso),
  );
}

export function dataHora(iso) {
  if (!iso) return "";
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "short", timeStyle: "short", timeZone: FUSO }).format(
    new Date(iso),
  );
}

export function haQuanto(iso, agora = new Date()) {
  const horas = (agora.getTime() - new Date(iso).getTime()) / 3600000;
  if (horas < 1) return "agora há pouco";
  if (horas < 24) return `há ${Math.floor(horas)} ${Math.floor(horas) === 1 ? "hora" : "horas"}`;
  if (horas < 48) return "ontem";
  return `há ${Math.floor(horas / 24)} dias`;
}

const APARELHOS = { NOTEBOOK: "Notebook", CELULAR: "Celular", COMPUTADOR: "Computador", OUTROS: "Outro aparelho" };

export function aparelho(tipo) {
  return APARELHOS[tipo] ?? tipo;
}

export const TIPOS_APARELHO = Object.keys(APARELHOS);

export function primeiroNome(nome) {
  return String(nome ?? "").split(" ")[0];
}

export function iniciais(nome) {
  const partes = String(nome ?? "").split(" ").filter(Boolean);
  return ((partes[0]?.[0] ?? "") + (partes.length > 1 ? partes.at(-1)[0] : "")).toUpperCase();
}
