<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { DEMO, DEMO_USERS, api } from "../services/api.js";
import { PERFIS, useSessao } from "../composables/sessao.js";
import { cpf as mascara, soDigitos } from "../lib/formato.js";

const router = useRouter();
const route = useRoute();
const { entrar } = useSessao();

const perfil = ref("cliente");
const cpfDigitado = ref("");
const erro = ref("");
const enviando = ref(false);

const opcoes = [
  { valor: "cliente", rotulo: "Cliente", texto: "Acompanhar meu aparelho" },
  { valor: "atendente", rotulo: "Atendente", texto: "Balcão: clientes e novas OS" },
  { valor: "tecnico", rotulo: "Técnico", texto: "Bancada: diagnóstico e reparo" },
  { valor: "admin", rotulo: "Administração", texto: "Números da loja e cadastros" },
];

const atalhos = [
  { ...DEMO_USERS.cliente, perfil: "cliente" },
  { ...DEMO_USERS.atendente, perfil: "atendente" },
  { ...DEMO_USERS.tecnico, perfil: "tecnico" },
  { ...DEMO_USERS.admin, perfil: "admin" },
];

function digitar(e) {
  cpfDigitado.value = mascara(e.target.value);
  erro.value = "";
}

async function acessar(p, cpf) {
  enviando.value = true;
  erro.value = "";
  try {
    const usuario = await api.buscarPorCpf(PERFIS[p].api, cpf);
    entrar(p, usuario);
    const volta = typeof route.query.volta === "string" && route.query.volta.startsWith("/") ? route.query.volta : null;
    router.push(volta ?? PERFIS[p].inicio);
  } catch {
    erro.value = "Não achamos ninguém com esse CPF nesse perfil. Confira os números e o perfil escolhido.";
  } finally {
    enviando.value = false;
  }
}

function enviar() {
  const digitos = soDigitos(cpfDigitado.value);
  if (digitos.length !== 11) {
    erro.value = "O CPF tem 11 números.";
    return;
  }
  acessar(perfil.value, digitos);
}
</script>

<template>
  <div class="conteudo pagina">
    <section v-if="DEMO" class="atalhos" aria-labelledby="t-atalhos">
      <h1 id="t-atalhos" class="titulo titulo-2">Entre com um clique</h1>
      <p class="fino">Cada botão abre o sistema com uma pessoa de exemplo. Os dados são fictícios.</p>
      <ul class="lista-atalhos">
        <li v-for="a in atalhos" :key="a.perfil">
          <button type="button" class="atalho" data-entrar-demo :data-perfil="a.perfil" :disabled="enviando" @click="acessar(a.perfil, a.cpf)">
            <span class="fita">{{ a.rotulo }}</span>
            <span class="quem">{{ a.nome }}</span>
            <span class="fino">{{ opcoes.find((o) => o.valor === a.perfil).texto }}</span>
          </button>
        </li>
      </ul>
    </section>

    <section class="folha formulario" aria-labelledby="t-cpf">
      <component :is="DEMO ? 'h2' : 'h1'" id="t-cpf" class="titulo titulo-3">{{ DEMO ? "Ou entre pelo CPF" : "Entrar" }}</component>
      <form class="pilha" novalidate @submit.prevent="enviar">
        <fieldset>
          <legend class="rotulo">Você é</legend>
          <div class="perfis">
            <label v-for="o in opcoes" :key="o.valor" class="perfil" :class="{ marcado: perfil === o.valor }">
              <input v-model="perfil" type="radio" name="perfil" :value="o.valor" class="sr-only" />
              <span class="nome">{{ o.rotulo }}</span>
            </label>
          </div>
        </fieldset>
        <div class="campo">
          <label for="cpf" class="rotulo">CPF</label>
          <input
            id="cpf"
            class="entrada"
            inputmode="numeric"
            autocomplete="off"
            placeholder="000.000.000-00"
            :value="cpfDigitado"
            :aria-invalid="erro ? 'true' : undefined"
            aria-describedby="cpf-erro"
            @input="digitar"
          />
          <p id="cpf-erro" class="erro" role="alert">{{ erro }}</p>
        </div>
        <button type="submit" class="btn btn-primario" :disabled="enviando">Entrar</button>
      </form>
    </section>
  </div>
</template>

<style scoped>
.pagina {
  display: grid;
  gap: 2rem;
  padding-block: 2.5rem;
  max-width: 52rem;
}
.atalhos {
  display: grid;
  gap: 0.75rem;
}
.lista-atalhos {
  list-style: none;
  margin: 0.5rem 0 0;
  padding: 0;
  display: grid;
  gap: 0.75rem;
  grid-template-columns: repeat(auto-fit, minmax(14rem, 1fr));
}
.atalho {
  width: 100%;
  height: 100%;
  display: grid;
  justify-items: start;
  gap: 0.35rem;
  text-align: left;
  padding: 1rem;
  background: var(--papel-claro);
  border: 2px solid var(--linha);
  border-radius: 10px;
  cursor: pointer;
}
.atalho:hover {
  border-color: var(--grafite);
}
.quem {
  font-weight: 750;
  font-size: 1.1rem;
}
.formulario {
  max-width: 28rem;
}
.perfis {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.5rem;
  margin-top: 0.4rem;
}
.perfil {
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
.perfil.marcado {
  border-color: var(--grafite);
  background: var(--grafite);
  color: var(--amarelo);
}
.perfil:has(:focus-visible) {
  outline: 3px solid var(--carbono);
  outline-offset: 2px;
}
.erro {
  color: #8a1c10;
  font-weight: 600;
  min-height: 1.5em;
}
</style>
