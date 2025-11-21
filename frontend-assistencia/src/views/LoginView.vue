<template>
  <div class="login-container">
    <h1>Login Assistência Técnica</h1>
    <div class="card">
      <label>Tipo de Usuário:</label>
      <select v-model="tipoUsuario">
        <option value="clientes">Cliente</option>
        <option value="tecnicos">Técnico</option>
        <option value="atendente">Atendente</option>
        <option value="administrador">Administrador</option>
      </select>

      <label>CPF:</label>
      <input type="text" v-model="cpf" placeholder="Digite apenas números" />

      <button @click="fazerLogin">Entrar</button>
      <p v-if="erro" class="error">{{ erro }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const tipoUsuario = ref('clientes');
const cpf = ref('');
const erro = ref('');

const fazerLogin = async () => {
  try {
    // A URL muda baseada no tipo selecionado (ex: /api/clientes/cpf/123)
    const response = await api.get(`/${tipoUsuario.value}/cpf/${cpf.value}`);
    
    // Salva sessão
    localStorage.setItem('usuario', JSON.stringify(response.data));
    localStorage.setItem('tipo', tipoUsuario.value);

    // Redirecionamento
    if (tipoUsuario.value === 'clientes') router.push('/cliente');
    else if (tipoUsuario.value === 'tecnicos') router.push('/tecnico');
    else if (tipoUsuario.value === 'atendente') router.push('/atendente');
    else if (tipoUsuario.value === 'administrador') router.push('/admin');

  } catch (e) {
    erro.value = "Usuário não encontrado ou erro no servidor.";
    console.error(e);
  }
};
</script>

<style scoped>
.login-container { display: flex; flex-direction: column; align-items: center; margin-top: 50px; }
.card { display: flex; flex-direction: column; gap: 10px; padding: 20px; border: 1px solid #ccc; border-radius: 8px; width: 300px; }
.error { color: red; }
</style>