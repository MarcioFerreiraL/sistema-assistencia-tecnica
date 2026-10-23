<script setup>
import Avisos from "./components/Avisos.vue";
import FaixaDemo from "./components/FaixaDemo.vue";
import Rodape from "./components/Rodape.vue";
import Topo from "./components/Topo.vue";
import { DEMO } from "./services/api.js";
</script>

<template>
  <a href="#conteudo" class="pular">Pular para o conteúdo</a>
  <FaixaDemo v-if="DEMO" />
  <Topo />
  <main id="conteudo" tabindex="-1">
    <RouterView />
  </main>
  <Rodape />
  <Avisos />
</template>

<style>
#conteudo:focus {
  outline: none;
}
</style>
