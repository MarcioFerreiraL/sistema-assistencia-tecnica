<template>
  <div class="container-fluid">
    <div class="top-bar">
      <h1>Painel Administrativo</h1>
      <div class="user-area">
        <span>Admin: <strong>{{ usuario?.nome }}</strong></span>
        <button @click="logout" class="btn-outline">Sair</button>
      </div>
    </div>

    <div class="nav-menu">
      <button 
        v-for="tab in tabs" 
        :key="tab.key"
        :class="['nav-item', { 'active': abaAtual === tab.key }]"
        @click="mudarAba(tab.key)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div class="content-wrapper">
      <div class="section-header">
        <h2>Gerenciar {{ tabs.find(t => t.key === abaAtual).label }}</h2>
        <button v-if="modo === 'lista'" class="btn-blue" @click="prepararCadastro">
          + Novo {{ tabs.find(t => t.key === abaAtual).singular }}
        </button>
      </div>

      <div v-if="modo === 'lista'" class="table-box">
        <div v-if="carregando" class="loading-state">
          <div class="spinner"></div> Carregando dados...
        </div>
        
        <table v-else class="clean-table">
          <thead>
            <tr>
              <th v-for="col in colunasAtuais" :key="col.key">{{ col.label }}</th>
              <th class="text-right">AÇÕES</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in listaDados" :key="item.id || item.numeroSerie">
              <td v-for="col in colunasAtuais" :key="col.key">
                <span v-if="col.key === 'estado'" :class="['status-tag', item.estado]">
                  {{ item.estado }}
                </span>
                <span v-else-if="col.key === 'valorOrcamento'">
                  {{ formatCurrency(item[col.key]) }}
                </span>
                <span v-else>{{ resolverValor(item, col.key) }}</span>
              </td>
              <td class="actions-col">
                <button class="icon-btn edit" title="Editar" @click="prepararEdicao(item)">✏️</button>
                <button class="icon-btn delete" title="Excluir" @click="excluirItem(item)">🗑️</button>
              </td>
            </tr>
            <tr v-if="listaDados.length === 0">
              <td :colspan="colunasAtuais.length + 1" class="empty-msg">Nenhum registro encontrado.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else class="form-box">
        <div class="form-grid">
          <div v-for="campo in camposFormulario" :key="campo.key" class="input-group">
            <label>{{ campo.label }}</label>
            <select v-if="campo.type === 'select'" v-model="itemEmEdicao[campo.key]">
              <option v-for="opt in campo.options" :key="opt" :value="opt">{{ opt }}</option>
            </select>
            <input v-else :type="campo.type || 'text'" v-model="itemEmEdicao[campo.key]">
          </div>
        </div>
        <div class="form-buttons">
          <button class="btn-outline" @click="cancelarEdicao" :disabled="salvando">Cancelar</button>
          <button class="btn-blue" @click="salvarItem" :disabled="salvando">
            <span v-if="salvando">Salvando...</span>
            <span v-else>Salvar</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';
import { formatCurrency } from '../utils/formatters'; // Importando formatador
import { useToast } from '../composables/useToast';   // Importando Toast

const router = useRouter();
const toast = useToast(); // Usando o hook
const usuario = JSON.parse(localStorage.getItem('usuario'));

const abaAtual = ref('clientes');
const modo = ref('lista');
const listaDados = ref([]);
const itemEmEdicao = ref({});
const carregando = ref(false);
const salvando = ref(false); // Estado de loading para salvar

// ... (Configurações de tabs, colunas e campos mantidos iguais ao anterior) ...
// Apenas certifique-se de que as configurações de abas e campos estejam aqui.
// Vou omitir para brevidade, mas elas são idênticas à resposta anterior.
const tabs = [
  { key: 'clientes', label: 'Clientes', singular: 'Cliente', endpoint: '/clientes' },
  { key: 'atendentes', label: 'Atendentes', singular: 'Atendente', endpoint: '/atendente' },
  { key: 'tecnicos', label: 'Técnicos', singular: 'Técnico', endpoint: '/tecnicos' },
  { key: 'os', label: 'Ordens de Serviço', singular: 'OS', endpoint: '/os' },
  { key: 'pecas', label: 'Peças', singular: 'Peça', endpoint: '/pecas' },
  { key: 'hardwares', label: 'Hardwares', singular: 'Hardware', endpoint: '/hardware' }
];

const colunasPorAba = {
  clientes: [{ key: 'id', label: 'ID' }, { key: 'nome', label: 'Nome' }, { key: 'cpf', label: 'CPF' }],
  atendentes: [{ key: 'id', label: 'ID' }, { key: 'nome', label: 'Nome' }, { key: 'cpf', label: 'CPF' }],
  tecnicos: [{ key: 'id', label: 'ID' }, { key: 'nome', label: 'Nome' }, { key: 'cpf', label: 'CPF' }],
  os: [{ key: 'id', label: 'ID' }, { key: 'descricao', label: 'Descrição' }, { key: 'estado', label: 'Status' }, { key: 'valorOrcamento', label: 'Valor' }],
  pecas: [{ key: 'numeroSerie', label: 'ID' }, { key: 'nome', label: 'Nome' }, { key: 'tipoPeca', label: 'Tipo' }],
  hardwares: [{ key: 'numeroSerie', label: 'Serial' }, { key: 'tipoHardware', label: 'Tipo' }, { key: 'cliente.nome', label: 'Dono' }]
};

const camposPorAba = {
  clientes: [ { key: 'nome', label: 'Nome' }, { key: 'cpf', label: 'CPF' }, { key: 'dataNascimento', label: 'Nascimento', type: 'date' }, { key: 'endereco', label: 'Endereço' } ],
  atendentes: [ { key: 'nome', label: 'Nome' }, { key: 'cpf', label: 'CPF' }, { key: 'dataNascimento', label: 'Nascimento', type: 'date' }, { key: 'endereco', label: 'Endereço' } ],
  tecnicos: [ { key: 'nome', label: 'Nome' }, { key: 'cpf', label: 'CPF' }, { key: 'dataNascimento', label: 'Nascimento', type: 'date' }, { key: 'endereco', label: 'Endereço' } ],
  pecas: [{ key: 'nome', label: 'Nome' }, { key: 'tipoPeca', label: 'Tipo', type: 'select', options: ['PROCESSADOR', 'MEMORIA_RAM', 'TELA', 'BATERIA'] }],
  hardwares: [{ key: 'tipoHardware', label: 'Tipo', type: 'select', options: ['NOTEBOOK', 'CELULAR', 'COMPUTADOR'] }, { key: 'clienteId', label: 'ID Cliente', type: 'number' }],
  os: [ { key: 'descricao', label: 'Descrição' }, { key: 'valorOrcamento', label: 'Valor', type: 'number' }, { key: 'clienteId', label: 'ID Cliente', type: 'number' }, { key: 'hardwareId', label: 'ID Hardware', type: 'number' }, { key: 'tecnicoId', label: 'ID Técnico', type: 'number' } ]
};

const colunasAtuais = computed(() => colunasPorAba[abaAtual.value] || []);
const camposFormulario = computed(() => camposPorAba[abaAtual.value] || []);

const resolverValor = (item, key) => key.split('.').reduce((obj, k) => (obj || {})[k], item);

const carregarDados = async () => {
  carregando.value = true;
  listaDados.value = [];
  const endpoint = tabs.find(t => t.key === abaAtual.value).endpoint;
  try {
    const res = await api.get(endpoint);
    listaDados.value = res.data;
  } catch (e) {
    toast.error("Erro ao carregar dados.");
  } finally {
    carregando.value = false;
  }
};

const mudarAba = (novaAba) => {
  if (abaAtual.value !== novaAba) {
    abaAtual.value = novaAba;
    modo.value = 'lista';
    carregarDados();
  }
};

const prepararCadastro = () => { itemEmEdicao.value = {}; modo.value = 'formulario'; };
const prepararEdicao = (item) => { 
  itemEmEdicao.value = JSON.parse(JSON.stringify(item));
  if(abaAtual.value === 'hardwares' && item.cliente) itemEmEdicao.value.clienteId = item.cliente.id;
  modo.value = 'formulario'; 
};
const cancelarEdicao = () => { modo.value = 'lista'; };

const salvarItem = async () => {
  salvando.value = true; // Ativa loader do botão
  const endpoint = tabs.find(t => t.key === abaAtual.value).endpoint;
  const id = itemEmEdicao.value.id || itemEmEdicao.value.numeroSerie;
  
  try {
    if (id) await api.put(`${endpoint}/${id}`, itemEmEdicao.value);
    else await api.post(endpoint, itemEmEdicao.value);
    
    toast.success("Registro salvo com sucesso!"); // Feedback elegante
    cancelarEdicao();
    carregarDados();
  } catch (e) {
    toast.error("Erro ao salvar: " + (e.response?.data || e.message));
  } finally {
    salvando.value = false; // Desativa loader
  }
};

const excluirItem = async (item) => {
  if(!confirm("Deseja realmente excluir este item?")) return; // Ainda usando confirm nativo para segurança crítica, mas poderia ser um Modal
  
  const endpoint = tabs.find(t => t.key === abaAtual.value).endpoint;
  try {
    await api.delete(`${endpoint}/${item.id || item.numeroSerie}`);
    toast.success("Item excluído.");
    carregarDados();
  } catch (e) {
    toast.error("Não foi possível excluir. Verifique dependências.");
  }
};

const logout = () => { localStorage.clear(); router.push('/'); };
onMounted(carregarDados);
</script>

<style scoped>
/* CSS do AdminDashboard atualizado com animação de loading */
.spinner {
  border: 3px solid #f3f3f3;
  border-top: 3px solid var(--color-primary);
  border-radius: 50%;
  width: 20px;
  height: 20px;
  animation: spin 1s linear infinite;
  display: inline-block;
  vertical-align: middle;
  margin-right: 10px;
}

@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* Mantém o restante do CSS profissional já definido anteriormente */
.container-fluid { font-family: 'Inter', sans-serif; background-color: #f8f9fa; min-height: 100vh; padding-bottom: 40px; }
.top-bar { background: #fff; padding: 15px 40px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eef2f6; }
.top-bar h1 { font-size: 1.5rem; color: #344767; margin: 0; font-weight: 600; }
.user-area { display: flex; align-items: center; gap: 15px; color: #67748e; font-size: 0.9rem; }
.nav-menu { padding: 20px 40px 0; display: flex; gap: 10px; }
.nav-item { background: none; border: none; padding: 10px 20px; color: #67748e; font-weight: 500; cursor: pointer; border-radius: 20px; transition: all 0.3s; }
.nav-item:hover { color: #2563eb; background: #eef2f6; }
.nav-item.active { background-color: #2563eb; color: #fff; box-shadow: 0 4px 6px rgba(37, 99, 235, 0.2); }
.content-wrapper { margin: 20px 40px; background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.03); border: 1px solid #eef2f6; min-height: 400px; }
.section-header { padding: 25px 30px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #f0f2f5; }
.btn-blue { background: #2563eb; color: white; border: none; padding: 10px 20px; border-radius: 6px; font-weight: 600; cursor: pointer; transition: background 0.2s; font-size: 0.9rem; }
.btn-blue:disabled { opacity: 0.7; cursor: not-allowed; }
.btn-outline { background: transparent; border: 1px solid #cbd5e1; color: #64748b; padding: 6px 16px; border-radius: 6px; cursor: pointer; }
.table-box { padding: 0; }
.clean-table { width: 100%; border-collapse: collapse; }
.clean-table th { text-align: left; padding: 15px 30px; font-size: 0.75rem; color: #8392ab; font-weight: 700; border-bottom: 1px solid #f0f2f5; background: #f8f9fa; }
.clean-table td { padding: 16px 30px; font-size: 0.9rem; color: #344767; border-bottom: 1px solid #f0f2f5; vertical-align: middle; }
.icon-btn { background: none; border: none; cursor: pointer; padding: 5px; font-size: 1.1rem; opacity: 0.7; transition: opacity 0.2s; }
.icon-btn:hover { opacity: 1; }
.form-box { padding: 30px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.input-group { display: flex; flex-direction: column; gap: 8px; margin-bottom: 15px; }
.input-group input, .input-group select { padding: 10px; border: 1px solid #d2d6da; border-radius: 6px; outline: none; transition: border-color 0.2s; }
.form-buttons { margin-top: 20px; display: flex; justify-content: flex-end; gap: 10px; }
.loading-state { padding: 40px; text-align: center; color: #8392ab; }
.status-tag { padding: 4px 10px; border-radius: 4px; font-size: 0.75rem; font-weight: 700; }
.status-tag.ABERTA { background: #e0f2fe; color: #0369a1; }
.status-tag.FINALIZADA { background: #dcfce7; color: #15803d; }
</style>