<template>
  <ToastContainer />
  <router-view />
</template>

<script setup>
import ToastContainer from './components/ToastContainer.vue';
import { watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

// Títulos Dinâmicos (Melhoria #5)
watch(route, (to) => {
  const titles = {
    'login': 'Login - MarcioTec',
    'cliente': 'Área do Cliente - MarcioTec',
    'tecnico': 'Área Técnica - MarcioTec',
    'atendente': 'Atendimento - MarcioTec',
    'admin': 'Administração - MarcioTec'
  };
  document.title = titles[to.name] || 'MarcioTec Sistema';
}, { immediate: true });
</script>