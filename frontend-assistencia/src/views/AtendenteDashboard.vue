<template>
  <div class="container">
    <h2>Painel do Atendente</h2>
    <div class="header-actions">
      <p>Bem-vindo, {{ usuario?.nome || 'Atendente' }}</p>
      <button @click="logout" class="btn-logout">Sair</button>
    </div>

    <div class="actions-grid">
      <div class="card form-section">
        <h3>1. Novo Cliente</h3>
        <div class="form-group">
          <input v-model="novoCliente.nome" placeholder="Nome Completo">
          <input 
            v-model="novoCliente.cpf" 
            placeholder="CPF (apenas números)" 
            @input="filtrarNumeros('novoCliente', 'cpf')"
            maxlength="11"
          >
          <input type="date" v-model="novoCliente.dataNascimento">
          <input v-model="novoCliente.endereco" placeholder="Endereço">
          <button @click="cadastrarCliente" class="btn-primary">Cadastrar Cliente</button>
        </div>
      </div>

      <div class="card form-section">
        <h3>2. Abrir Ordem de Serviço</h3>
        <div class="search-box">
          <input 
            v-model="novaOS.cpfCliente" 
            placeholder="Buscar CPF do Cliente"
            @input="filtrarNumeros('novaOS', 'cpfCliente')"
          >
          <button @click="buscarClienteParaOS" class="btn-secondary">Buscar</button>
        </div>
        
        <div v-if="clienteSelecionado" class="cliente-info">
          <p><strong>Cliente:</strong> {{ clienteSelecionado.nome }}</p>
          <label>Tipo de Hardware:</label>
          <select v-model="novaOS.tipoHardware">
            <option value="NOTEBOOK">Notebook</option>
            <option value="CELULAR">Celular</option>
            <option value="COMPUTADOR">Computador</option>
            <option value="OUTROS">Outros</option>
          </select>
          <textarea v-model="novaOS.descricao" placeholder="Descrição do defeito relatado..." rows="3"></textarea>
          <button @click="abrirOS" class="btn-success">Gerar OS</button>
        </div>
      </div>
    </div>

    <h3>Ordens de Serviço Recentes</h3>
    <div class="table-container">
      <table class="styled-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Cliente</th>
            <th>Hardware</th>
            <th>Status</th>
            <th>Ação</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="os in listaOS" :key="os.id">
            <td>#{{ os.id.substring(0,8) }}</td>
            <td>{{ os.cliente?.nome }}</td>
            <td>{{ os.hardware?.tipoHardware }}</td>
            <td><span :class="'status-' + os.estado.toLowerCase()">{{ os.estado }}</span></td>
            <td>
              <button class="btn-small" @click="verDetalhes(os)">Detalhes</button>
            </td>
          </tr>
        </tbody>
      </table>
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
              <span :class="'status-' + osSelecionada.estado.toLowerCase()">{{ osSelecionada.estado }}</span>
            </div>
            <div class="detail-group">
              <label>Orçamento:</label>
              <span>R$ {{ osSelecionada.valorOrcamento.toFixed(2) }}</span>
            </div>
          </div>
          
          <hr>
          
          <div class="detail-group">
            <label>Cliente:</label>
            <span>{{ osSelecionada.cliente?.nome }} (CPF: {{ osSelecionada.cliente?.cpf }})</span>
            <small>{{ osSelecionada.cliente?.endereco }}</small>
          </div>

          <div class="detail-group">
            <label>Equipamento:</label>
            <span>{{ osSelecionada.hardware?.tipoHardware }}</span>
          </div>

          <div class="detail-group">
            <label>Descrição do Problema:</label>
            <p class="desc-text">{{ osSelecionada.descricao }}</p>
          </div>

          <hr>

          <div class="detail-group">
            <label>Técnico Responsável:</label>
            <span v-if="osSelecionada.tecnico">{{ osSelecionada.tecnico.nome }}</span>
            <span v-else class="text-muted">Ainda não atribuído</span>
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
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const usuario = JSON.parse(localStorage.getItem('usuario'));
const listaOS = ref([]);
const novoCliente = ref({ nome: '', cpf: '', dataNascimento: '', endereco: '' });
const novaOS = ref({ cpfCliente: '', tipoHardware: 'NOTEBOOK', descricao: '' });
const clienteSelecionado = ref(null);

// Estado do Modal
const modalAberto = ref(false);
const osSelecionada = ref({});

const verDetalhes = (os) => {
  osSelecionada.value = os;
  modalAberto.value = true;
};

const fecharModal = () => {
  modalAberto.value = false;
};

const filtrarNumeros = (obj, campo) => {
  if (obj === 'novoCliente') novoCliente.value[campo] = novoCliente.value[campo].replace(/\D/g, '');
  else if (obj === 'novaOS') novaOS.value[campo] = novaOS.value[campo].replace(/\D/g, '');
};

const carregarOS = async () => {
  try {
    const res = await api.get('/os');
    listaOS.value = res.data.reverse();
  } catch (e) { console.error(e); }
};

const cadastrarCliente = async () => {
  try {
    if(novoCliente.value.cpf.length !== 11) return alert("CPF deve ter 11 dígitos");
    await api.post('/clientes', novoCliente.value);
    alert('Cliente cadastrado!');
    novoCliente.value = { nome: '', cpf: '', dataNascimento: '', endereco: '' };
  } catch (e) { alert('Erro: ' + (e.response?.data || e.message)); }
};

const buscarClienteParaOS = async () => {
  try {
    const res = await api.get(`/clientes/cpf/${novaOS.value.cpfCliente}`);
    clienteSelecionado.value = res.data;
  } catch (e) { alert('Cliente não encontrado.'); }
};

const abrirOS = async () => {
  if(!clienteSelecionado.value) return;
  try {
    const resHard = await api.post('/hardware', { tipoHardware: novaOS.value.tipoHardware, clienteId: clienteSelecionado.value.id });
    await api.post('/os', {
      valorOrcamento: 0.0,
      descricao: novaOS.value.descricao,
      clienteId: clienteSelecionado.value.id,
      hardwareId: resHard.data.numeroSerie
    });
    alert('OS Aberta!');
    carregarOS();
    clienteSelecionado.value = null;
    novaOS.value.descricao = '';
  } catch (e) { alert('Erro ao abrir OS'); }
};

const logout = () => { localStorage.clear(); router.push('/'); };
onMounted(carregarOS);
</script>

<style scoped>
/* Estilos anteriores mantidos */
.container { max-width: 1000px; margin: 0 auto; padding: 20px; }
.header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.actions-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 30px; }
.card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); border: 1px solid #eee; }
.form-group { display: flex; flex-direction: column; gap: 10px; }
input, select, textarea { padding: 10px; border: 1px solid #ddd; border-radius: 4px; width: 100%; }
.search-box { display: flex; gap: 10px; margin-bottom: 15px; }
.styled-table { width: 100%; border-collapse: collapse; margin-top: 10px; background: white; border-radius: 8px; overflow: hidden; }
.styled-table th, .styled-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eee; }
.styled-table th { background-color: #2c3e50; color: white; }
.status-aberta { color: #2980b9; font-weight: bold; }
.status-finalizada { color: #27ae60; font-weight: bold; }
.btn-primary { background-color: #3498db; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; }
.btn-success { background-color: #27ae60; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; }
.btn-secondary { background-color: #95a5a6; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; }
.btn-logout { background-color: #e74c3c; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
.btn-small { background-color: #f1c40f; color: #333; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-weight: bold; font-size: 0.9em; }

/* Estilos do Modal */
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
.text-muted { color: #999; font-style: italic; }
</style>