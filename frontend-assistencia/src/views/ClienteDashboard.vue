<template>
  <div class="container">
    <h2>Painel do Cliente: {{ usuario.nome }}</h2>
    <button @click="logout">Sair</button>

    <h3>Minhas Ordens de Serviço</h3>
    <div v-if="listaOS.length === 0">Nenhuma OS encontrada.</div>
    
    <div v-for="os in listaOS" :key="os.id" class="os-card">
      <p><strong>ID:</strong> {{ os.id }}</p>
      <p><strong>Descrição:</strong> {{ os.descricao }}</p>
      <p><strong>Status:</strong> <span :class="os.estado">{{ os.estado }}</span></p>
      <p><strong>Orçamento:</strong> R$ {{ os.valorOrcamento }}</p>
      
      <button v-if="os.estado === 'AGUARDANDO_APROVACAO'" @click="aprovarOrcamento(os.id)">
        Aprovar Orçamento
      </button>
      <button v-if="os.estado !== 'FINALIZADA' && os.estado !== 'CANCELADA'" @click="cancelarOS(os.id)" class="btn-danger">
        Cancelar OS
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const usuario = JSON.parse(localStorage.getItem('usuario'));
const listaOS = ref([]);

const carregarOS = async () => {
  try {
    const res = await api.get(`/os/cliente/${usuario.id}`);
    listaOS.value = res.data;
  } catch (e) { alert('Erro ao carregar OS'); }
};

const aprovarOrcamento = async (id) => {
  await api.post(`/os/${id}/aprovar`);
  carregarOS();
};

const cancelarOS = async (id) => {
  await api.post(`/os/${id}/cancelar`);
  carregarOS();
};

const logout = () => { localStorage.clear(); router.push('/'); };

onMounted(carregarOS);
</script>

<style scoped>
.os-card { border: 1px solid #ddd; padding: 10px; margin: 10px 0; border-left: 5px solid #333; }
.AGUARDANDO_APROVACAO { color: orange; font-weight: bold; }
.EM_REPARO { color: blue; font-weight: bold; }
.FINALIZADA { color: green; font-weight: bold; }
.btn-danger { background-color: #ff4444; color: white; margin-left: 10px;}
</style>