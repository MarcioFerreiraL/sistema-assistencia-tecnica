<template>
  <div class="container">
    <div class="header">
      <h2>Painel do Cliente</h2> <br>
      <p>Bem-vindo, {{ usuario?.nome }}</p>
      <button @click="logout" class="btn-logout">Sair</button>
    </div>

    <h3>Minhas Solicitações</h3>
    <div v-if="listaOS.length === 0" class="empty-state">
      Você não possui ordens de serviço registradas.
    </div>
    
    <div v-for="os in listaOS" :key="os.id" class="os-card">
      <div class="os-header">
        <span class="os-id">OS #{{ os.id.substring(0,8) }}</span>
        <span :class="['status-badge', os.estado]">{{ formatarStatus(os.estado) }}</span>
      </div>
      
      <div class="os-body">
        <p><strong>Equipamento:</strong> {{ os.hardware?.tipoHardware }}</p>
        <p><strong>Descrição:</strong> {{ os.descricao }}</p>
        <p v-if="os.valorOrcamento > 0"><strong>Valor do Orçamento:</strong> <span class="valor">R$ {{ os.valorOrcamento.toFixed(2) }}</span></p>
      </div>

      <div v-if="os.estado === 'AGUARDANDO_APROVACAO'" class="approval-box">
        <p>O técnico definiu um orçamento para o reparo.</p>
        <div class="approval-actions">
          <button @click="aprovarOrcamento(os.id)" class="btn-approve">
            ✅ Aprovar Orçamento (R$ {{ os.valorOrcamento.toFixed(2) }})
          </button>
          <button @click="cancelarOS(os.id)" class="btn-reject">
            ❌ Recusar e Cancelar
          </button>
        </div>
      </div>

      <div v-else-if="os.estado !== 'FINALIZADA' && os.estado !== 'CANCELADA'" class="actions">
        <button @click="cancelarOS(os.id)" class="btn-cancel-sm">Cancelar Solicitação</button>
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
const listaOS = ref([]);

const carregarOS = async () => {
  try {
    const res = await api.get(`/os/cliente/${usuario.id}`);
    listaOS.value = res.data.reverse();
  } catch (e) { alert('Erro ao carregar OS'); }
};

const formatarStatus = (status) => {
  return status.replace(/_/g, ' ');
};

const aprovarOrcamento = async (id) => {
  if(!confirm("Confirma a aprovação do valor?")) return;
  try {
    await api.post(`/os/${id}/aprovar`);
    alert("Orçamento aprovado! O técnico iniciará o reparo.");
    carregarOS();
  } catch (e) { alert("Erro ao aprovar."); }
};

const cancelarOS = async (id) => {
  if(!confirm("Tem certeza que deseja cancelar esta OS?")) return;
  try {
    await api.post(`/os/${id}/cancelar`);
    carregarOS();
  } catch (e) { alert("Erro ao cancelar."); }
};

const logout = () => { localStorage.clear(); router.push('/'); };

onMounted(carregarOS);
</script>

<style scoped>
.container { max-width: 800px; margin: 0 auto; padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #eee; padding-bottom: 10px; }
.btn-logout { background: #e74c3c; color: white; border: none; padding: 8px 15px; border-radius: 4px; cursor: pointer; }

.os-card { background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); margin-bottom: 20px; overflow: hidden; border: 1px solid #eee; }
.os-header { background: #f8f9fa; padding: 15px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; }
.os-id { font-weight: bold; color: #555; }
.status-badge { padding: 4px 10px; border-radius: 12px; font-size: 0.85em; font-weight: bold; color: white; }
.status-badge.ABERTA { background-color: #3498db; }
.status-badge.AGUARDANDO_APROVACAO { background-color: #f39c12; }
.status-badge.EM_REPARO { background-color: #9b59b6; }
.status-badge.FINALIZADA { background-color: #27ae60; }
.status-badge.CANCELADA { background-color: #7f8c8d; }

.os-body { padding: 15px; }
.valor { color: #27ae60; font-weight: bold; font-size: 1.1em; }

.approval-box { background: #fff3cd; padding: 15px; margin: 15px; border-radius: 6px; border: 1px solid #ffeeba; }
.approval-actions { display: flex; gap: 10px; margin-top: 10px; }
.btn-approve { background: #28a745; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; font-weight: bold; flex: 1; }
.btn-reject { background: #dc3545; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; }

.actions { padding: 0 15px 15px 15px; text-align: right; }
.btn-cancel-sm { background: transparent; color: #e74c3c; border: 1px solid #e74c3c; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 0.9em; }
.btn-cancel-sm:hover { background: #e74c3c; color: white; }
</style>