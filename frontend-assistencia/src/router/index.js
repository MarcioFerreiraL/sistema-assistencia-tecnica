import { createRouter, createWebHistory } from "vue-router";
import { PERFIS, useSessao } from "../composables/sessao.js";
import Inicio from "../views/Inicio.vue";

const rotas = [
  { path: "/", name: "inicio", component: Inicio, meta: { titulo: "Ponto de Solda, assistência técnica em Surubim" } },
  { path: "/entrar", name: "entrar", component: () => import("../views/Entrar.vue"), meta: { titulo: "Entrar" } },
  { path: "/cliente", name: "cliente", component: () => import("../views/Cliente.vue"), meta: { perfil: "cliente", titulo: "Meus aparelhos" } },
  { path: "/balcao", name: "balcao", component: () => import("../views/Balcao.vue"), meta: { perfil: "atendente", titulo: "Balcão" } },
  { path: "/bancada", name: "bancada", component: () => import("../views/Bancada.vue"), meta: { perfil: "tecnico", titulo: "Bancada" } },
  { path: "/gerencia", name: "gerencia", component: () => import("../views/Gerencia.vue"), meta: { perfil: "admin", titulo: "Gerência" } },
  { path: "/:caminho(.*)*", name: "nao-encontrada", component: () => import("../views/NaoEncontrada.vue"), meta: { titulo: "Página não encontrada" } },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: rotas,
  scrollBehavior: (to) => (to.hash ? { el: to.hash } : { top: 0 }),
});

router.beforeEach((to) => {
  const exige = to.meta.perfil;
  if (!exige) return true;
  const { sessao } = useSessao();
  if (!sessao.value) return { name: "entrar", query: { volta: to.fullPath } };
  if (sessao.value.perfil !== exige) return PERFIS[sessao.value.perfil].inicio;
  return true;
});

router.afterEach((to) => {
  const t = to.meta.titulo;
  document.title = to.name === "inicio" ? t : `${t} · Ponto de Solda`;
});

export default router;
