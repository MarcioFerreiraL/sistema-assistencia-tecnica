<script setup>
import { computed } from "vue";
import Confirmar from "../components/Confirmar.vue";
import Etiqueta from "../components/Etiqueta.vue";
import { useAvisos } from "../composables/avisos.js";
import { useCarga } from "../composables/carga.js";
import { useSessao } from "../composables/sessao.js";
import { codigoOS } from "../lib/estados.js";
import { dinheiro, primeiroNome } from "../lib/formato.js";
import { api } from "../services/api.js";

const { sessao } = useSessao();
const avisos = useAvisos();
const eu = sessao.value.usuario;

const { dados, erro, carregando, recarregar } = useCarga(() => api.listarOSDoCliente(eu.id));

const recentes = (lista) => [...lista].sort((a, b) => b.dataCriacao.localeCompare(a.dataCriacao));
const esperando = computed(() => recentes(dados.value ?? []).filter((o) => o.estado === "AGUARDANDO_APROVACAO"));
const andamento = computed(() =>
  recentes(dados.value ?? []).filter((o) => ["ABERTA", "AGUARDANDO_ORCAMENTO", "EM_REPARO"].includes(o.estado)),
);
const historico = computed(() => recentes(dados.value ?? []).filter((o) => ["FINALIZADA", "CANCELADA"].includes(o.estado)));
const cancelavel = (o) => ["ABERTA", "AGUARDANDO_ORCAMENTO", "EM_REPARO"].includes(o.estado);

async function aprovar(os) {
  try {
    await api.aprovarOS(os.id);
    avisos.ok(`Orçamento aprovado. A OS ${codigoOS(os.id)} foi para o reparo.`);
  } catch (e) {
    avisos.erro(e);
  }
  recarregar();
}

async function cancelar(os, texto) {
  try {
    await api.cancelarOS(os.id);
    avisos.ok(texto ?? `OS ${codigoOS(os.id)} cancelada.`);
  } catch (e) {
    avisos.erro(e);
  }
  recarregar();
}
</script>

<template>
  <div class="conteudo pagina">
    <header class="cabeca">
      <h1 class="titulo titulo-2">Olá, {{ primeiroNome(eu.nome) }}</h1>
      <p class="fino">Seus aparelhos no Ponto de Solda.</p>
    </header>

    <p v-if="carregando" class="fino">Carregando…</p>
    <p v-else-if="erro" role="alert">{{ erro }}</p>
    <template v-else>
      <section v-if="esperando.length" class="pilha" aria-labelledby="t-esperando">
        <h2 id="t-esperando" class="titulo titulo-3">Esperando sua resposta</h2>
        <Etiqueta v-for="os in esperando" :key="os.id" :os="os" perfil="cliente" data-esperando>
          <p class="pergunta">
            O conserto sai por <strong>{{ dinheiro(os.valorOrcamento) }}</strong>. Podemos fazer?
          </p>
          <button type="button" class="btn btn-primario" data-aprovar @click="aprovar(os)">Aprovar orçamento</button>
          <Confirmar
            rotulo="Recusar orçamento"
            pergunta="Recusar e cancelar a OS?"
            confirmar="Sim, recusar"
            @confirmado="cancelar(os, `Orçamento recusado. A OS ${codigoOS(os.id)} foi cancelada.`)"
          />
        </Etiqueta>
      </section>

      <section class="pilha" aria-labelledby="t-andamento">
        <h2 id="t-andamento" class="titulo titulo-3">Em andamento</h2>
        <p v-if="!andamento.length" class="fino">Nada na bancada agora. Para deixar um aparelho, é só passar no balcão.</p>
        <Etiqueta v-for="os in andamento" :key="os.id" :os="os" perfil="cliente">
          <Confirmar
            v-if="cancelavel(os)"
            rotulo="Cancelar esta OS"
            pergunta="Cancelar o conserto?"
            confirmar="Sim, cancelar"
            @confirmado="cancelar(os)"
          />
        </Etiqueta>
      </section>

      <section v-if="historico.length" class="pilha" aria-labelledby="t-historico">
        <h2 id="t-historico" class="titulo titulo-3">Histórico</h2>
        <Etiqueta v-for="os in historico" :key="os.id" :os="os" perfil="cliente" />
      </section>
    </template>
  </div>
</template>

<style scoped>
.pagina {
  display: grid;
  gap: 2.25rem;
  padding-block: 2.5rem;
  max-width: 44rem;
}
.cabeca {
  display: grid;
  gap: 0.3rem;
}
.pergunta {
  flex-basis: 100%;
}
</style>
