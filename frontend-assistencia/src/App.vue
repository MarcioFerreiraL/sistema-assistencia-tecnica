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
    'login': 'Login - TechRepair',
    'cliente': 'Área do Cliente - TechRepair',
    'tecnico': 'Área Técnica - TechRepair',
    'atendente': 'Atendimento - TechRepair',
    'admin': 'Administração - TechRepair'
  };
  document.title = titles[to.name] || 'TechRepair Sistema';
}, { immediate: true });
</script>