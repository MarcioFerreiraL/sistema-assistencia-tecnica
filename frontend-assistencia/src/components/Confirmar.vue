<script setup>
import { ref } from "vue";

/** Ação destrutiva em dois toques: o primeiro pergunta, o segundo confirma. */
defineProps({
  rotulo: { type: String, required: true },
  pergunta: { type: String, required: true },
  confirmar: { type: String, default: "Sim, confirmar" },
});
const emit = defineEmits(["confirmado"]);
const perguntando = ref(false);
</script>

<template>
  <button v-if="!perguntando" type="button" class="btn-link" @click="perguntando = true">{{ rotulo }}</button>
  <span v-else class="pergunta" role="group" :aria-label="pergunta">
    <span>{{ pergunta }}</span>
    <button type="button" class="btn btn-contorno" @click="(perguntando = false), emit('confirmado')">{{ confirmar }}</button>
    <button type="button" class="btn-link" @click="perguntando = false">Voltar</button>
  </span>
</template>

<style scoped>
.pergunta {
  display: inline-flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem 0.75rem;
  font-weight: 600;
}
</style>
