/**
 * Ponto único de acesso aos dados. Com VITE_DEMO=1 o frontend usa o backend de demonstração
 * no navegador; sem ela, fala com a API Spring Boot.
 */
import { createDemoApi } from "./demo/api.js";
import { createHttpApi } from "./http.js";

export const DEMO = import.meta.env.VITE_DEMO === "1";
export { DEMO_USERS } from "./demo/seed.js";

function armazenamento() {
  try {
    const k = "__teste__";
    localStorage.setItem(k, k);
    localStorage.removeItem(k);
    return localStorage;
  } catch {
    const m = new Map();
    return { getItem: (k) => m.get(k) ?? null, setItem: (k, v) => m.set(k, String(v)), removeItem: (k) => m.delete(k) };
  }
}

export const api = DEMO ? createDemoApi({ storage: armazenamento() }) : createHttpApi();
