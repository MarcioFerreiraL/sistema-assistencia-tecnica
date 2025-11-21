<template>
  <div class="container">
    <div class="header flex-between">
      <div>
        <h2>Minhas Solicitações</h2>
        <p class="text-muted">Gerencie o reparo dos seus equipamentos</p>
      </div>
      <div class="user-actions flex">
        <span class="user-name">Olá, {{ usuario?.nome }}</span>
        <button @click="logout" class="btn-secondary btn-sm">Sair</button>
      </div>
    </div>

    <div v-if="listaOS.length === 0" class="empty-state">
      <div class="empty-icon">📭</div>
      <h3>Nenhuma solicitação encontrada</h3>
      <p>Você ainda não possui ordens de serviço registradas em nosso sistema.</p>
    </div>
    
    <div class="os-grid">
      <div v-for="os in listaOS" :key="os.id" class="card os-card">
        <div class="os-header flex-between">
          <div class="os-meta">
            <span class="os-id">#{{ os.id.substring(0,8) }}</span>
            <span class="os-date">{{ new Date().toLocaleDateString() }}</span> </div>
          <span :class="['badge', 'status-' + os.estado]">{{ formatarStatus(os.estado) }}</span>
        </div>
        
        <div class="os-body">
          <div class="info-row">
            <label>Equipamento</label>
            <p class="highlight">{{ os.hardware?.tipoHardware }}</p>
          </div>
          <div class="info-row">
            <label>Problema Relatado</label>
            <p class="description">{{ os.descricao }}</p>
          </div>
          
          <div v-if="os.valorOrcamento > 0" class="budget-box">
            <span>Valor do Orçamento:</span>
            <span class="price">R$ {{ os.valorOrcamento.toFixed(2) }}</span>
          </div>
        </div>

        <div class="os-footer">
          <div v-if="os.estado === 'AGUARDANDO_APROVACAO'" class="approval-area">
            <p class="alert-text">⚠️ O técnico aguarda sua aprovação.</p>
            <div class="flex">
              <button @click="aprovarOrcamento(os.id)" class="btn-success w-full">
                Aprovar
              </button>
              <button @click="cancelarOS(os.id)" class="btn-danger w-full">
                Recusar
              </button>
            </div>
          </div>

          <div v-else-if="!['FINALIZADA', 'CANCELADA'].includes(os.estado)" class="text-right">
            <button @click="cancelarOS(os.id)" class="btn-link-danger">Cancelar Solicitação</button>
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
const listaOS = ref([]);

const carregarOS = async () => {
  try {
    const res = await api.get(`/os/cliente/${usuario.id}`);
    listaOS.value = res.data.reverse();
  } catch (e) { console.error(e); }
};

const formatarStatus = (status) => status.replace(/_/g, ' ');

const aprovarOrcamento = async (id) => {
  if(!confirm("Confirma a aprovação do valor?")) return;
  try {
    await api.post(`/os/${id}/aprovar`);
    carregarOS();
  } catch (e) { alert("Erro ao processar."); }
};

const cancelarOS = async (id) => {
  if(!confirm("Deseja realmente cancelar?")) return;
  try {
    await api.post(`/os/${id}/cancelar`);
    carregarOS();
  } catch (e) { alert("Erro ao cancelar."); }
};

const logout = () => { localStorage.clear(); router.push('/'); };
onMounted(carregarOS);
</script>

<style scoped>
.header { margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid var(--color-border); }
.text-muted { color: var(--color-text-muted); font-size: 0.9rem; }
.user-name { font-weight: 600; margin-right: 10px; }

.empty-state { text-align: center; padding: 4rem 0; color: var(--color-text-muted); }
.empty-icon { font-size: 3rem; margin-bottom: 1rem; }

.os-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 1.5rem; }

.os-card { padding: 0; overflow: hidden; display: flex; flex-direction: column; height: 100%; border: 1px solid var(--color-border); }

.os-header { background: #f8fafc; padding: 1rem 1.5rem; border-bottom: 1px solid var(--color-border); }
.os-id { font-family: monospace; font-weight: 700; color: var(--color-secondary); }
.os-date { font-size: 0.8rem; color: var(--color-text-muted); margin-left: 10px; }

.os-body { padding: 1.5rem; flex: 1; }
.info-row { margin-bottom: 1rem; }
.highlight { font-weight: 600; color: var(--color-text-main); }
.description { color: var(--color-secondary); font-size: 0.95rem; }

.budget-box { 
  background: #f0fdf4; 
  border: 1px dashed #86efac; 
  padding: 0.75rem; 
  border-radius: var(--radius); 
  display: flex; 
  justify-content: space-between; 
  align-items: center;
  margin-top: 1rem;
}
.price { color: #15803d; font-weight: 700; font-size: 1.1rem; }

.os-footer { padding: 1rem 1.5rem; border-top: 1px solid var(--color-border); background: #fff; }

.approval-area { background: #fff7ed; padding: 1rem; border-radius: var(--radius); border: 1px solid #ffedd5; margin: -0.5rem -0.5rem 0; }
.alert-text { font-size: 0.85rem; color: #c2410c; margin-bottom: 0.5rem; font-weight: 600; }

.w-full { width: 100%; }
.btn-link-danger { background: none; color: var(--color-danger); padding: 0; font-size: 0.9rem; text-decoration: underline; }
</style>