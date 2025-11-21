<template>
  <div class="container">
    <h2>Área Técnica: {{ usuario.nome }}</h2>
    <button @click="logout">Sair</button>

    <div class="grid">
      <div class="column">
        <h3>OS Abertas (Disponíveis)</h3>
        <div v-for="os in osAbertas" :key="os.id" class="card">
          <p>{{ os.descricao }} - {{ os.hardware?.tipoHardware }}</p>
          <button @click="assumirOS(os)">Assumir e Orçar</button>
        </div>
      </div>

      <div class="column">
        <h3>Minhas OS</h3>
        <div v-for="os in minhasOS" :key="os.id" class="card">
          <p><strong>Status:</strong> {{ os.estado }}</p>
          <p>{{ os.descricao }}</p>
          
          <div v-if="os.estado === 'ABERTA' || os.estado === 'AGUARDANDO_ORCAMENTO'">
             <input type="number" v-model="valorOrcamento[os.id]" placeholder="Valor R$">
             <button @click="enviarOrcamento(os.id)">Enviar Orçamento</button>
          </div>

          <div v-if="os.estado === 'EM_REPARO'">
             <button @click="executarReparo(os.id)">Executar Reparo (Logs)</button>
             <button @click="finalizarOS(os.id)" class="btn-success">Finalizar OS</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const usuario = JSON.parse(localStorage.getItem('usuario'));
const osAbertas = ref([]);
const minhasOS = ref([]);
const valorOrcamento = ref({});

const carregarDados = async () => {
  // Busca OS com estado ABERTA
  const resAbertas = await api.get('/os/estado/ABERTA');
  osAbertas.value = resAbertas.data;

  // Busca OS do técnico logado
  const resMinhas = await api.get(`/os/tecnico/${usuario.id}`);
  minhasOS.value = resMinhas.data;
};

// O técnico assume a OS ao orçar (Backend logic mapping)
// Como seu backend não tem endpoint explicito "assumir", vamos atualizar a OS inserindo o ID do tecnico
const assumirOS = async (os) => {
    const dto = {
        valorOrcamento: os.valorOrcamento,
        descricao: os.descricao,
        clienteId: os.cliente.id,
        hardwareId: os.hardware.numeroSerie,
        tecnicoId: usuario.id // Atribui a mim mesmo
    };
    // Atualiza a OS com o ID do técnico
    await api.put(`/os/${os.id}`, dto);
    // Muda status para aguardando orçamento (lógica simulada, ou via endpoint de estado)
    carregarDados();
};

const enviarOrcamento = async (id) => {
    // 1. Atualiza valor
    const osAtual = minhasOS.value.find(o => o.id === id);
    const dto = {
        valorOrcamento: valorOrcamento.value[id],
        descricao: osAtual.descricao,
        clienteId: osAtual.cliente.id,
        hardwareId: osAtual.hardware.numeroSerie,
        tecnicoId: usuario.id
    };
    await api.put(`/os/${id}`, dto);
    
    // 2. Dispara gatilho de estado
    await api.post(`/os/${id}/orcamentar`);
    carregarDados();
};

const executarReparo = async (id) => {
    // Chama endpoint que dispara o Template Method no backend
    await api.post(`/os/${id}/executar`);
    alert("Reparo executado (Verifique o console do servidor Java para os logs do Template Method)");
};

const finalizarOS = async (id) => {
    await api.post(`/os/${id}/finalizar`);
    carregarDados();
};

const logout = () => { localStorage.clear(); router.push('/'); };
onMounted(carregarDados);
</script>

<style scoped>
.grid { display: flex; gap: 20px; }
.column { flex: 1; background: #f9f9f9; padding: 10px; }
.card { background: white; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; }
.btn-success { background-color: green; color: white; margin-top: 5px;}
</style>