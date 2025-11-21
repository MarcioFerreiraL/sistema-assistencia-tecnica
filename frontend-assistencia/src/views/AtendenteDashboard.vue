<template>
  <div class="container">
    <h2>Painel do Atendente</h2>
    <button @click="logout">Sair</button>

    <div class="actions">
        <div class="form-section">
            <h3>1. Cadastrar Cliente</h3>
            <input v-model="novoCliente.nome" placeholder="Nome">
            <input v-model="novoCliente.cpf" placeholder="CPF">
            <input type="date" v-model="novoCliente.dataNascimento">
            <input v-model="novoCliente.endereco" placeholder="Endereço">
            <button @click="cadastrarCliente">Cadastrar Cliente</button>
        </div>

        <div class="form-section">
            <h3>2. Abrir Ordem de Serviço</h3>
            <input v-model="novaOS.cpfCliente" placeholder="CPF do Cliente">
            <button @click="buscarClienteParaOS">Buscar Cliente</button>
            
            <div v-if="clienteSelecionado">
                <p>Cliente: {{ clienteSelecionado.nome }}</p>
                
                <label>Hardware:</label>
                <select v-model="novaOS.tipoHardware">
                    <option value="NOTEBOOK">Notebook</option>
                    <option value="CELULAR">Celular</option>
                    <option value="COMPUTADOR">Computador</option>
                    <option value="OUTROS">Outros</option>
                </select>

                <textarea v-model="novaOS.descricao" placeholder="Descrição do problema"></textarea>
                <button @click="abrirOS">Gerar OS</button>
            </div>
        </div>
    </div>

    <h3>Todas as Ordens de Serviço</h3>
    <ul>
        <li v-for="os in listaOS" :key="os.id">
            #{{ os.id.substring(0,8) }} - {{ os.cliente.nome }} - {{ os.estado }}
        </li>
    </ul>
</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const listaOS = ref([]);
const novoCliente = ref({ nome: '', cpf: '', dataNascimento: '', endereco: '' });
const novaOS = ref({ cpfCliente: '', tipoHardware: 'NOTEBOOK', descricao: '' });
const clienteSelecionado = ref(null);

const carregarOS = async () => {
    const res = await api.get('/os');
    listaOS.value = res.data;
};

const cadastrarCliente = async () => {
    try {
        await api.post('/clientes', novoCliente.value);
        alert('Cliente cadastrado!');
        novoCliente.value = { nome: '', cpf: '', dataNascimento: '', endereco: '' };
    } catch (e) { alert('Erro ao cadastrar: ' + e.message); }
};

const buscarClienteParaOS = async () => {
    try {
        const res = await api.get(`/clientes/cpf/${novaOS.value.cpfCliente}`);
        clienteSelecionado.value = res.data;
    } catch (e) { alert('Cliente não encontrado'); }
};

const abrirOS = async () => {
    if(!clienteSelecionado.value) return;

    try {
        // 1. Cadastrar Hardware (Fluxo simplificado: cria hardware na hora)
        const hardwareDto = {
            tipoHardware: novaOS.value.tipoHardware,
            clienteId: clienteSelecionado.value.id
        };
        const resHard = await api.post('/hardware', hardwareDto);
        const hardwareId = resHard.data.numeroSerie;

        // 2. Criar OS
        const osDto = {
            valorOrcamento: 0.0,
            descricao: novaOS.value.descricao,
            clienteId: clienteSelecionado.value.id,
            hardwareId: hardwareId,
            tecnicoId: null // Sem técnico inicialmente
        };
        
        await api.post('/os', osDto);
        alert('OS Aberta com Sucesso!');
        carregarOS();
        clienteSelecionado.value = null;
        novaOS.value.descricao = '';
    } catch (e) {
        alert('Erro ao abrir OS');
        console.error(e);
    }
};

const logout = () => { localStorage.clear(); router.push('/'); };
onMounted(carregarOS);
</script>

<style scoped>
.actions { display: flex; gap: 20px; margin-bottom: 20px; }
.form-section { border: 1px solid #ccc; padding: 15px; border-radius: 5px; flex: 1; display: flex; flex-direction: column; gap: 10px; }
</style>