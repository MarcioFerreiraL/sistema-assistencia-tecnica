<template>
  <div class="container">
    <div class="header">
      <h2>Painel Administrador</h2>
      <div class="user-info">
        <span>Olá, {{ usuario?.nome }}</span>
        <button @click="logout" class="btn-logout">Sair</button>
      </div>
    </div>

    <div class="tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.key"
        :class="{ active: abaAtual === tab.key }"
        @click="mudarAba(tab.key)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-if="modo === 'lista'" class="content-area">
      <div class="list-header">
        <h3>Gerenciar {{ tabs.find(t => t.key === abaAtual).label }}</h3>
      </div>

      <table class="styled-table">
        <thead>
          <tr>
            <th v-for="col in colunasAtuais" :key="col.key">{{ col.label }}</th>
            <th style="width: 200px;">Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in listaDados" :key="item.id || item.numeroSerie">
            <td v-for="col in colunasAtuais" :key="col.key">
              {{ resolverValor(item, col.key) }}
            </td>
            <td class="actions-cell">
              <button v-if="abaAtual === 'os'" class="btn-details" @click="verDetalhes(item)">Detalhes</button>
              
              <button class="btn-edit" @click="prepararEdicao(item)">Editar</button>
              <button class="btn-delete" @click="excluirItem(item)">Excluir</button>
            </td>
          </tr>
          <tr v-if="listaDados.length === 0">
            <td :colspan="colunasAtuais.length + 1" class="text-center">Nenhum registro encontrado.</td>
          </tr>
        </tbody>
      </table>

      <div class="footer-actions">
        <button class="btn-create" @click="prepararCadastro">
          + Cadastrar Novo {{ tabs.find(t => t.key === abaAtual).singular }}
        </button>
      </div>
    </div>

    <div v-else class="form-area">
      <h3>{{ itemEmEdicao.id || itemEmEdicao.numeroSerie ? 'Editar' : 'Cadastrar' }} {{ tabs.find(t => t.key === abaAtual).singular }}</h3>
      <div class="form-grid">
        <div v-for="campo in camposFormulario" :key="campo.key" class="form-group">
          <label>{{ campo.label }}</label>
          <select v-if="campo.type === 'select'" v-model="itemEmEdicao[campo.key]">
            <option v-for="opt in campo.options" :key="opt" :value="opt">{{ opt }}</option>
          </select>
          <input v-else :type="campo.type || 'text'" v-model="itemEmEdicao[campo.key]" :placeholder="campo.label">
        </div>
      </div>
      <div class="form-actions">
        <button class="btn-cancel" @click="cancelarEdicao">Cancelar</button>
        <button class="btn-save" @click="salvarItem">Salvar</button>
      </div>
    </div>

    <div v-if="modalAberto" class="modal-overlay" @click.self="fecharModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Detalhes da OS #{{ osSelecionada.id.substring(0,8) }}</h3>
          <button @click="fecharModal" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <div class="detail-group">
            <label>ID Completo:</label>
            <span>{{ osSelecionada.id }}</span>
          </div>
          <div class="detail-row">
            <div class="detail-group">
              <label>Status:</label>
              <span class="status-badge">{{ osSelecionada.estado }}</span>
            </div>
            <div class="detail-group">
              <label>Orçamento:</label>
              <span>R$ {{ osSelecionada.valorOrcamento?.toFixed(2) }}</span>
            </div>
          </div>
          <hr>
          <div class="detail-group">
            <label>Cliente:</label>
            <span>{{ osSelecionada.cliente?.nome }} (CPF: {{ osSelecionada.cliente?.cpf }})</span>
          </div>
          <div class="detail-group">
            <label>Equipamento:</label>
            <span>{{ osSelecionada.hardware?.tipoHardware }} (ID: {{ osSelecionada.hardware?.numeroSerie }})</span>
          </div>
          <div class="detail-group">
            <label>Descrição:</label>
            <p class="desc-text">{{ osSelecionada.descricao }}</p>
          </div>
          <hr>
          <div class="detail-group">
            <label>Técnico:</label>
            <span v-if="osSelecionada.tecnico">{{ osSelecionada.tecnico.nome }}</span>
            <span v-else class="text-muted">Não atribuído</span>
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
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const usuario = JSON.parse(localStorage.getItem('usuario'));
const abaAtual = ref('clientes');
const modo = ref('lista');
const listaDados = ref([]);
const itemEmEdicao = ref({});

// Modal Logic
const modalAberto = ref(false);
const osSelecionada = ref({});

const verDetalhes = (os) => {
  osSelecionada.value = os;
  modalAberto.value = true;
};
const fecharModal = () => {
  modalAberto.value = false;
};

// Configurações (Abas, Colunas, Campos)
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
  os: [{ key: 'id', label: 'ID (UUID)' }, { key: 'descricao', label: 'Descrição' }, { key: 'estado', label: 'Status' }, { key: 'valorOrcamento', label: 'Valor' }],
  pecas: [{ key: 'numeroSerie', label: 'ID' }, { key: 'nome', label: 'Nome' }, { key: 'tipoPeca', label: 'Tipo' }],
  hardwares: [{ key: 'numeroSerie', label: 'Serial' }, { key: 'tipoHardware', label: 'Tipo' }, { key: 'cliente.nome', label: 'Dono' }]
};

const camposPorAba = {
  clientes: [
    { key: 'nome', label: 'Nome Completo' },
    { key: 'cpf', label: 'CPF (Apenas números)' },
    { key: 'dataNascimento', label: 'Data Nascimento', type: 'date' },
    { key: 'endereco', label: 'Endereço' }
  ],
  atendentes: [
    { key: 'nome', label: 'Nome Completo' },
    { key: 'cpf', label: 'CPF' },
    { key: 'dataNascimento', label: 'Data Nascimento', type: 'date' },
    { key: 'endereco', label: 'Endereço' }
  ],
  tecnicos: [
    { key: 'nome', label: 'Nome Completo' },
    { key: 'cpf', label: 'CPF' },
    { key: 'dataNascimento', label: 'Data Nascimento', type: 'date' },
    { key: 'endereco', label: 'Endereço' }
  ],
  pecas: [
    { key: 'nome', label: 'Nome da Peça' },
    { key: 'tipoPeca', label: 'Tipo', type: 'select', options: ['PROCESSADOR', 'MEMORIA_RAM', 'PLACA_MAE', 'TELA', 'BATERIA'] }
  ],
  hardwares: [
    { key: 'tipoHardware', label: 'Tipo', type: 'select', options: ['NOTEBOOK', 'COMPUTADOR', 'CELULAR', 'OUTROS'] },
    { key: 'clienteId', label: 'ID do Cliente (Dono)', type: 'number' }
  ],
  os: [
    { key: 'descricao', label: 'Descrição do Problema' },
    { key: 'valorOrcamento', label: 'Valor Inicial', type: 'number' },
    { key: 'clienteId', label: 'ID do Cliente', type: 'number' },
    { key: 'hardwareId', label: 'ID do Hardware', type: 'number' },
    { key: 'tecnicoId', label: 'ID do Técnico (Opcional)', type: 'number' }
  ]
};

const colunasAtuais = computed(() => colunasPorAba[abaAtual.value]);
const camposFormulario = computed(() => camposPorAba[abaAtual.value]);

const resolverValor = (item, key) => key.split('.').reduce((obj, k) => (obj || {})[k], item);

const carregarDados = async () => {
  const config = tabs.find(t => t.key === abaAtual.value);
  try {
    const res = await api.get(config.endpoint);
    listaDados.value = res.data;
  } catch (e) { console.error(e); listaDados.value = []; }
};

const mudarAba = (novaAba) => {
  abaAtual.value = novaAba;
  modo.value = 'lista';
  carregarDados();
};

const prepararCadastro = () => { itemEmEdicao.value = {}; modo.value = 'formulario'; };
const prepararEdicao = (item) => { 
  itemEmEdicao.value = JSON.parse(JSON.stringify(item)); 
  if (abaAtual.value === 'hardwares' && item.cliente) itemEmEdicao.value.clienteId = item.cliente.id;
  modo.value = 'formulario'; 
};
const cancelarEdicao = () => { itemEmEdicao.value = {}; modo.value = 'lista'; };

const salvarItem = async () => {
  const config = tabs.find(t => t.key === abaAtual.value);
  const id = itemEmEdicao.value.id || itemEmEdicao.value.numeroSerie;
  try {
    if (id) await api.put(`${config.endpoint}/${id}`, itemEmEdicao.value);
    else await api.post(config.endpoint, itemEmEdicao.value);
    alert('Salvo com sucesso!');
    cancelarEdicao();
    carregarDados();
  } catch (e) { alert('Erro: ' + (e.response?.data || e.message)); }
};

const excluirItem = async (item) => {
  if(!confirm('Excluir este item?')) return;
  const config = tabs.find(t => t.key === abaAtual.value);
  try {
    await api.delete(`${config.endpoint}/${item.id || item.numeroSerie}`);
    carregarDados();
  } catch (e) { alert('Erro ao excluir.'); }
};

const logout = () => { localStorage.clear(); router.push('/'); };
onMounted(carregarDados);
</script>

<style scoped>
/* Estilos do Admin */
.container { max-width: 1100px; margin: 0 auto; padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #eee; padding-bottom: 10px; }
.tabs { display: flex; gap: 5px; margin-bottom: 20px; flex-wrap: wrap; }
.tabs button { padding: 10px 20px; background: #f1f1f1; border: none; cursor: pointer; border-radius: 4px 4px 0 0; font-weight: 500; }
.tabs button.active { background: #3498db; color: white; }
.styled-table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.styled-table th, .styled-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eee; }
.styled-table th { background-color: #2c3e50; color: white; }
.actions-cell { display: flex; gap: 5px; }
.btn-logout { background: #e74c3c; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
.btn-create { margin-top: 15px; background: #27ae60; color: white; border: none; padding: 12px; width: 100%; border-radius: 4px; cursor: pointer; }
.btn-edit { background: #f39c12; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; }
.btn-delete { background: #c0392b; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; }
.btn-details { background: #3498db; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; }
.btn-save { background: #27ae60; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
.btn-cancel { background: #7f8c8d; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; margin-right: 10px; }
.form-area { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px; }
.form-group { display: flex; flex-direction: column; gap: 5px; }
input, select { padding: 10px; border: 1px solid #ddd; border-radius: 4px; }

/* Modal Styles (Copied for consistency) */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; width: 500px; max-width: 90%; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.2); overflow: hidden; }
.modal-header { background: #2c3e50; color: white; padding: 15px; display: flex; justify-content: space-between; align-items: center; }
.btn-close { background: none; border: none; color: white; font-size: 1.5rem; cursor: pointer; }
.modal-body { padding: 20px; }
.modal-footer { padding: 15px; background: #f8f9fa; text-align: right; border-top: 1px solid #eee; }
.detail-group { margin-bottom: 15px; display: flex; flex-direction: column; }
.detail-group label { font-weight: bold; color: #7f8c8d; font-size: 0.9em; }
.detail-row { display: flex; gap: 20px; }
.desc-text { background: #f9f9f9; padding: 10px; border-radius: 4px; border: 1px solid #eee; margin-top: 5px; }
.status-badge { font-weight: bold; color: #2980b9; }
.text-muted { color: #999; font-style: italic; }
</style>