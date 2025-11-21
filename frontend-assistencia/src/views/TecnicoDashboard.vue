<template>
  <div class="container">
    <div class="header">
      <h2>Área Técnica: {{ usuario?.nome }}</h2>
      <button @click="logout" class="btn-logout">Sair</button>
    </div>

    <div class="grid">
      <div class="column">
        <h3>OS Disponíveis (Abertas)</h3>
        <div v-if="osAbertas.length === 0">Nenhuma OS pendente.</div>
        <div v-for="os in osAbertas" :key="os.id" class="card os-item">
          <div class="card-header">
            <strong>OS #{{ os.id.substring(0,6) }}</strong>
            <span class="badge">{{ os.hardware?.tipoHardware }}</span>
          </div>
          <p>{{ os.descricao }}</p>
          <p class="text-small">Cliente: {{ os.cliente?.nome }}</p>
          <button @click="assumirOS(os)" class="btn-action">Assumir</button>
        </div>
      </div>

      <div class="column">
        <h3>Minhas Ordens de Serviço</h3>
        <div v-for="os in minhasOS" :key="os.id" class="card os-item active-os">
          <div class="card-header">
            <strong>#{{ os.id.substring(0,6) }}</strong>
            <span :class="'status-' + os.estado">{{ os.estado }}</span>
          </div>
          
          <p class="desc">{{ os.descricao }}</p>

          <div v-if="os.estado === 'ABERTA' || os.estado === 'AGUARDANDO_ORCAMENTO'" class="action-area">
             <label>Valor Orçamento (R$):</label>
             <input type="number" v-model="valorOrcamento[os.id]" placeholder="0.00">
             <button @click="enviarOrcamento(os.id)" class="btn-primary">Enviar Orçamento</button>
          </div>

          <div v-if="os.estado === 'EM_REPARO'" class="template-method-area">
             <h4>Execução do Reparo (Passo a Passo)</h4>
             
             <div class="steps-container">
               <button 
                 :disabled="getStep(os.id) > 0" 
                 @click="avancarPasso(os.id, 1)" 
                 :class="{ done: getStep(os.id) >= 1 }">
                 1. Diagnosticar
               </button>
               
               <button 
                 :disabled="getStep(os.id) < 1 || getStep(os.id) > 1" 
                 @click="avancarPasso(os.id, 2)" 
                 :class="{ done: getStep(os.id) >= 2 }">
                 2. Separar Materiais
               </button>
               
               <button 
                 :disabled="getStep(os.id) < 2 || getStep(os.id) > 2" 
                 @click="avancarPasso(os.id, 3)" 
                 :class="{ done: getStep(os.id) >= 3 }">
                 3. Reparar
               </button>
               
               <button 
                 :disabled="getStep(os.id) < 3 || getStep(os.id) > 3" 
                 @click="avancarPasso(os.id, 4)" 
                 :class="{ done: getStep(os.id) >= 4 }">
                 4. Testes
               </button>

               <button 
                 :disabled="getStep(os.id) < 4 || getStep(os.id) > 4" 
                 @click="avancarPasso(os.id, 5)" 
                 :class="{ done: getStep(os.id) >= 5 }">
                 5. Limpeza Final
               </button>
             </div>

             <button 
               v-if="getStep(os.id) === 5" 
               @click="executarReparoReal(os.id)" 
               class="btn-success full-width">
               Confirmar Execução no Sistema
             </button>
             
             <button 
               v-if="getStep(os.id) === 6" 
               @click="finalizarOS(os.id)" 
               class="btn-finish full-width">
               Finalizar OS (Entregar)
             </button>
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

// Estado local para controlar os passos visuais de cada OS
// 0: Início, 1: Diagnóstico Feito, ..., 5: Limpeza Feita, 6: Registrado no Backend
const stepsProgress = reactive({});

const getStep = (id) => stepsProgress[id] || 0;

const avancarPasso = (id, step) => {
  stepsProgress[id] = step;
};

const carregarDados = async () => {
  try {
    const resAbertas = await api.get('/os/estado/ABERTA');
    osAbertas.value = resAbertas.data;

    const resMinhas = await api.get(`/os/tecnico/${usuario.id}`);
    minhasOS.value = resMinhas.data;
  } catch (e) { console.error("Erro ao carregar dados", e); }
};

const assumirOS = async (os) => {
    try {
        const dto = {
            valorOrcamento: os.valorOrcamento,
            descricao: os.descricao,
            clienteId: os.cliente.id,
            hardwareId: os.hardware.numeroSerie,
            tecnicoId: usuario.id // Importante: Vincula o técnico
        };
        await api.put(`/os/${os.id}`, dto);
        
        // Muda estado para AGUARDANDO_ORCAMENTO (se o backend não fizer auto)
        // Como seu backend requer 'orcamentar' para mudar estado, aqui apenas vinculamos.
        // O ideal seria ter um endpoint '/os/{id}/assumir', mas usamos o update.
        
        alert("OS Assumida! Agora defina o orçamento.");
        carregarDados();
    } catch (e) { alert("Erro ao assumir OS"); }
};

const enviarOrcamento = async (id) => {
    if (!valorOrcamento.value[id]) return alert("Digite um valor!");
    
    const osAtual = minhasOS.value.find(o => o.id === id);
    const dto = {
        valorOrcamento: valorOrcamento.value[id],
        descricao: osAtual.descricao,
        clienteId: osAtual.cliente.id,
        hardwareId: osAtual.hardware.numeroSerie,
        tecnicoId: usuario.id
    };
    await api.put(`/os/${id}`, dto);
    await api.post(`/os/${id}/orcamentar`); // Muda estado
    carregarDados();
};

const executarReparoReal = async (id) => {
    try {
        // Chama o backend que roda o Template Method completo
        
        await api.post(`/os/${id}/executar`);
        stepsProgress[id] = 6; // Marca como pronto para finalizar
    } catch (e) { alert("Erro na execução"); }
};

const finalizarOS = async (id) => {
    await api.post(`/os/${id}/finalizar`);
    carregarDados();
    delete stepsProgress[id]; // Limpa progresso
};

const logout = () => { localStorage.clear(); router.push('/'); };
onMounted(carregarDados);
</script>

<style scoped>
.grid { display: flex; gap: 20px; }
.column { flex: 1; background: #f8f9fa; padding: 15px; border-radius: 8px; }
.card { background: white; padding: 15px; margin-bottom: 15px; border-radius: 6px; border: 1px solid #e9ecef; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.card-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.badge { background: #eee; padding: 2px 6px; border-radius: 4px; font-size: 0.8em; }
.btn-action { background: #3498db; color: white; border: none; padding: 8px; width: 100%; cursor: pointer; border-radius: 4px; }
.btn-primary { background: #2980b9; color: white; border: none; padding: 8px; cursor: pointer; margin-left: 5px; border-radius: 4px;}
.btn-success { background: #27ae60; color: white; border: none; padding: 10px; cursor: pointer; border-radius: 4px; margin-top: 10px;}
.btn-finish { background: #2c3e50; color: white; border: none; padding: 10px; cursor: pointer; border-radius: 4px; margin-top: 10px;}
.steps-container { display: flex; flex-direction: column; gap: 5px; margin-top: 10px; }
.steps-container button { padding: 8px; text-align: left; border: 1px solid #ccc; background: #fff; cursor: pointer; border-radius: 4px; transition: all 0.3s; }
.steps-container button.done { background-color: #d5f5e3; border-color: #27ae60; color: #27ae60; }
.steps-container button:disabled { opacity: 0.5; cursor: not-allowed; }
.full-width { width: 100%; }
</style>