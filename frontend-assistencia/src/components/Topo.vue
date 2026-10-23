<script setup>
import { useRouter } from "vue-router";
import { PERFIS, useSessao } from "../composables/sessao.js";
import { primeiroNome } from "../lib/formato.js";

const { sessao, sair } = useSessao();
const router = useRouter();

function sairAgora() {
  sair();
  router.push("/");
}
</script>

<template>
  <header class="topo">
    <div class="conteudo linha">
      <RouterLink to="/" class="marca" aria-label="Ponto de Solda, página inicial">
        <span class="ponto" aria-hidden="true"></span>
        <span>Ponto de Solda</span>
      </RouterLink>

      <nav v-if="sessao" aria-label="Sessão" class="nav">
        <RouterLink :to="PERFIS[sessao.perfil].inicio" class="quem">
          <span class="nome">{{ primeiroNome(sessao.usuario.nome) }}</span>
          <span class="papel-perfil">{{ PERFIS[sessao.perfil].rotulo }}</span>
        </RouterLink>
        <button type="button" class="btn btn-contorno" @click="sairAgora">Sair</button>
      </nav>
      <nav v-else aria-label="Principal" class="nav">
        <RouterLink to="/#caminho" class="link">Como funciona</RouterLink>
        <RouterLink to="/entrar" class="btn btn-grafite">Entrar</RouterLink>
      </nav>
    </div>
  </header>
</template>

<style scoped>
.topo {
  border-bottom: 1px solid var(--linha);
}
.linha {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  min-height: 4.25rem;
}
.marca {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  color: var(--grafite);
  text-decoration: none;
  font-stretch: 125%;
  font-weight: 850;
  font-size: 1.15rem;
  white-space: nowrap;
}
.ponto {
  width: 1.15rem;
  height: 1.15rem;
  border-radius: 50%;
  background: var(--amarelo);
  border: 4px solid var(--grafite);
  box-shadow: 0 0 0 2px var(--grafite);
}
.nav {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.link,
.quem {
  color: var(--grafite);
  font-weight: 600;
}
.quem {
  display: grid;
  line-height: 1.2;
  text-align: right;
  text-decoration: none;
}
.papel-perfil {
  font-size: 0.8rem;
  font-weight: 500;
  color: var(--apagado);
}
@media (max-width: 30rem) {
  .link {
    display: none;
  }
}
</style>
