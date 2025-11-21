<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="logo-area">
        <div class="icon-circle">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>
        </div>
        <h1>TechRepair</h1>
        <p>Sistema de Gestão de Assistência</p>
      </div>

      <form @submit.prevent="fazerLogin">
        <div class="form-group">
          <label>Eu sou:</label>
          <select v-model="tipoUsuario">
            <option value="clientes">Cliente</option>
            <option value="tecnicos">Técnico</option>
            <option value="atendente">Atendente</option>
            <option value="administrador">Administrador</option>
          </select>
        </div>

        <div class="form-group">
          <label>CPF:</label>
          <input 
            type="text" 
            v-model="cpfVisual"
            @input="aplicarMascaraCpf"
            placeholder="000.000.000-00" 
            maxlength="14"
            :class="{ 'input-error': erro }"
          />
        </div>

        <button type="submit" class="btn-primary w-full" :disabled="loading">
          <span v-if="loading">Entrando...</span>
          <span v-else>Acessar Sistema</span>
        </button>

        <div v-if="erro" class="error-message">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
          {{ erro }}
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const tipoUsuario = ref('clientes');
const cpfVisual = ref('');
const cpfLimpo = ref('');
const erro = ref('');
const loading = ref(false);

const aplicarMascaraCpf = (event) => {
  let valor = event.target.value.replace(/\D/g, '');
  cpfLimpo.value = valor;
  if (valor.length > 11) valor = valor.slice(0, 11);
  valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
  valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
  valor = valor.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
  cpfVisual.value = valor;
  erro.value = ''; // Limpa erro ao digitar
};

const fazerLogin = async () => {
  if (!cpfLimpo.value) {
    erro.value = "Por favor, digite o CPF.";
    return;
  }
  
  loading.value = true;
  erro.value = '';

  try {
    let endpointTipo = tipoUsuario.value;
    if (tipoUsuario.value === 'atendente') endpointTipo = 'atendente';
    if (tipoUsuario.value === 'administrador') endpointTipo = 'administrador';

    const response = await api.get(`/${endpointTipo}/cpf/${cpfLimpo.value}`);
    
    localStorage.setItem('usuario', JSON.stringify(response.data));
    localStorage.setItem('tipo', tipoUsuario.value);

    const rotas = {
      clientes: '/cliente',
      tecnicos: '/tecnico',
      atendente: '/atendente',
      administrador: '/admin'
    };
    router.push(rotas[tipoUsuario.value]);

  } catch (e) {
    erro.value = "Usuário não encontrado. Verifique o CPF e o tipo de conta.";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}

.login-card {
  background: var(--color-surface);
  padding: 2.5rem;
  border-radius: 16px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  border: 1px solid var(--color-border);
}

.logo-area {
  text-align: center;
  margin-bottom: 2rem;
}

.icon-circle {
  width: 56px;
  height: 56px;
  background: #eff6ff;
  color: var(--color-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
}

h1 {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-text-main);
  margin-bottom: 0.25rem;
}

.logo-area p {
  color: var(--color-text-muted);
  font-size: 0.9rem;
}

.w-full { width: 100%; }

.error-message {
  margin-top: 1rem;
  padding: 0.75rem;
  border-radius: var(--radius);
  background-color: #fef2f2;
  color: var(--color-danger);
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  border: 1px solid #fee2e2;
}

.input-error {
  border-color: var(--color-danger);
}
</style>