<script setup>
import { useAvisos } from "../composables/avisos.js";

const { itens, remover } = useAvisos();
</script>

<template>
  <div class="avisos" role="status" aria-live="polite">
    <TransitionGroup name="aviso">
      <div v-for="a in itens" :key="a.id" class="aviso" :class="a.tipo" :data-aviso="a.tipo">
        <p>{{ a.texto }}</p>
        <button type="button" class="fechar" aria-label="Fechar aviso" @click="remover(a.id)">×</button>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.avisos {
  position: fixed;
  z-index: 40;
  right: 1rem;
  bottom: 1rem;
  left: 1rem;
  display: grid;
  justify-items: end;
  gap: 0.5rem;
  pointer-events: none;
}
.aviso {
  pointer-events: auto;
  display: flex;
  gap: 0.75rem;
  align-items: flex-start;
  max-width: 26rem;
  background: var(--grafite);
  color: var(--papel);
  border-left: 6px solid var(--amarelo);
  border-radius: var(--raio);
  padding: 0.75rem 0.5rem 0.75rem 1rem;
  box-shadow: 0 10px 24px -12px rgb(0 0 0 / 0.5);
}
.aviso.erro {
  border-left-color: #ff8a7a;
}
.fechar {
  background: none;
  border: 0;
  color: inherit;
  font-size: 1.4rem;
  line-height: 1;
  min-width: 2rem;
  min-height: 2rem;
  cursor: pointer;
}
.aviso-enter-from,
.aviso-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
.aviso-enter-active,
.aviso-leave-active {
  transition: all 0.2s ease;
}
</style>
