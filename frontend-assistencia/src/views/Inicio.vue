<script setup>
import Etiqueta from "../components/Etiqueta.vue";
import { roteiroDe } from "../services/demo/roteiros.js";
import { DEMO } from "../services/api.js";

const agora = new Date();
const exemplo = {
  id: "a41f07c2-0000-4000-8000-000000000000",
  descricao: "Tela trincada no canto, touch falhando embaixo.",
  estado: "EM_REPARO",
  valorOrcamento: 280,
  dataCriacao: new Date(agora.getTime() - 26 * 3600 * 1000).toISOString(),
  cliente: { nome: "Rita Araújo" },
  hardware: { tipoHardware: "CELULAR" },
  tecnicoResponsavel: { nome: "Severino Lopes" },
};

const caminho = [
  { etapa: "Recebido", texto: "No balcão, a atendente anota o defeito e seus dados e prende a etiqueta no aparelho." },
  { etapa: "Diagnóstico", texto: "Um técnico assume a ordem de serviço e abre o aparelho para achar a causa." },
  { etapa: "Orçamento", texto: "Você recebe o valor e decide. Sem a sua aprovação, nenhuma peça é trocada." },
  { etapa: "Reparo", texto: "O técnico segue o roteiro do tipo de aparelho, do diagnóstico à limpeza final." },
  { etapa: "Pronto", texto: "Testado e limpo. É só passar no balcão para buscar." },
];

const bancada = [
  { tipo: "CELULAR", nome: "Celular" },
  { tipo: "NOTEBOOK", nome: "Notebook" },
  { tipo: "COMPUTADOR", nome: "Computador" },
  { tipo: "OUTROS", nome: "E o que mais tiver placa" },
].map((b) => ({ ...b, olhamos: roteiroDe(b.tipo)[0].passos }));
</script>

<template>
  <section class="abertura conteudo">
    <div class="texto">
      <h1 class="titulo titulo-1">Conserto que você acompanha furo por furo.</h1>
      <p class="sub">
        Todo aparelho que entra no Ponto de Solda ganha uma etiqueta numerada. Você acompanha cada etapa pelo CPF
        e só autoriza o reparo depois de ver o orçamento.
      </p>
      <div class="ctas">
        <RouterLink to="/entrar" class="btn btn-primario">Acompanhar meu aparelho</RouterLink>
        <a href="#caminho" class="btn btn-contorno">Como funciona</a>
      </div>
      <p v-if="DEMO" class="ajuda">
        Na demonstração, dá para entrar como cliente, atendente, técnico ou dono da loja com um clique.
      </p>
    </div>
    <div class="vitrine" aria-label="Exemplo de etiqueta de ordem de serviço" role="img">
      <span class="barbante" aria-hidden="true"></span>
      <Etiqueta :os="exemplo" :agora="agora" class="exemplo" aria-hidden="true" />
    </div>
  </section>

  <section id="caminho" class="conteudo bloco">
    <h2 class="titulo titulo-2">O caminho do seu aparelho</h2>
    <ol class="caminho">
      <li v-for="(c, i) in caminho" :key="c.etapa">
        <span class="numero" aria-hidden="true">{{ i + 1 }}</span>
        <h3 class="titulo titulo-3">{{ c.etapa }}</h3>
        <p>{{ c.texto }}</p>
      </li>
    </ol>
    <p class="fino nota">Mudou de ideia? Dá para cancelar a qualquer momento antes de o reparo terminar.</p>
  </section>

  <section class="conteudo bloco">
    <h2 class="titulo titulo-2">O que entra na bancada</h2>
    <p class="fino intro">Cada tipo de aparelho tem o seu roteiro. Isto é o que olhamos primeiro:</p>
    <ul class="bancada">
      <li v-for="b in bancada" :key="b.tipo" class="folha">
        <h3 class="titulo titulo-3">{{ b.nome }}</h3>
        <ul class="passos">
          <li v-for="p in b.olhamos" :key="p">{{ p }}</li>
        </ul>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.abertura {
  display: grid;
  gap: 2.5rem;
  align-items: center;
  padding-block: clamp(2rem, 6vw, 4.5rem);
}
@media (min-width: 56rem) {
  .abertura {
    grid-template-columns: 1.1fr 1fr;
  }
}
.texto {
  display: grid;
  gap: 1.25rem;
}
.sub {
  font-size: 1.15rem;
  max-width: 36rem;
}
.ctas {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.vitrine {
  position: relative;
  padding-top: 2.5rem;
}
.vitrine::after {
  /* nó do barbante sobre o ilhós */
  content: "";
  position: absolute;
  z-index: 1;
  left: calc(1.25rem - 3px);
  top: calc(50% + 1.25rem - 4px);
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #8c7a4c;
}
/* Barbante: sai do ilhós (meio da borda esquerda) e sobe para fora do quadro. */
.barbante {
  position: absolute;
  top: 0;
  bottom: calc(50% - 1.25rem);
  left: 1.25rem;
  width: 2px;
  background: repeating-linear-gradient(to bottom, #b9a46f 0 3px, #8c7a4c 3px 6px);
  transform: rotate(6deg);
  transform-origin: bottom;
  z-index: 1;
}
.exemplo {
  transform: rotate(-1deg);
}
@media (min-width: 56rem) {
  .vitrine {
    font-size: 1.12rem;
  }
  .exemplo {
    transform: rotate(-2deg);
  }
}
/* O único movimento da página: os furos marcados um a um até a etapa atual. */
.exemplo :deep(.feito .furo),
.exemplo :deep(.atual .furo) {
  animation: furar 0.25s ease-out both;
}
.exemplo :deep(li:nth-child(2) .furo) {
  animation-delay: 0.35s;
}
.exemplo :deep(li:nth-child(3) .furo) {
  animation-delay: 0.7s;
}
.exemplo :deep(li:nth-child(4) .furo) {
  animation-delay: 1.05s;
}
@keyframes furar {
  from {
    background: var(--papel-claro);
    border-color: var(--linha);
    transform: scale(1.5);
  }
}

.bloco {
  padding-block: 2.5rem;
  border-top: 1px solid var(--linha);
}
.caminho {
  list-style: none;
  margin: 1.5rem 0 0;
  padding: 0;
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(12rem, 1fr));
}
.caminho li {
  display: grid;
  gap: 0.4rem;
  align-content: start;
}
.numero {
  display: inline-grid;
  place-items: center;
  width: 2.25rem;
  height: 2.25rem;
  border-radius: 50%;
  background: var(--grafite);
  color: var(--amarelo);
  font-weight: 800;
}
.nota {
  margin-top: 1.5rem;
}
.intro {
  margin-top: 0.5rem;
}
.bancada {
  list-style: none;
  margin: 1.5rem 0 0;
  padding: 0;
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(15rem, 1fr));
}
.passos {
  margin: 0.6rem 0 0;
  padding-left: 1.1rem;
  color: var(--apagado);
  display: grid;
  gap: 0.3rem;
}
</style>
