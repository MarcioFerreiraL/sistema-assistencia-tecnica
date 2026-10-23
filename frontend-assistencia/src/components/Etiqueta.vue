<script setup>
import { computed } from "vue";
import { ETAPAS, codigoOS, etapaAtual, rotuloEstado } from "../lib/estados.js";
import { aparelho, dinheiro, haQuanto } from "../lib/formato.js";

const props = defineProps({
  os: { type: Object, required: true },
  /** Quem está vendo: muda o título (cliente não vê o próprio nome) e o rótulo do estado. */
  perfil: { type: String, default: "" },
  agora: { type: Date, default: () => new Date() },
});

const atual = computed(() => etapaAtual(props.os.estado));
const cancelada = computed(() => props.os.estado === "CANCELADA");
const titulo = computed(() =>
  props.perfil === "cliente" ? aparelho(props.os.hardware.tipoHardware) : `${aparelho(props.os.hardware.tipoHardware)} de ${props.os.cliente.nome}`,
);
</script>

<template>
  <article class="etiqueta" :class="{ cancelada }" :data-os="os.id" :data-estado="os.estado">
    <div class="papel">
    <span class="ilhos" aria-hidden="true"></span>
    <div class="corpo">
      <header class="cabeca">
        <span class="fita">OS {{ codigoOS(os.id) }}</span>
        <span class="estado">{{ rotuloEstado(os.estado, perfil) }}</span>
      </header>

      <h3 class="titulo titulo-3">{{ titulo }}</h3>
      <p class="defeito">{{ os.descricao }}</p>

      <ol v-if="!cancelada" class="furos" aria-label="Andamento do conserto">
        <li
          v-for="(e, i) in ETAPAS"
          :key="e.estado"
          :class="{ feito: i < atual, atual: i === atual }"
          :aria-current="i === atual ? 'step' : undefined"
        >
          <span class="furo" aria-hidden="true"></span>
          <span class="nome">{{ e.etapa }}</span>
          <span class="sr-only">{{ i < atual ? "(concluída)" : i === atual ? "(etapa atual)" : "" }}</span>
        </li>
      </ol>
      <p v-else class="carimbo">Cancelada</p>

      <dl class="dados">
        <div v-if="os.valorOrcamento > 0">
          <dt>Orçamento</dt>
          <dd class="valor">{{ dinheiro(os.valorOrcamento) }}</dd>
        </div>
        <div v-if="os.tecnicoResponsavel && perfil !== 'tecnico'">
          <dt>Técnico</dt>
          <dd>{{ os.tecnicoResponsavel.nome }}</dd>
        </div>
        <div>
          <dt>Entrada</dt>
          <dd>{{ haQuanto(os.dataCriacao, agora) }}</dd>
        </div>
      </dl>

      <div v-if="$slots.default" class="acoes"><slot /></div>
    </div>
    </div>
  </article>
</template>

<style scoped>
.etiqueta {
  filter: drop-shadow(0 1px 0 rgb(29 32 38 / 0.14)) drop-shadow(0 6px 10px rgb(29 32 38 / 0.12));
}
.papel {
  --corte: 20px;
  container-type: inline-size;
  display: grid;
  grid-template-columns: 2.6em 1fr;
  background: var(--papel-claro);
  clip-path: polygon(var(--corte) 0, 100% 0, 100% 100%, var(--corte) 100%, 0 calc(100% - var(--corte)), 0 var(--corte));
}

/* Ilhós: o furo reforçado por onde passa o barbante. */
.ilhos {
  align-self: center;
  justify-self: center;
  width: 1.1em;
  height: 1.1em;
  border-radius: 50%;
  background: var(--papel);
  box-shadow: inset 0 1px 2px rgb(29 32 38 / 0.35), 0 0 0 4px #e6dcc3;
}

.corpo {
  padding: 1em 1.1em 1.1em 0.4em;
  display: grid;
  gap: 0.6em;
  min-width: 0;
}

.cabeca {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 0.5em;
}
.estado {
  font-weight: 700;
  font-size: 0.95em;
}
[data-estado="AGUARDANDO_APROVACAO"] .estado {
  color: var(--carbono);
}

.defeito {
  color: var(--apagado);
}

.furos {
  list-style: none;
  margin: 0.2em 0 0;
  padding: 0;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  position: relative;
}
.furos::before {
  content: "";
  position: absolute;
  left: 10%;
  right: 10%;
  top: 0.55em;
  border-top: 2px dashed var(--linha);
}
.furos li {
  position: relative;
  display: grid;
  justify-items: center;
  gap: 0.3em;
  text-align: center;
}
.furo {
  width: 1.1em;
  height: 1.1em;
  border-radius: 50%;
  background: var(--papel-claro);
  border: 2px solid var(--linha);
}
.feito .furo {
  background: var(--grafite);
  border-color: var(--grafite);
}
.atual .furo {
  background: var(--amarelo);
  border: 4px solid var(--grafite);
}
.nome {
  font-size: 0.78em;
  line-height: 1.2;
  color: var(--apagado);
}
.atual .nome {
  color: var(--grafite);
  font-weight: 700;
}
/* Etiqueta estreita: só a etapa atual mostra o nome; as outras ficam para o leitor de tela. */
@container (max-width: 27rem) {
  .furos li:not(.atual) .nome {
    position: absolute;
    width: 1px;
    height: 1px;
    overflow: hidden;
    clip: rect(0 0 0 0);
    white-space: nowrap;
  }
  .atual .nome {
    white-space: nowrap;
  }
}

.carimbo {
  justify-self: start;
  border: 3px solid var(--carbono);
  color: var(--carbono);
  font-stretch: 125%;
  font-weight: 850;
  padding: 0.1em 0.6em;
  border-radius: 4px;
  transform: rotate(-4deg);
}
.cancelada .defeito,
.cancelada .titulo {
  text-decoration: line-through;
  text-decoration-color: rgb(29 32 38 / 0.4);
}

.dados {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4em 1.5em;
  margin: 0;
  font-size: 0.9em;
}
.dados dt {
  color: var(--apagado);
  font-size: 0.8em;
}
.dados dd {
  margin: 0;
  font-weight: 600;
}
.valor {
  font-variant-numeric: tabular-nums;
}

.acoes {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6em;
  align-items: center;
  padding-top: 0.6em;
  border-top: 1px dashed var(--linha);
}
</style>
