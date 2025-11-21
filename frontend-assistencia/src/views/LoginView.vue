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
      <input 
        type="text" 
        v-model="cpfVisual"
        @keyup.enter="fazerLogin" 
        @input="aplicarMascaraCpf"
        placeholder="000.000.000-00" 
        maxlength="14"
      />

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
const cpfVisual = ref(''); // O que o usuário vê
const cpfLimpo = ref('');  // O que o sistema usa (apenas números)
const erro = ref('');

// Lógica da Máscara
const aplicarMascaraCpf = (event) => {
  let valor = event.target.value.replace(/\D/g, ''); // Remove tudo que não é dígito
  cpfLimpo.value = valor; // Salva o valor puro

  // Aplica a formatação visual
  if (valor.length > 11) valor = valor.slice(0, 11);
  
  valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
  valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
  valor = valor.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
  
  cpfVisual.value = valor;
};

const fazerLogin = async () => {
  if (!cpfLimpo.value) {
    erro.value = "Por favor, digite o CPF.";
    return;
  }

  try {
    // Mapeia os tipos do select para os endpoints corretos do seu backend
    let endpointTipo = tipoUsuario.value;
    if (tipoUsuario.value === 'atendente') endpointTipo = 'atendente'; // Singular no backend
    if (tipoUsuario.value === 'administrador') endpointTipo = 'administrador'; // Singular no backend

    const response = await api.get(`/${endpointTipo}/cpf/${cpfLimpo.value}`);
    
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
.card { display: flex; flex-direction: column; gap: 10px; padding: 20px; border: 1px solid #ccc; border-radius: 8px; width: 300px; background: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
.error { color: red; font-size: 0.9em; text-align: center; }
input, select, button { padding: 10px; border-radius: 4px; border: 1px solid #ddd; }
button { background-color: #2c3e50; color: white; cursor: pointer; font-weight: bold; border: none; }
button:hover { background-color: #34495e; }
</style>