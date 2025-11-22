import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'login', component: LoginView },
    { path: '/cliente', name: 'cliente', component: () => import('../views/ClienteDashboard.vue') },
    { path: '/tecnico', name: 'tecnico', component: () => import('../views/TecnicoDashboard.vue') },
    { path: '/atendente', name: 'atendente', component: () => import('../views/AtendenteDashboard.vue') },
    { path: '/admin', name: 'admin', component: () => import('../views/AdminDashboard.vue') }
  ]
})

// Guarda de rota simples
router.beforeEach((to, from, next) => {
  const usuario = localStorage.getItem('usuario');
  if (to.name !== 'login' && !usuario) next({ name: 'login' });
  else next();
})

export default router