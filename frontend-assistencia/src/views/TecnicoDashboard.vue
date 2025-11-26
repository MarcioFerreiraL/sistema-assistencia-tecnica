<template>
  <div class="container-fluid">
    <div class="top-bar">
      <h1>Área Técnica</h1>
      <div class="user-area">
        <span>Técnico: <strong>{{ usuario?.nome }}</strong></span>
        <button @click="logout" class="btn-outline">Sair</button>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="col-panel">
        <div class="panel-header">
          <h3><span class="icon">📋</span> OS Disponíveis</h3>
          <span class="badge-count">{{ osAbertas.length }}</span>
        </div>
        
        <div class="scroll-area">
          <div v-if="osAbertas.length === 0" class="empty-state">Não há OS pendente.</div>
          
          <div v-for="os in osAbertas" :key="os.id" class="task-card">
            <div class="task-header">
              <span class="task-id">#{{ os.id.substring(0,6) }}</span>
              <span class="chip-type">{{ os.hardware?.tipoHardware }}</span>
            </div>
            <p class="task-desc">{{ os.descricao }}</p>
            <div class="task-meta">Cliente: {{ os.cliente?.nome }}</div>
            <button @click="assumirOS(os)" class="btn-blue full-width mt-2">Assumir Serviço</button>
          </div>
        </div>
      </div>

      <div class="col-panel main-panel">
        <div class="panel-header">
          <h3><span class="icon">🔧</span> Em Andamento (Minhas OS)</h3>
        </div>

        <div v-if="minhasOS.length === 0" class="empty-state-large">
          <p>Você não possui serviços em andamento.</p>
        </div>

        <div v-for="os in minhasOS" :key="os.id" class="active-card">
          <div class="active-header">
            <div class="active-title">
              <strong>OS #{{ os.id.substring(0,8) }}</strong>
              <span :class="['status-badge', os.estado]">{{ os.estado.replace(/_/g, ' ') }}</span>
            </div>
            <div class="device-info">{{ os.hardware?.tipoHardware }} - {{ os.cliente?.nome }}</div>
          </div>

          <div class="active-body">
            <label class="label-muted">Problema Relatado</label>
            <p class="desc-box">{{ os.descricao }}</p>

            <div v-if="os.estado === 'ABERTA' || os.estado === 'AGUARDANDO_ORCAMENTO'" class="action-row">
              <div class="input-with-btn">
                <span class="currency">R$</span>
                <input type="number" v-model="valorOrcamento[os.id]" placeholder="0.00">
                <button @click="enviarOrcamento(os.id)" class="btn-blue">Enviar Orçamento</button>
              </div>
            </div>

            <div v-if="os.estado === 'EM_REPARO'" class="notes-section">
              <label>Registro do Serviço (Laudo Técnico)</label>
              <textarea 
                v-model="os.observacoesTecnicas" 
                rows="5" 
                placeholder="Descreva o que foi feito no equipamento (ex: Troca de tela, testes realizados...)"
                class="text-area-full"
              ></textarea>
              
              <div class="workflow-actions">
                <button @click="salvarNotas(os)" class="btn-outline">
                  💾 Salvar Notas
                </button>
                
                <button @click="finalizarOS(os.id)" class="btn-dark">
                  🏁 Finalizar Serviço
                </button>
              </div>
            </div>
              
              <div class="workflow-actions">
                <button v-if="getStep(os.id) === 5" @click="executarReparoReal(os.id)" class="btn-success full-width">
                  ✅ Confirmar Execução Técnica
                </button>
                <button v-if="getStep(os.id) === 6" @click="finalizarOS(os.id)" class="btn-dark full-width">
                  🏁 Finalizar e Entregar
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const usuario = JSON.parse(localStorage.getItem('usuario'));
const osAbertas = ref([]);
const minhasOS = ref([]);
const valorOrcamento = ref({});
const stepsProgress = reactive({});

const passosReparo = ["Diagnóstico", "Materiais", "Reparo", "Testes", "Limpeza"];

const getStep = (id) => stepsProgress[id] || 0;
const avancarPasso = (id, step) => { stepsProgress[id] = step; };

const carregarDados = async () => {
  try {
    // Carrega todas as abertas e filtra as que não são do usuário atual
    const resAbertas = await api.get('/os/estado/ABERTA');
    osAbertas.value = resAbertas.data; 
    // Carrega as do técnico
    const resMinhas = await api.get(`/os/tecnico/${usuario.id}`);
    minhasOS.value = resMinhas.data;
  } catch (e) { console.error(e); }
};

const assumirOS = async (os) => {
  try {
    const dto = { ...os, clienteId: os.cliente.id, hardwareId: os.hardware.numeroSerie, tecnicoId: usuario.id };
    await api.put(`/os/${os.id}`, dto);
    await carregarDados();
  } catch (e) { alert("Erro ao assumir."); }
};

const enviarOrcamento = async (id) => {
  if (!valorOrcamento.value[id]) return alert("Valor inválido");
  const os = minhasOS.value.find(o => o.id === id);
  try {
    const dto = { ...os, valorOrcamento: valorOrcamento.value[id], clienteId: os.cliente.id, hardwareId: os.hardware.numeroSerie, tecnicoId: usuario.id };
    await api.put(`/os/${id}`, dto);
    await api.post(`/os/${id}/orcamentar`);
    carregarDados();
  } catch (e) { alert("Erro ao enviar."); }
};

const executarReparoReal = async (id) => {
  try { await api.post(`/os/${id}/executar`); stepsProgress[id] = 6; } 
  catch (e) { alert("Erro na execução."); }
};

const finalizarOS = async (id) => {
  try { await api.post(`/os/${id}/finalizar`); carregarDados(); delete stepsProgress[id]; } 
  catch (e) { alert("Erro ao finalizar."); }
};

const logout = () => { localStorage.clear(); router.push('/'); };
onMounted(carregarDados);

const salvarNotas = async (os) => {
  try {
    // Envia apenas o texto para o backend
    await api.patch(`/os/${os.id}/observacoes`, os.observacoesTecnicas, {
      headers: { 'Content-Type': 'text/plain' } // Importante para enviar String pura
    });
    alert("Anotações salvas com sucesso!");
  } catch (e) {
    alert("Erro ao salvar anotações.");
    console.error(e);
  }
};
</script>

<style scoped>
/* Global Layout */
.container-fluid { font-family: 'Inter', sans-serif; background-color: #f0f2f5; min-height: 100vh; }
.top-bar { background: #fff; padding: 15px 40px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eef2f6; box-shadow: 0 2px 4px rgba(0,0,0,0.02); }
.top-bar h1 { font-size: 1.4rem; color: #1e293b; margin: 0; font-weight: 600; }
.user-area { font-size: 0.9rem; color: #64748b; display: flex; gap: 15px; align-items: center; }

/* Dashboard Grid */
.dashboard-content { display: grid; grid-template-columns: 350px 1fr; gap: 25px; padding: 25px 40px; align-items: start; }
.col-panel { background: transparent; }
.main-panel { min-width: 0; }

.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.panel-header h3 { font-size: 1.1rem; color: #475569; font-weight: 600; display: flex; align-items: center; gap: 8px; }
.badge-count { background: #e2e8f0; color: #334155; padding: 2px 8px; border-radius: 12px; font-size: 0.8rem; font-weight: 700; }

/* Cards de OS Abertas */
.scroll-area { display: flex; flex-direction: column; gap: 15px; }
.task-card { background: #fff; padding: 15px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.03); border: 1px solid #eef2f6; transition: transform 0.2s; }
.task-card:hover { transform: translateY(-2px); border-color: #2563eb; }
.task-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.task-id { font-family: monospace; font-weight: 700; color: #2563eb; }
.chip-type { background: #f1f5f9; padding: 2px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: 600; color: #475569; }
.task-desc { font-size: 0.9rem; color: #334155; margin-bottom: 8px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.task-meta { font-size: 0.8rem; color: #94a3b8; }

/* Cards Ativos (Minhas OS) */
.active-card { background: #fff; border-radius: 12px; border: 1px solid #e2e8f0; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); overflow: hidden; margin-bottom: 20px; }
.active-header { background: #f8fafc; padding: 15px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
.active-title { display: flex; align-items: center; gap: 12px; font-size: 1rem; color: #1e293b; }
.status-badge { font-size: 0.75rem; padding: 4px 10px; border-radius: 99px; font-weight: 700; text-transform: uppercase; }
.status-badge.ABERTA { background: #dbeafe; color: #1e40af; }
.status-badge.EM_REPARO { background: #f3e8ff; color: #6b21a8; }
.status-badge.AGUARDANDO_ORCAMENTO { background: #ffedd5; color: #9a3412; }
.device-info { font-size: 0.85rem; color: #64748b; font-weight: 500; }

.active-body { padding: 20px; }
.label-muted { font-size: 0.75rem; text-transform: uppercase; color: #94a3b8; font-weight: 700; margin-bottom: 5px; display: block; }
.desc-box { background: #f8fafc; padding: 12px; border-radius: 6px; color: #334155; font-size: 0.95rem; border: 1px solid #f1f5f9; margin-bottom: 20px; }

/* Actions */
.input-with-btn { display: flex; gap: 0; border: 1px solid #cbd5e1; border-radius: 6px; overflow: hidden; max-width: 400px; }
.currency { background: #f1f5f9; padding: 10px 15px; color: #64748b; font-weight: 600; border-right: 1px solid #cbd5e1; }
.input-with-btn input { border: none; padding: 10px; flex: 1; outline: none; }
.input-with-btn button { border-radius: 0; margin: 0; }

/* Workflow Visual */
.workflow-container { margin-top: 10px; border-top: 1px dashed #e2e8f0; padding-top: 20px; }
.steps-track { display: flex; justify-content: space-between; margin-bottom: 20px; position: relative; }
.track-step { display: flex; flex-direction: column; align-items: center; gap: 8px; cursor: pointer; position: relative; z-index: 1; flex: 1; opacity: 0.5; transition: opacity 0.3s; }
.track-step.completed, .track-step.current { opacity: 1; }
.track-step .circle { width: 32px; height: 32px; background: #f1f5f9; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; color: #64748b; transition: all 0.3s; border: 2px solid #e2e8f0; }
.track-step span { font-size: 0.75rem; font-weight: 600; color: #64748b; }

.track-step.completed .circle { background: #22c55e; color: white; border-color: #22c55e; }
.track-step.current .circle { background: #2563eb; color: white; border-color: #2563eb; transform: scale(1.1); }

/* Botões */
.btn-blue { background: #2563eb; color: white; border: none; padding: 10px 16px; font-weight: 600; border-radius: 6px; cursor: pointer; }
.btn-blue:hover { background: #1d4ed8; }
.btn-outline { background: transparent; border: 1px solid #cbd5e1; padding: 6px 12px; border-radius: 6px; cursor: pointer; }
.btn-success { background: #22c55e; color: white; border: none; padding: 12px; border-radius: 6px; font-weight: 700; cursor: pointer; }
.btn-dark { background: #1e293b; color: white; border: none; padding: 12px; border-radius: 6px; font-weight: 700; cursor: pointer; }
.full-width { width: 100%; }
.mt-2 { margin-top: 10px; }

.notes-section {
  margin-top: 15px;
  border-top: 1px solid #e2e8f0;
  padding-top: 15px;
}
.text-area-full {
  width: 100%;
  padding: 10px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  margin-bottom: 10px;
  font-family: inherit;
  resize: vertical;
}
.workflow-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}
</style>