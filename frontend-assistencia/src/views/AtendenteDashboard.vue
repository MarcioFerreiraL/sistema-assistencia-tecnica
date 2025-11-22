<template>
  <div class="container-fluid">
    <div class="top-bar">
      <h1>Painel de Atendimento</h1>
      <div class="user-area">
        <span>Olá, <strong>{{ usuario?.nome || 'Atendente' }}</strong></span>
        <button @click="logout" class="btn-outline">Sair</button>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="left-col">
        
        <div class="card action-card">
          <div class="card-header">
            <div class="icon-bg blue">👤</div>
            <h3>Novo Cliente</h3>
          </div>
          <div class="card-body">
            <div class="form-group">
              <label>Nome Completo</label>
              <input v-model="novoCliente.nome" placeholder="Ex: Maria Silva">
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label>CPF</label>
                <input 
                  v-model="novoCliente.cpf" 
                  placeholder="Apenas números" 
                  @input="validarCPFEmTempoReal"
                  maxlength="11"
                  :class="{ 'input-error': errosValidacao.cpf }"
                >
                <span v-if="errosValidacao.cpf" class="error-text">{{ errosValidacao.cpf }}</span>
              </div>
              <div class="form-group">
                <label>Nascimento</label>
                <input type="date" v-model="novoCliente.dataNascimento">
              </div>
            </div>
            
            <div class="form-group">
              <label>Endereço</label>
              <input v-model="novoCliente.endereco" placeholder="Rua, Número, Bairro">
            </div>
            
            <button @click="cadastrarCliente" class="btn-blue w-full" :disabled="loadingCadastro">
              <span v-if="loadingCadastro" class="spinner-sm"></span>
              <span v-else>Cadastrar Cliente</span>
            </button>
          </div>
        </div>

        <div class="card action-card">
          <div class="card-header">
            <div class="icon-bg green">📝</div>
            <h3>Abrir Ordem de Serviço</h3>
          </div>
          <div class="card-body">
            <label>Buscar Cliente (CPF)</label>
            <div class="search-box">
              <input 
                v-model="novaOS.cpfCliente" 
                placeholder="Digite o CPF..."
                @input="filtrarNumeros('novaOS', 'cpfCliente')"
                @keyup.enter="buscarClienteParaOS"
              >
              <button @click="buscarClienteParaOS" class="btn-icon-search" :disabled="loadingBusca">
                <span v-if="loadingBusca">...</span>
                <span v-else>🔍</span>
              </button>
            </div>
            
            <transition name="fade">
              <div v-if="clienteSelecionado" class="selected-client-box">
                <div class="client-info-header">
                  <div>
                    <span class="client-name">{{ clienteSelecionado.nome }}</span>
                    <small class="client-cpf">CPF: {{ clienteSelecionado.cpf }}</small>
                  </div>
                  <button @click="limparSelecaoCliente" class="btn-close-sm">×</button>
                </div>
                
                <div class="form-group mt-3">
                  <label>Equipamento</label>
                  <select v-model="novaOS.tipoHardware">
                    <option value="NOTEBOOK">Notebook</option>
                    <option value="CELULAR">Celular</option>
                    <option value="COMPUTADOR">Computador</option>
                    <option value="OUTROS">Outros</option>
                  </select>
                </div>
                
                <div class="form-group">
                  <label>Defeito Relatado</label>
                  <textarea 
                    v-model="novaOS.descricao" 
                    placeholder="Descreva o problema com detalhes..." 
                    rows="3"
                  ></textarea>
                </div>
                
                <button @click="abrirOS" class="btn-success w-full" :disabled="loadingOS">
                  <span v-if="loadingOS" class="spinner-sm"></span>
                  <span v-else>Gerar Ordem de Serviço</span>
                </button>
              </div>
            </transition>
          </div>
        </div>
      </div>

      <div class="right-col">
        <div class="card table-card">
          <div class="card-header-simple">
            <h3>Últimas Solicitações</h3>
            <button @click="carregarOS" class="btn-refresh" title="Atualizar Lista">↻</button>
          </div>
          
          <div class="table-responsive">
            <table class="clean-table">
              <thead>
                <tr>
                  <th>OS</th>
                  <th>Cliente / Equipamento</th>
                  <th>Status</th>
                  <th class="text-right">Ação</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="os in listaOS" :key="os.id">
                  <td class="font-mono">#{{ os.id.substring(0,6) }}</td>
                  <td>
                    <div class="cell-content">
                      <span class="font-bold">{{ os.cliente?.nome.split(' ')[0] }}</span>
                      <small class="text-muted">{{ os.hardware?.tipoHardware }}</small>
                    </div>
                  </td>
                  <td>
                    <span :class="['status-tag', os.estado]">{{ formatarStatus(os.estado) }}</span>
                  </td>
                  <td class="text-right">
                    <button class="btn-outline btn-xs" @click="verDetalhes(os)">Ver</button>
                  </td>
                </tr>
                <tr v-if="listaOS.length === 0">
                  <td colspan="4" class="empty-msg">Nenhum registro recente.</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <div v-if="modalAberto" class="modal-backdrop" @click.self="fecharModal">
      <div class="modal-window">
        <div class="modal-header">
          <h3>Detalhes da OS <span class="highlight">#{{ osSelecionada.id.substring(0,8) }}</span></h3>
          <button @click="fecharModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="info-grid">
            <div class="info-item full-width status-banner" :class="'bg-' + osSelecionada.estado">
              <span>Status: <strong>{{ formatarStatus(osSelecionada.estado) }}</strong></span>
            </div>
            
            <div class="info-item">
              <label>Cliente</label>
              <p>{{ osSelecionada.cliente?.nome }}</p>
              <small class="text-muted">{{ osSelecionada.cliente?.cpf }}</small>
            </div>
            
            <div class="info-item">
              <label>Equipamento</label>
              <p>{{ osSelecionada.hardware?.tipoHardware }}</p>
              <small class="text-muted">ID: {{ osSelecionada.hardware?.numeroSerie }}</small>
            </div>

            <div class="info-item full-width">
              <label>Descrição do Problema</label>
              <p class="text-box">{{ osSelecionada.descricao }}</p>
            </div>

            <div class="info-item">
              <label>Técnico Responsável</label>
              <p v-if="osSelecionada.tecnico">{{ osSelecionada.tecnico.nome }}</p>
              <p v-else class="text-warning">Aguardando atribuição</p>
            </div>
            
            <div class="info-item">
              <label>Orçamento</label>
              <p v-if="osSelecionada.valorOrcamento > 0" class="price">
                {{ formatCurrency(osSelecionada.valorOrcamento) }}
              </p>
              <p v-else class="text-muted">-</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="fecharModal" class="btn-secondary">Fechar</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';
import { useToast } from '../composables/useToast';
import { formatCurrency } from '../utils/formatters';

const router = useRouter();
const toast = useToast();
const usuario = JSON.parse(localStorage.getItem('usuario'));

// Estados de Dados
const listaOS = ref([]);
const novoCliente = ref({ nome: '', cpf: '', dataNascimento: '', endereco: '' });
const novaOS = ref({ cpfCliente: '', tipoHardware: 'NOTEBOOK', descricao: '' });
const clienteSelecionado = ref(null);
const osSelecionada = ref({});

// Estados de UI e Validação
const modalAberto = ref(false);
const errosValidacao = reactive({ cpf: '' });
const loadingCadastro = ref(false);
const loadingBusca = ref(false);
const loadingOS = ref(false);

// --- Funções Auxiliares ---

const formatarStatus = (status) => status ? status.replace(/_/g, ' ') : '';

const filtrarNumeros = (obj, campo) => {
  const target = obj === 'novoCliente' ? novoCliente : novaOS;
  target.value[campo] = target.value[campo].replace(/\D/g, '');
};

const limparSelecaoCliente = () => {
  clienteSelecionado.value = null;
  novaOS.value.cpfCliente = '';
  novaOS.value.descricao = '';
};

// --- Validação em Tempo Real ---

const validarCPFEmTempoReal = () => {
  // Remove caracteres não numéricos
  novoCliente.value.cpf = novoCliente.value.cpf.replace(/\D/g, '');
  const cpf = novoCliente.value.cpf;

  if (cpf.length > 0 && cpf.length < 11) {
    errosValidacao.cpf = 'CPF deve ter 11 dígitos';
  } else {
    errosValidacao.cpf = '';
  }
};

// --- Ações de API ---

const carregarOS = async () => {
  try {
    const res = await api.get('/os');
    listaOS.value = res.data.reverse(); // Mostra as mais recentes primeiro
  } catch (e) {
    console.error("Erro ao carregar lista", e);
    toast.error("Não foi possível atualizar a lista de solicitações.");
  }
};

const cadastrarCliente = async () => {
  // Validação final antes de enviar
  if (!novoCliente.value.nome || novoCliente.value.cpf.length !== 11) {
    toast.warning("Preencha todos os dados corretamente.");
    return;
  }

  loadingCadastro.value = true;
  try {
    await api.post('/clientes', novoCliente.value);
    toast.success('Cliente cadastrado com sucesso!');
    // Limpa o formulário
    novoCliente.value = { nome: '', cpf: '', dataNascimento: '', endereco: '' };
    errosValidacao.cpf = '';
  } catch (e) {
    const msg = e.response?.data?.message || e.message;
    toast.error(`Erro ao cadastrar: ${msg}`);
  } finally {
    loadingCadastro.value = false;
  }
};

const buscarClienteParaOS = async () => {
  if (!novaOS.value.cpfCliente) {
    toast.warning("Digite um CPF para buscar.");
    return;
  }

  loadingBusca.value = true;
  try {
    const res = await api.get(`/clientes/cpf/${novaOS.value.cpfCliente}`);
    clienteSelecionado.value = res.data;
    toast.info("Cliente encontrado.");
  } catch (e) {
    toast.error("Cliente não encontrado. Verifique o CPF ou cadastre um novo.");
    clienteSelecionado.value = null;
  } finally {
    loadingBusca.value = false;
  }
};

const abrirOS = async () => {
  if (!clienteSelecionado.value) return;
  if (!novaOS.value.descricao) {
    toast.warning("A descrição do defeito é obrigatória.");
    return;
  }

  loadingOS.value = true;
  try {
    // 1. Cria o Hardware vinculado ao cliente
    const resHard = await api.post('/hardware', { 
      tipoHardware: novaOS.value.tipoHardware, 
      clienteId: clienteSelecionado.value.id 
    });

    // 2. Cria a OS vinculada ao Hardware e Cliente
    await api.post('/os', {
      valorOrcamento: 0.0,
      descricao: novaOS.value.descricao,
      clienteId: clienteSelecionado.value.id,
      hardwareId: resHard.data.numeroSerie
    });

    toast.success('Ordem de Serviço aberta com sucesso!');
    carregarOS(); // Atualiza a tabela
    limparSelecaoCliente(); // Reseta o fluxo
  } catch (e) {
    toast.error('Erro ao processar abertura de OS.');
  } finally {
    loadingOS.value = false;
  }
};

// --- Modal ---
const verDetalhes = (os) => { 
  osSelecionada.value = os; 
  modalAberto.value = true; 
};
const fecharModal = () => { 
  modalAberto.value = false; 
};

const logout = () => { 
  localStorage.clear(); 
  router.push('/'); 
};

onMounted(carregarOS);
</script>

<style scoped>
/* Layout Global */
.container-fluid { font-family: 'Inter', sans-serif; background-color: #f8f9fa; min-height: 100vh; padding-bottom: 40px; }
.top-bar { background: #fff; padding: 15px 40px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eef2f6; }
.top-bar h1 { font-size: 1.5rem; color: #344767; margin: 0; font-weight: 600; }
.user-area { display: flex; align-items: center; gap: 15px; color: #67748e; font-size: 0.9rem; }

.dashboard-content { display: grid; grid-template-columns: 350px 1fr; gap: 25px; padding: 25px 40px; align-items: start; }
@media (max-width: 1024px) { .dashboard-content { grid-template-columns: 1fr; } }

/* Cartões de Ação */
.left-col { display: flex; flex-direction: column; gap: 20px; }
.action-card { background: #fff; border-radius: 12px; border: 1px solid #eef2f6; box-shadow: 0 2px 6px rgba(0,0,0,0.02); overflow: hidden; transition: all 0.3s; }
.action-card:hover { border-color: #2563eb; box-shadow: 0 4px 12px rgba(37, 99, 235, 0.1); }

.card-header { display: flex; align-items: center; gap: 12px; padding: 20px; border-bottom: 1px solid #f0f2f5; background: #fafbfc; }
.card-header h3 { font-size: 1.1rem; font-weight: 600; margin: 0; color: #344767; }
.card-body { padding: 20px; }

.icon-bg { width: 40px; height: 40px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 1.25rem; }
.icon-bg.blue { background: #eff6ff; color: #2563eb; }
.icon-bg.green { background: #f0fdf4; color: #16a34a; }

/* Formulários */
.form-group { margin-bottom: 15px; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
label { display: block; font-size: 0.85rem; font-weight: 600; color: #344767; margin-bottom: 6px; }
input, select, textarea { width: 100%; padding: 10px; border: 1px solid #d2d6da; border-radius: 6px; outline: none; font-family: inherit; transition: border-color 0.2s; background: #fff; }
input:focus, select:focus, textarea:focus { border-color: #2563eb; box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1); }

.input-error { border-color: #ef4444 !important; background-color: #fef2f2; }
.error-text { color: #ef4444; font-size: 0.75rem; margin-top: 4px; display: block; }

/* Busca e Cliente Selecionado */
.search-box { display: flex; gap: 8px; margin-bottom: 15px; }
.search-box input { margin-bottom: 0; }
.btn-icon-search { background: #f1f5f9; border: 1px solid #cbd5e1; width: 42px; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s; }
.btn-icon-search:hover:not(:disabled) { background: #e2e8f0; border-color: #94a3b8; }

.selected-client-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 15px; border-left: 4px solid #2563eb; animation: slideDown 0.3s ease; }
.client-info-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; border-bottom: 1px solid #eef2f6; padding-bottom: 8px; }
.client-name { font-weight: 700; color: #1e293b; display: block; }
.client-cpf { font-size: 0.8rem; color: #64748b; }
.btn-close-sm { background: none; border: none; color: #94a3b8; cursor: pointer; font-size: 1.2rem; line-height: 1; padding: 0 5px; }
.btn-close-sm:hover { color: #ef4444; }

.mt-3 { margin-top: 15px; }

/* Botões */
.btn-blue { background: #2563eb; color: white; border: none; padding: 12px; border-radius: 6px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
.btn-blue:hover:not(:disabled) { background: #1d4ed8; }
.btn-success { background: #16a34a; color: white; border: none; padding: 12px; border-radius: 6px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
.btn-success:hover:not(:disabled) { background: #15803d; }
.btn-outline { background: transparent; border: 1px solid #cbd5e1; color: #64748b; padding: 6px 16px; border-radius: 6px; cursor: pointer; }
.btn-outline:hover { background: #fff; border-color: #94a3b8; }
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; }
.w-full { width: 100%; }
button:disabled { opacity: 0.7; cursor: not-allowed; }

/* Spinner Simples */
.spinner-sm { display: inline-block; width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-radius: 50%; border-top-color: #fff; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* Tabela */
.table-card { background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.03); border: 1px solid #eef2f6; overflow: hidden; height: 100%; display: flex; flex-direction: column; }
.card-header-simple { padding: 20px 25px; border-bottom: 1px solid #f0f2f5; display: flex; justify-content: space-between; align-items: center; }
.card-header-simple h3 { font-size: 1.1rem; font-weight: 600; color: #344767; margin: 0; }
.btn-refresh { background: none; border: none; font-size: 1.2rem; color: #64748b; cursor: pointer; transition: transform 0.3s; }
.btn-refresh:hover { transform: rotate(180deg); color: #2563eb; }

.clean-table { width: 100%; border-collapse: collapse; }
.clean-table th { text-align: left; padding: 15px 25px; font-size: 0.75rem; color: #8392ab; font-weight: 700; text-transform: uppercase; border-bottom: 1px solid #f0f2f5; background: #f8f9fa; }
.clean-table td { padding: 16px 25px; font-size: 0.9rem; color: #344767; border-bottom: 1px solid #f0f2f5; vertical-align: middle; }
.font-mono { font-family: monospace; color: #2563eb; font-weight: 600; }
.cell-content { display: flex; flex-direction: column; line-height: 1.3; }
.font-bold { font-weight: 600; }
.text-muted { font-size: 0.8rem; color: #94a3b8; }
.empty-msg { text-align: center; padding: 40px; color: #94a3b8; font-style: italic; }

.status-tag { padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; }
.status-tag.ABERTA { background: #dbeafe; color: #1e40af; }
.status-tag.FINALIZADA { background: #dcfce7; color: #15803d; }
.status-tag.CANCELADA { background: #f1f5f9; color: #64748b; }
.status-tag.EM_REPARO { background: #f3e8ff; color: #6b21a8; }

/* Modal */
.modal-backdrop { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(15, 23, 42, 0.6); display: flex; justify-content: center; align-items: center; z-index: 100; backdrop-filter: blur(2px); }
.modal-window { background: white; width: 100%; max-width: 550px; border-radius: 12px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); overflow: hidden; margin: 1rem; }
.modal-header { padding: 1.5rem; background: #f8fafc; border-bottom: 1px solid #eef2f6; display: flex; justify-content: space-between; align-items: center; }
.close-btn { background: none; border: none; font-size: 1.5rem; color: #64748b; cursor: pointer; }
.modal-body { padding: 2rem; }
.modal-footer { padding: 1rem 2rem; background: #f8fafc; border-top: 1px solid #eef2f6; text-align: right; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; }
.info-item.full-width { grid-column: span 2; }
.info-item label { display: block; font-size: 0.75rem; color: #94a3b8; text-transform: uppercase; font-weight: 700; margin-bottom: 4px; }
.info-item p { font-weight: 500; color: #334155; margin: 0; }
.highlight { color: #2563eb; }
.price { color: #16a34a; font-weight: 700; font-size: 1.1rem; }
.text-box { background: #f8fafc; padding: 12px; border-radius: 6px; border: 1px solid #eef2f6; font-size: 0.95rem; line-height: 1.5; }
.text-warning { color: #d97706; font-style: italic; }

.status-banner { padding: 10px; border-radius: 6px; text-align: center; font-size: 0.9rem; border: 1px solid transparent; }
.bg-ABERTA { background: #dbeafe; color: #1e40af; border-color: #bfdbfe; }
.bg-FINALIZADA { background: #dcfce7; color: #15803d; border-color: #bbf7d0; }

@keyframes slideDown { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
</style>