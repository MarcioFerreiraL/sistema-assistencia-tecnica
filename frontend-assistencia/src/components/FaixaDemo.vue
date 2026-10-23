<script setup>
import { api } from "../services/api.js";
import { useSessao } from "../composables/sessao.js";

const { sair } = useSessao();

async function restaurar() {
  await api.resetDemo();
  sair();
  window.location.assign("/");
}
</script>

<template>
  <div class="faixa" data-faixa-demo>
    <p><strong>Demonstração</strong> com dados fictícios. O que você mudar fica só no seu navegador.</p>
    <div class="links">
      <button type="button" class="btn-faixa" @click="restaurar">Restaurar dados de exemplo</button>
      <a href="https://marciodev.com/projetos/assistencia-tecnica">Sobre o projeto</a>
    </div>
  </div>
</template>

<style scoped>
.faixa {
  background: var(--grafite);
  color: var(--papel);
  font-size: 0.85rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem 1.25rem;
  align-items: center;
  justify-content: space-between;
  padding: 0.4rem max(1rem, (100% - 72rem) / 2);
}
strong {
  color: var(--amarelo);
}
.links {
  display: flex;
  gap: 1.25rem;
}
a,
.btn-faixa {
  color: var(--papel);
  text-decoration: underline;
  text-underline-offset: 3px;
  background: none;
  border: 0;
  padding: 0.5rem 0;
  font-size: inherit;
  cursor: pointer;
}
a:hover,
.btn-faixa:hover {
  color: var(--amarelo);
}
:focus-visible {
  outline-color: var(--amarelo);
}
</style>
