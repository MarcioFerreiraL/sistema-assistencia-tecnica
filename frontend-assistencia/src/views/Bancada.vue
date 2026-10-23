<script setup>
import { computed, reactive, watch } from "vue";
import Confirmar from "../components/Confirmar.vue";
import Etiqueta from "../components/Etiqueta.vue";
import { useAvisos } from "../composables/avisos.js";
import { useCarga } from "../composables/carga.js";
import { useSessao } from "../composables/sessao.js";
import { codigoOS } from "../lib/estados.js";
import { primeiroNome } from "../lib/formato.js";
import { api } from "../services/api.js";

const { sessao } = useSessao();
const eu = sessao.value.usuario;
const avisos = useAvisos();
const agora = new Date();

const { dados, erro, carregando, recarregar } = useCarga(async () => {
  const [fila, minhas] = await Promise.all([api.listarOSPorEstado("ABERTA"), api.listarOSDoTecnico(eu.id)]);
  return { fila, minhas };
});

const antigas = (l) => [...l].sort((a, b) => a.dataCriacao.localeCompare(b.dataCriacao));
const fila = computed(() => antigas(dados.value?.fila ?? []));
const doEstado = (estado) => computed(() => antigas((dados.value?.minhas ?? []).filter((o) => o.estado === estado)));
const diagnostico = doEstado("AGUARDANDO_ORCAMENTO");
const reparo = doEstado("EM_REPARO");
const esperando = doEstado("AGUARDANDO_APROVACAO");
const concluidas = computed(() =>
  (dados.value?.minhas ?? [])
    .filter((o) => o.estado === "FINALIZADA")
    .sort((a, b) => b.dataAtualizacao.localeCompare(a.dataAtualizacao))
    .slice(0, 3),
);

/** Rascunhos por OS: valor e laudo do orçamento; roteiro e passos marcados do reparo. */
const rascunho = reactive({});
watch(dados, (d) => {
  for (const os of [...(d?.fila ?? []), ...(d?.minhas ?? [])]) {
    rascunho[os.id] ??= { valor: "", laudo: os.observacoesTecnicas ?? "", roteiro: null, feitos: 0, ocupado: false };
  }
});
const de = (os) => rascunho[os.id];

async function agir(os, fazer, mensagem) {
  const r = de(os);
  r.ocupado = true;
  try {
    await fazer();
    if (mensagem) avisos.ok(mensagem);
    await recarregar();
  } catch (e) {
    avisos.erro(e);
  } finally {
    r.ocupado = false;
  }
}

function assumir(os) {
  agir(os, () => api.assumirOS(os.id, eu.id), `OS ${codigoOS(os.id)} na sua bancada.`);
}

function enviarOrcamento(os) {
  const r = de(os);
  const valor = Number(String(r.valor).replace(",", "."));
  if (!(valor > 0)) {
    avisos.erro("Informe o valor do conserto, maior que zero.");
    return;
  }
  if (!r.laudo.trim()) {
    avisos.erro("Escreva o diagnóstico para o cliente entender o orçamento.");
    return;
  }
  agir(
    os,
    async () => {
      await api.salvarObservacoes(os.id, r.laudo.trim());
      await api.enviarOrcamento(os.id, valor);
    },
    `Orçamento da OS ${codigoOS(os.id)} enviado ao cliente.`,
  );
}

async function iniciarRoteiro(os) {
  const r = de(os);
  try {
    r.roteiro = (await api.executarOS(os.id)).roteiro;
    r.feitos = 0;
  } catch (e) {
    avisos.erro(e);
  }
}

function salvarLaudo(os) {
  agir(os, () => api.salvarObservacoes(os.id, de(os).laudo.trim()), "Laudo salvo.");
}

function finalizar(os) {
  agir(
    os,
    async () => {
      await api.salvarObservacoes(os.id, de(os).laudo.trim());
      await api.finalizarOS(os.id);
      delete rascunho[os.id];
    },
    `OS ${codigoOS(os.id)} pronta. O cliente já vê como "Pronto".`,
  );
}

function cancelar(os) {
  agir(os, () => api.cancelarOS(os.id), `OS ${codigoOS(os.id)} cancelada.`);
}
</script>

<template>
  <div class="conteudo pagina">
    <header class="cabeca">
      <h1 class="titulo titulo-2">Bancada de {{ primeiroNome(eu.nome) }}</h1>
      <p class="fino">A fila do balcão fica à esquerda; o que já está com você, à direita.</p>
    </header>

    <p v-if="carregando" class="fino">Carregando…</p>
    <p v-else-if="erro" role="alert">{{ erro }}</p>
    <div v-else class="colunas">
      <section class="pilha" aria-labelledby="t-fila" data-fila>
        <h2 id="t-fila" class="titulo titulo-3">Fila do balcão <span class="conta">{{ fila.length }}</span></h2>
        <p v-if="!fila.length" class="fino">Fila vazia. Bom momento para fechar os reparos em andamento.</p>
        <Etiqueta v-for="os in fila" :key="os.id" :os="os" perfil="tecnico" :agora="agora">
          <button type="button" class="btn btn-grafite" :disabled="de(os).ocupado" data-assumir @click="assumir(os)">
            Assumir
          </button>
        </Etiqueta>
      </section>

      <div class="pilha minhas">
        <section v-if="diagnostico.length" class="pilha" aria-labelledby="t-diag">
          <h2 id="t-diag" class="titulo titulo-3">Para orçar</h2>
          <Etiqueta v-for="os in diagnostico" :key="os.id" :os="os" perfil="tecnico" :agora="agora">
            <form class="pilha cheio" novalidate @submit.prevent="enviarOrcamento(os)">
              <div class="campo">
                <label :for="`laudo-${os.id}`" class="rotulo">Diagnóstico</label>
                <textarea :id="`laudo-${os.id}`" v-model="de(os).laudo" class="entrada" placeholder="O que você encontrou e o que precisa trocar."></textarea>
              </div>
              <div class="linha-orcamento">
                <div class="campo valor">
                  <label :for="`valor-${os.id}`" class="rotulo">Valor (R$)</label>
                  <input :id="`valor-${os.id}`" v-model="de(os).valor" class="entrada" inputmode="decimal" placeholder="0,00" />
                </div>
                <button type="submit" class="btn btn-primario" :disabled="de(os).ocupado" data-enviar-orcamento>
                  Enviar orçamento
                </button>
              </div>
            </form>
          </Etiqueta>
        </section>

        <section v-if="reparo.length" class="pilha" aria-labelledby="t-reparo">
          <h2 id="t-reparo" class="titulo titulo-3">Em reparo</h2>
          <Etiqueta v-for="os in reparo" :key="os.id" :os="os" perfil="tecnico" :agora="agora">
            <div class="pilha cheio">
              <button v-if="!de(os).roteiro" type="button" class="btn btn-grafite" data-iniciar-roteiro @click="iniciarRoteiro(os)">
                Abrir roteiro de reparo
              </button>
              <ol v-else class="roteiro" data-roteiro>
                <li v-for="(etapa, i) in de(os).roteiro" :key="etapa.etapa" :class="{ feita: i < de(os).feitos }">
                  <label class="etapa">
                    <input
                      type="checkbox"
                      :checked="i < de(os).feitos"
                      :disabled="i !== de(os).feitos && i !== de(os).feitos - 1"
                      @change="de(os).feitos = $event.target.checked ? i + 1 : i"
                    />
                    <span>
                      <strong>{{ etapa.etapa }}</strong>
                      <span v-for="p in etapa.passos" :key="p" class="passo">{{ p }}</span>
                    </span>
                  </label>
                </li>
              </ol>
              <p v-if="de(os).roteiro && de(os).feitos < 5" class="ajuda">
                As etapas seguem a ordem do roteiro. Marque cada uma ao terminar.
              </p>
              <div class="campo">
                <label :for="`laudo-${os.id}`" class="rotulo">Laudo técnico</label>
                <textarea :id="`laudo-${os.id}`" v-model="de(os).laudo" class="entrada"></textarea>
              </div>
              <div class="botoes">
                <button
                  type="button"
                  class="btn btn-primario"
                  :disabled="de(os).ocupado || !de(os).roteiro || de(os).feitos < 5"
                  data-finalizar
                  @click="finalizar(os)"
                >
                  Finalizar reparo
                </button>
                <button type="button" class="btn btn-contorno" :disabled="de(os).ocupado" @click="salvarLaudo(os)">
                  Salvar laudo
                </button>
                <Confirmar rotulo="Cancelar OS" pergunta="Cancelar este reparo?" confirmar="Sim, cancelar" @confirmado="cancelar(os)" />
              </div>
            </div>
          </Etiqueta>
        </section>

        <section v-if="esperando.length" class="pilha" aria-labelledby="t-esperando">
          <h2 id="t-esperando" class="titulo titulo-3">Esperando o cliente</h2>
          <Etiqueta v-for="os in esperando" :key="os.id" :os="os" perfil="tecnico" :agora="agora" />
        </section>

        <p v-if="!diagnostico.length && !reparo.length && !esperando.length" class="fino">
          Nada na sua bancada. Assuma uma OS da fila para começar.
        </p>

        <section v-if="concluidas.length" class="pilha" aria-labelledby="t-prontas">
          <h2 id="t-prontas" class="titulo titulo-3">Prontas por você</h2>
          <Etiqueta v-for="os in concluidas" :key="os.id" :os="os" perfil="tecnico" :agora="agora" />
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pagina {
  display: grid;
  gap: 1.5rem;
  padding-block: 2.5rem;
}
.cabeca {
  display: grid;
  gap: 0.3rem;
}
.colunas {
  display: grid;
  gap: 2.5rem;
  align-items: start;
}
@media (min-width: 62rem) {
  .colunas {
    grid-template-columns: 21rem 1fr;
  }
}
.minhas {
  gap: 2rem;
}
.conta {
  display: inline-grid;
  place-items: center;
  min-width: 1.8rem;
  height: 1.8rem;
  margin-left: 0.3rem;
  border-radius: 999px;
  background: var(--grafite);
  color: var(--amarelo);
  font-size: 0.9rem;
  font-stretch: 100%;
  vertical-align: middle;
}
.cheio {
  flex-basis: 100%;
}
.linha-orcamento {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: end;
}
.valor {
  width: 9rem;
}
.botoes {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
  align-items: center;
}
.roteiro {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 0.4rem;
}
.etapa {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 0.75rem;
  padding: 0.6rem 0.75rem;
  border: 2px solid var(--linha);
  border-radius: var(--raio);
  background: var(--branco);
  cursor: pointer;
}
.etapa input {
  width: 1.25rem;
  height: 1.25rem;
  margin-top: 0.15rem;
  accent-color: var(--grafite);
}
.etapa input:disabled {
  cursor: not-allowed;
}
.etapa > span {
  display: grid;
  gap: 0.15rem;
}
.passo {
  color: var(--apagado);
  font-size: 0.92rem;
}
.feita .etapa {
  border-color: var(--grafite);
  background: var(--papel);
}
</style>
