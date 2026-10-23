<script setup>
import { computed, reactive, ref } from "vue";
import Confirmar from "../components/Confirmar.vue";
import { useAvisos } from "../composables/avisos.js";
import { useCarga } from "../composables/carga.js";
import { ESTADOS, codigoOS, rotuloEstado } from "../lib/estados.js";
import { TIPOS_APARELHO, aparelho, cpf as mascara, data, dinheiro, soDigitos } from "../lib/formato.js";
import { api } from "../services/api.js";

const avisos = useAvisos();
const agora = new Date();
const TRINTA_DIAS = 30 * 24 * 3600 * 1000;

const { dados, erro, carregando, recarregar } = useCarga(async () => {
  const [ordens, clientes, tecnicos, atendentes, pecas] = await Promise.all([
    api.listarOS(),
    api.listarPessoas("clientes"),
    api.listarPessoas("tecnicos"),
    api.listarPessoas("atendentes"),
    api.listarPecas(),
  ]);
  return { ordens, clientes, tecnicos, atendentes, pecas };
});

const ordens = computed(() => [...(dados.value?.ordens ?? [])].sort((a, b) => b.dataCriacao.localeCompare(a.dataCriacao)));
const noMes = (o) => agora.getTime() - new Date(o.dataAtualizacao).getTime() <= TRINTA_DIAS;

const numeros = computed(() => {
  const os = ordens.value;
  const prontas = os.filter((o) => o.estado === "FINALIZADA" && noMes(o));
  return [
    { rotulo: "Na fila do balcão", valor: os.filter((o) => o.estado === "ABERTA").length },
    {
      rotulo: "Em andamento",
      valor: os.filter((o) => ["AGUARDANDO_ORCAMENTO", "AGUARDANDO_APROVACAO", "EM_REPARO"].includes(o.estado)).length,
    },
    { rotulo: "Prontas nos últimos 30 dias", valor: prontas.length },
    { rotulo: "Faturado nos últimos 30 dias", valor: dinheiro(prontas.reduce((t, o) => t + o.valorOrcamento, 0)) },
  ];
});

const porEstado = computed(() => {
  const total = Math.max(1, ...ESTADOS.map((e) => ordens.value.filter((o) => o.estado === e).length));
  return ESTADOS.map((e) => {
    const n = ordens.value.filter((o) => o.estado === e).length;
    return { estado: e, n, largura: `${(n / total) * 100}%` };
  });
});

const porTecnico = computed(() =>
  (dados.value?.tecnicos ?? []).map((t) => {
    const minhas = ordens.value.filter((o) => o.tecnicoResponsavel?.id === t.id);
    return {
      id: t.id,
      nome: t.nome,
      andamento: minhas.filter((o) => ["AGUARDANDO_ORCAMENTO", "AGUARDANDO_APROVACAO", "EM_REPARO"].includes(o.estado)).length,
      prontas: minhas.filter((o) => o.estado === "FINALIZADA").length,
    };
  }),
);

// --- cadastros ---
const abas = [
  { chave: "ordens", rotulo: "Ordens" },
  { chave: "clientes", rotulo: "Clientes" },
  { chave: "tecnicos", rotulo: "Técnicos" },
  { chave: "atendentes", rotulo: "Atendentes" },
  { chave: "pecas", rotulo: "Peças" },
];
const aba = ref("ordens");
const ehPessoa = computed(() => ["clientes", "tecnicos", "atendentes"].includes(aba.value));
const singular = { clientes: "cliente", tecnicos: "técnico", atendentes: "atendente" };

const pessoa = reactive({ nome: "", cpf: "", dataNascimento: "", endereco: "" });
const peca = reactive({ nome: "", tipoPeca: "CELULAR" });

async function tentar(fazer, ok) {
  try {
    await fazer();
    avisos.ok(ok);
    await recarregar();
    return true;
  } catch (e) {
    avisos.erro(e);
    return false;
  }
}

async function cadastrarPessoa() {
  if (!pessoa.nome.trim() || soDigitos(pessoa.cpf).length !== 11) {
    avisos.erro("Preencha o nome e os 11 números do CPF.");
    return;
  }
  const feito = await tentar(
    () => api.criarPessoa(aba.value, { ...pessoa, nome: pessoa.nome.trim(), cpf: soDigitos(pessoa.cpf) }),
    `${pessoa.nome.trim()} cadastrado.`,
  );
  if (feito) Object.assign(pessoa, { nome: "", cpf: "", dataNascimento: "", endereco: "" });
}

async function cadastrarPeca() {
  if (!peca.nome.trim()) {
    avisos.erro("Escreva o nome da peça.");
    return;
  }
  const feito = await tentar(() => api.criarPeca({ nome: peca.nome.trim(), tipoPeca: peca.tipoPeca }), `${peca.nome.trim()} cadastrada.`);
  if (feito) peca.nome = "";
}
</script>

<template>
  <div class="conteudo pagina">
    <h1 class="titulo titulo-2">Gerência</h1>

    <p v-if="carregando" class="fino">Carregando…</p>
    <p v-else-if="erro" role="alert">{{ erro }}</p>
    <template v-else>
      <dl class="numeros" data-numeros>
        <div v-for="n in numeros" :key="n.rotulo">
          <dt>{{ n.rotulo }}</dt>
          <dd>{{ n.valor }}</dd>
        </div>
      </dl>

      <div class="dois">
        <section class="folha pilha" aria-labelledby="t-etapas">
          <h2 id="t-etapas" class="titulo titulo-3">Ordens por estado</h2>
          <ul class="barras">
            <li v-for="b in porEstado" :key="b.estado">
              <span class="rot">{{ rotuloEstado(b.estado) }}</span>
              <span class="trilho" aria-hidden="true">
                <span class="barra" :class="{ apagada: b.estado === 'CANCELADA' }" :style="{ width: b.largura }"></span>
              </span>
              <span class="n">{{ b.n }}</span>
            </li>
          </ul>
        </section>

        <section class="folha pilha" aria-labelledby="t-tecnicos">
          <h2 id="t-tecnicos" class="titulo titulo-3">Técnicos</h2>
          <table class="tabela">
            <thead>
              <tr>
                <th scope="col">Nome</th>
                <th scope="col" class="num">Em andamento</th>
                <th scope="col" class="num">Prontas</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="t in porTecnico" :key="t.id">
                <td>{{ t.nome }}</td>
                <td class="num">{{ t.andamento }}</td>
                <td class="num">{{ t.prontas }}</td>
              </tr>
            </tbody>
          </table>
        </section>
      </div>

      <section class="pilha" aria-labelledby="t-cadastros">
        <h2 id="t-cadastros" class="titulo titulo-3">Cadastros</h2>
        <div class="abas" role="group" aria-label="Escolher cadastro">
          <button
            v-for="a in abas"
            :key="a.chave"
            type="button"
            class="aba"
            :aria-pressed="aba === a.chave"
            :data-aba="a.chave"
            @click="aba = a.chave"
          >
            {{ a.rotulo }}
          </button>
        </div>

        <div v-if="aba === 'ordens'" class="rolagem">
          <table class="tabela" data-tabela="ordens">
            <thead>
              <tr>
                <th scope="col">OS</th>
                <th scope="col">Cliente</th>
                <th scope="col">Aparelho</th>
                <th scope="col">Estado</th>
                <th scope="col" class="num">Valor</th>
                <th scope="col"><span class="sr-only">Ações</span></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="o in ordens" :key="o.id" :data-os="o.id">
                <td><span class="fita">{{ codigoOS(o.id) }}</span></td>
                <td>{{ o.cliente.nome }}</td>
                <td>{{ aparelho(o.hardware.tipoHardware) }}</td>
                <td>{{ rotuloEstado(o.estado) }}</td>
                <td class="num">{{ o.valorOrcamento > 0 ? dinheiro(o.valorOrcamento) : "" }}</td>
                <td>
                  <Confirmar
                    v-if="o.estado === 'ABERTA'"
                    rotulo="Excluir"
                    pergunta="Excluir esta OS?"
                    confirmar="Sim, excluir"
                    @confirmado="tentar(() => api.excluirOS(o.id), `OS ${codigoOS(o.id)} excluída.`)"
                  />
                </td>
              </tr>
            </tbody>
          </table>
          <p class="ajuda">Só dá para excluir uma OS enquanto ela está na fila. Depois disso, cancele.</p>
        </div>

        <div v-else-if="ehPessoa" class="cadastro">
          <div class="rolagem">
            <table class="tabela" :data-tabela="aba">
              <thead>
                <tr>
                  <th scope="col">Nome</th>
                  <th scope="col">CPF</th>
                  <th scope="col">Nascimento</th>
                  <th scope="col"><span class="sr-only">Ações</span></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="p in dados[aba]" :key="p.id">
                  <td>{{ p.nome }}</td>
                  <td class="nowrap">{{ mascara(p.cpf) }}</td>
                  <td class="nowrap">{{ data(`${p.dataNascimento}T12:00:00Z`) }}</td>
                  <td>
                    <Confirmar
                      rotulo="Excluir"
                      :pergunta="`Excluir ${p.nome}?`"
                      confirmar="Sim, excluir"
                      @confirmado="tentar(() => api.excluirPessoa(aba, p.id), `${p.nome} excluído.`)"
                    />
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <form class="folha pilha" novalidate @submit.prevent="cadastrarPessoa">
            <h3 class="titulo titulo-3">Cadastrar {{ singular[aba] }}</h3>
            <div class="campo">
              <label for="p-nome" class="rotulo">Nome completo</label>
              <input id="p-nome" v-model="pessoa.nome" class="entrada" autocomplete="off" />
            </div>
            <div class="campo">
              <label for="p-cpf" class="rotulo">CPF</label>
              <input id="p-cpf" class="entrada" inputmode="numeric" autocomplete="off" :value="pessoa.cpf" @input="(e) => (pessoa.cpf = mascara(e.target.value))" />
            </div>
            <div class="campo">
              <label for="p-nasc" class="rotulo">Data de nascimento</label>
              <input id="p-nasc" v-model="pessoa.dataNascimento" type="date" class="entrada" />
            </div>
            <div class="campo">
              <label for="p-end" class="rotulo">Endereço</label>
              <input id="p-end" v-model="pessoa.endereco" class="entrada" autocomplete="off" />
            </div>
            <button type="submit" class="btn btn-primario">Cadastrar</button>
          </form>
        </div>

        <div v-else class="cadastro">
          <div class="rolagem">
            <table class="tabela" data-tabela="pecas">
              <thead>
                <tr>
                  <th scope="col">Peça</th>
                  <th scope="col">Para</th>
                  <th scope="col" class="num">Usada em</th>
                  <th scope="col"><span class="sr-only">Ações</span></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="p in dados.pecas" :key="p.numeroSerie" :data-peca="p.numeroSerie">
                  <td>{{ p.nome }}</td>
                  <td>{{ aparelho(p.tipoPeca) }}</td>
                  <td class="num">{{ p.usos }} OS</td>
                  <td>
                    <Confirmar
                      rotulo="Excluir"
                      :pergunta="`Excluir ${p.nome}?`"
                      confirmar="Sim, excluir"
                      @confirmado="tentar(() => api.excluirPeca(p.numeroSerie), `${p.nome} excluída.`)"
                    />
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <form class="folha pilha" novalidate @submit.prevent="cadastrarPeca">
            <h3 class="titulo titulo-3">Cadastrar peça</h3>
            <div class="campo">
              <label for="pc-nome" class="rotulo">Nome</label>
              <input id="pc-nome" v-model="peca.nome" class="entrada" autocomplete="off" />
            </div>
            <div class="campo">
              <label for="pc-tipo" class="rotulo">Para</label>
              <select id="pc-tipo" v-model="peca.tipoPeca" class="entrada">
                <option v-for="t in TIPOS_APARELHO" :key="t" :value="t">{{ aparelho(t) }}</option>
              </select>
            </div>
            <button type="submit" class="btn btn-primario">Cadastrar</button>
          </form>
        </div>
      </section>
    </template>
  </div>
</template>

<style scoped>
.pagina {
  display: grid;
  gap: 2rem;
  padding-block: 2.5rem;
}
.numeros {
  display: grid;
  gap: 1px;
  grid-template-columns: repeat(auto-fit, minmax(11rem, 1fr));
  margin: 0;
  background: var(--linha);
  border: 1px solid var(--linha);
  border-radius: 10px;
  overflow: hidden;
}
.numeros > div {
  background: var(--papel-claro);
  padding: 1rem 1.1rem;
  display: grid;
  gap: 0.2rem;
  align-content: start;
}
.numeros dt {
  color: var(--apagado);
  font-size: 0.9rem;
}
.numeros dd {
  margin: 0;
  font-stretch: 115%;
  font-weight: 800;
  font-size: 1.6rem;
  font-variant-numeric: tabular-nums;
}
.dois {
  display: grid;
  gap: 1.25rem;
  grid-template-columns: repeat(auto-fit, minmax(min(100%, 22rem), 1fr));
  align-items: start;
}
.barras {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 0.5rem;
}
.barras li {
  display: grid;
  grid-template-columns: 9.5rem 1fr 2rem;
  gap: 0.6rem;
  align-items: center;
}
.rot {
  font-size: 0.92rem;
}
.trilho {
  height: 0.9rem;
}
.barra {
  display: block;
  height: 100%;
  min-width: 2px;
  background: var(--grafite);
  border-radius: 0 4px 4px 0;
}
.barra.apagada {
  background: var(--apagado);
}
.n {
  text-align: right;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}
.abas {
  display: flex;
  gap: 0.4rem;
  overflow-x: auto;
  padding-bottom: 0.25rem;
}
.aba {
  flex: none;
  min-height: 2.5rem;
  padding: 0.3rem 0.9rem;
  border-radius: 999px;
  border: 2px solid var(--linha);
  background: var(--papel-claro);
  font-weight: 600;
  cursor: pointer;
}
.aba[aria-pressed="true"] {
  background: var(--grafite);
  border-color: var(--grafite);
  color: var(--papel);
}
.rolagem {
  /* posicionado: os textos sr-only (absolutos) da tabela ficam presos aqui e não alargam a página */
  position: relative;
  overflow-x: auto;
  max-width: 100%;
  min-width: 0;
}
.cadastro > *,
.dois > * {
  min-width: 0;
}
.tabela {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.95rem;
}
.tabela th {
  text-align: left;
  font-size: 0.85rem;
  color: var(--apagado);
  font-weight: 600;
  padding: 0.5rem 0.6rem;
  border-bottom: 2px solid var(--grafite);
}
.tabela td {
  padding: 0.35rem 0.6rem;
  border-bottom: 1px solid var(--linha);
  vertical-align: middle;
}
.num {
  text-align: right;
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}
.nowrap {
  white-space: nowrap;
}
.cadastro {
  display: grid;
  gap: 1.5rem;
  align-items: start;
}
@media (min-width: 60rem) {
  .cadastro {
    grid-template-columns: 1fr 20rem;
  }
}
@media (max-width: 30rem) {
  .barras li {
    grid-template-columns: 7.5rem 1fr 1.5rem;
  }
}
</style>
