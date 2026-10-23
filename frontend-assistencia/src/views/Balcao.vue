<script setup>
import { computed, reactive, ref } from "vue";
import Etiqueta from "../components/Etiqueta.vue";
import { useAvisos } from "../composables/avisos.js";
import { useCarga } from "../composables/carga.js";
import { ESTADOS, codigoOS, rotuloEstado } from "../lib/estados.js";
import { TIPOS_APARELHO, aparelho, cpf as mascara, haQuanto, soDigitos } from "../lib/formato.js";
import { api } from "../services/api.js";

const avisos = useAvisos();
const agora = new Date();

// --- lista de ordens ---
const { dados: ordens, erro, carregando, recarregar } = useCarga(() => api.listarOS());
const filtro = ref("TODAS");
const busca = ref("");
const aberta = ref(null);

const recentes = computed(() => [...(ordens.value ?? [])].sort((a, b) => b.dataCriacao.localeCompare(a.dataCriacao)));
const contagem = computed(() => {
  const c = Object.fromEntries(ESTADOS.map((e) => [e, 0]));
  for (const o of recentes.value) c[o.estado] += 1;
  return c;
});
const visiveis = computed(() => {
  const termo = busca.value.trim().toLowerCase();
  return recentes.value.filter(
    (o) =>
      (filtro.value === "TODAS" || o.estado === filtro.value) &&
      (!termo || o.cliente.nome.toLowerCase().includes(termo) || codigoOS(o.id).toLowerCase().includes(termo)),
  );
});

// --- nova OS ---
const cpfBusca = ref("");
const cliente = ref(null);
const naoAchou = ref(false);
const novo = reactive({ nome: "", dataNascimento: "", endereco: "" });
const tipo = ref("CELULAR");
const defeito = ref("");
const ocupado = ref(false);

async function buscarCliente() {
  const d = soDigitos(cpfBusca.value);
  if (d.length !== 11) {
    avisos.erro("O CPF tem 11 números.");
    return;
  }
  try {
    cliente.value = await api.buscarPorCpf("clientes", d);
    naoAchou.value = false;
  } catch {
    cliente.value = null;
    naoAchou.value = true;
  }
}

function trocarCliente() {
  cliente.value = null;
  naoAchou.value = false;
  cpfBusca.value = "";
}

async function cadastrar() {
  if (!novo.nome.trim()) {
    avisos.erro("Escreva o nome do cliente.");
    return;
  }
  ocupado.value = true;
  try {
    cliente.value = await api.criarPessoa("clientes", { ...novo, nome: novo.nome.trim(), cpf: soDigitos(cpfBusca.value) });
    naoAchou.value = false;
    Object.assign(novo, { nome: "", dataNascimento: "", endereco: "" });
    avisos.ok(`${cliente.value.nome} cadastrado.`);
  } catch (e) {
    avisos.erro(e);
  } finally {
    ocupado.value = false;
  }
}

async function abrirOS() {
  if (!defeito.value.trim()) {
    avisos.erro("Descreva o defeito que o cliente relatou.");
    return;
  }
  ocupado.value = true;
  try {
    const hw = await api.criarHardware({ tipoHardware: tipo.value, clienteId: cliente.value.id });
    const os = await api.criarOS({ descricao: defeito.value.trim(), clienteId: cliente.value.id, hardwareId: hw.numeroSerie });
    avisos.ok(`OS ${codigoOS(os.id)} aberta para ${cliente.value.nome}. Prenda a etiqueta no aparelho.`);
    defeito.value = "";
    trocarCliente();
    filtro.value = "TODAS";
    aberta.value = os.id;
    await recarregar();
  } catch (e) {
    avisos.erro(e);
  } finally {
    ocupado.value = false;
  }
}
</script>

<template>
  <div class="conteudo pagina">
    <h1 class="titulo titulo-2">Balcão</h1>

    <div class="colunas">
      <section class="folha pilha nova" aria-labelledby="t-nova">
        <h2 id="t-nova" class="titulo titulo-3">Receber aparelho</h2>

        <form v-if="!cliente" class="pilha" novalidate @submit.prevent="buscarCliente">
          <div class="campo">
            <label for="cpf-cliente" class="rotulo">CPF do cliente</label>
            <div class="linha-busca">
              <input
                id="cpf-cliente"
                class="entrada"
                inputmode="numeric"
                autocomplete="off"
                placeholder="000.000.000-00"
                :value="cpfBusca"
                @input="(e) => ((cpfBusca = mascara(e.target.value)), (naoAchou = false))"
              />
              <button type="submit" class="btn btn-contorno">Buscar</button>
            </div>
          </div>
        </form>

        <form v-if="naoAchou" class="pilha cadastro" novalidate data-cadastro @submit.prevent="cadastrar">
          <p><strong>Ninguém com esse CPF.</strong> Cadastre o cliente para continuar.</p>
          <div class="campo">
            <label for="nome" class="rotulo">Nome completo</label>
            <input id="nome" v-model="novo.nome" class="entrada" autocomplete="off" />
          </div>
          <div class="campo">
            <label for="nascimento" class="rotulo">Data de nascimento</label>
            <input id="nascimento" v-model="novo.dataNascimento" type="date" class="entrada" />
            <p class="ajuda">Precisa ter 18 anos ou mais.</p>
          </div>
          <div class="campo">
            <label for="endereco" class="rotulo">Endereço</label>
            <input id="endereco" v-model="novo.endereco" class="entrada" autocomplete="off" />
          </div>
          <button type="submit" class="btn btn-contorno" :disabled="ocupado">Cadastrar cliente</button>
        </form>

        <form v-if="cliente" class="pilha" novalidate @submit.prevent="abrirOS">
          <div class="escolhido" data-cliente-escolhido>
            <div>
              <p class="nome-cliente">{{ cliente.nome }}</p>
              <p class="fino">CPF {{ mascara(cliente.cpf) }}</p>
            </div>
            <button type="button" class="btn-link" @click="trocarCliente">Trocar</button>
          </div>
          <fieldset>
            <legend class="rotulo">Aparelho</legend>
            <div class="tipos">
              <label v-for="t in TIPOS_APARELHO" :key="t" class="tipo" :class="{ marcado: tipo === t }">
                <input v-model="tipo" type="radio" name="tipo" :value="t" class="sr-only" />
                {{ aparelho(t) }}
              </label>
            </div>
          </fieldset>
          <div class="campo">
            <label for="defeito" class="rotulo">Defeito relatado</label>
            <textarea id="defeito" v-model="defeito" class="entrada" placeholder="Com as palavras do cliente."></textarea>
          </div>
          <button type="submit" class="btn btn-primario" :disabled="ocupado" data-abrir-os>Abrir OS</button>
        </form>
      </section>

      <section class="pilha" aria-labelledby="t-ordens">
        <div class="cabeca-lista">
          <h2 id="t-ordens" class="titulo titulo-3">Ordens de serviço</h2>
          <label class="sr-only" for="busca">Buscar por cliente ou número da OS</label>
          <input id="busca" v-model="busca" class="entrada busca" type="search" placeholder="Cliente ou número" />
        </div>

        <div class="filtros" role="group" aria-label="Filtrar por estado">
          <button type="button" class="filtro" :aria-pressed="filtro === 'TODAS'" @click="filtro = 'TODAS'">
            Todas <span class="conta">{{ recentes.length }}</span>
          </button>
          <button
            v-for="e in ESTADOS"
            :key="e"
            type="button"
            class="filtro"
            :aria-pressed="filtro === e"
            :data-filtro="e"
            @click="filtro = e"
          >
            {{ rotuloEstado(e) }} <span class="conta">{{ contagem[e] }}</span>
          </button>
        </div>

        <p v-if="carregando" class="fino">Carregando…</p>
        <p v-else-if="erro" role="alert">{{ erro }}</p>
        <p v-else-if="!visiveis.length" class="fino">Nenhuma OS com esse filtro.</p>
        <ul v-else class="ordens" data-lista-os>
          <li v-for="o in visiveis" :key="o.id">
            <button
              type="button"
              class="linha-os"
              :aria-expanded="aberta === o.id"
              :aria-controls="`os-${o.id}`"
              @click="aberta = aberta === o.id ? null : o.id"
            >
              <span class="fita">{{ codigoOS(o.id) }}</span>
              <span class="quem">
                <span class="nome-cliente">{{ o.cliente.nome }}</span>
                <span class="fino">{{ aparelho(o.hardware.tipoHardware) }}, {{ haQuanto(o.dataCriacao, agora) }}</span>
              </span>
              <span class="estado" :data-estado="o.estado">{{ rotuloEstado(o.estado) }}</span>
            </button>
            <div v-if="aberta === o.id" :id="`os-${o.id}`" class="detalhe entra">
              <Etiqueta :os="o" :agora="agora" />
              <p v-if="o.observacoesTecnicas" class="laudo"><strong>Laudo:</strong> {{ o.observacoesTecnicas }}</p>
            </div>
          </li>
        </ul>
      </section>
    </div>
  </div>
</template>

<style scoped>
.pagina {
  display: grid;
  gap: 1.5rem;
  padding-block: 2.5rem;
}
.colunas {
  display: grid;
  gap: 2rem;
  align-items: start;
}
@media (min-width: 60rem) {
  .colunas {
    grid-template-columns: 22rem 1fr;
  }
  .nova {
    position: sticky;
    top: 1rem;
  }
}
.linha-busca {
  display: flex;
  gap: 0.5rem;
}
.cadastro {
  padding-top: 1rem;
  border-top: 1px dashed var(--linha);
}
.escolhido {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: var(--branco);
  border: 2px solid var(--grafite);
  border-radius: var(--raio);
}
.nome-cliente {
  font-weight: 700;
}
.tipos {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.5rem;
  margin-top: 0.4rem;
}
.tipo {
  position: relative;
  display: flex;
  align-items: center;
  min-height: 2.75rem;
  padding: 0.5rem 0.75rem;
  border: 2px solid var(--linha);
  border-radius: var(--raio);
  background: var(--branco);
  cursor: pointer;
  font-weight: 600;
}
.tipo.marcado {
  border-color: var(--grafite);
  background: var(--grafite);
  color: var(--amarelo);
}
.tipo:has(:focus-visible) {
  outline: 3px solid var(--carbono);
  outline-offset: 2px;
}

.cabeca-lista {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: center;
  justify-content: space-between;
}
.busca {
  max-width: 16rem;
}
.filtros {
  display: flex;
  gap: 0.4rem;
  overflow-x: auto;
  padding-bottom: 0.25rem;
}
.filtro {
  flex: none;
  min-height: 2.5rem;
  padding: 0.3rem 0.8rem;
  border-radius: 999px;
  border: 2px solid var(--linha);
  background: var(--papel-claro);
  font-weight: 600;
  cursor: pointer;
}
.filtro[aria-pressed="true"] {
  background: var(--grafite);
  border-color: var(--grafite);
  color: var(--papel);
}
.conta {
  font-variant-numeric: tabular-nums;
  opacity: 0.8;
  margin-left: 0.2rem;
}

.ordens {
  list-style: none;
  margin: 0;
  padding: 0;
  border-top: 1px solid var(--linha);
}
.ordens > li {
  border-bottom: 1px solid var(--linha);
}
.linha-os {
  width: 100%;
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 0.9rem;
  align-items: center;
  padding: 0.75rem 0.25rem;
  background: none;
  border: 0;
  text-align: left;
  cursor: pointer;
}
.linha-os:hover {
  background: var(--papel-claro);
}
.quem {
  display: grid;
  min-width: 0;
}
.quem .fino {
  font-size: 0.88rem;
}
.estado {
  font-weight: 650;
  font-size: 0.9rem;
  text-align: right;
}
.estado[data-estado="CANCELADA"] {
  color: var(--apagado);
  text-decoration: line-through;
}
.estado[data-estado="FINALIZADA"]::before {
  content: "✓ ";
}
.detalhe {
  display: grid;
  gap: 0.75rem;
  padding: 0.5rem 0 1.25rem;
}
.laudo {
  padding-left: 2.6rem;
}
@media (min-width: 60rem) {
  .filtros {
    flex-wrap: wrap;
    overflow-x: visible;
  }
}
@media (max-width: 30rem) {
  .linha-os {
    grid-template-columns: auto 1fr;
  }
  .estado {
    grid-column: 2;
    text-align: left;
  }
  .busca {
    max-width: none;
  }
}
</style>
