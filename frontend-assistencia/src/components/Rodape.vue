<script setup>
import { DEMO } from "../services/api.js";
</script>

<template>
  <footer class="rodape">
    <div class="conteudo grade">
      <div>
        <p class="nome">Ponto de Solda</p>
        <p>Rua Siqueira Campos, 480, Centro, Surubim (PE)</p>
        <p>Segunda a sexta, 8h às 18h. Sábado, 8h ao meio-dia.</p>
      </div>
      <div class="creditos">
        <p v-if="DEMO">Loja fictícia. Endereço e pessoas são de exemplo.</p>
        <p>
          Sistema feito por
          <a href="https://marciodev.com/projetos/assistencia-tecnica">Márcio Ferreira Lima</a>
          na disciplina de Programação III da UPE.
        </p>
        <p><a href="https://github.com/MarcioFerreiraL/sistema-assistencia-tecnica">Código no GitHub</a></p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.rodape {
  margin-top: 4rem;
  background: var(--grafite);
  color: var(--papel);
  padding: 2.5rem 0 3rem;
  font-size: 0.92rem;
}
.grade {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(16rem, 1fr));
}
.nome {
  font-stretch: 125%;
  font-weight: 850;
  color: var(--amarelo);
  margin-bottom: 0.4rem;
}
.creditos {
  display: grid;
  gap: 0.4rem;
}
a {
  color: var(--papel);
}
a:hover {
  color: var(--amarelo);
}
:focus-visible {
  outline-color: var(--amarelo);
}
</style>
