<template>
  <div class="container">
    <div class="header">
        <h2>Painel Administrador</h2>
        <button @click="logout">Sair</button>
    </div>

    <div class="tabs">
        <button @click="abaAtual = 'os'">Gerenciar OS</button>
        <button @click="abaAtual = 'usuarios'">Cadastrar Equipe</button>
        <button @click="abaAtual = 'relatorios'">Relatórios</button>
    </div>

    <div v-if="abaAtual === 'os'">
        <h3>Todas as OS</h3>
        <table border="1">
            <thead><tr><th>ID</th><th>Cliente</th><th>Status</th><th>Ações</th></tr></thead>
            <tbody>
                <tr v-for="os in listaOS" :key="os.id">
                    <td>{{ os.id.substring(0,8) }}...</td>
                    <td>{{ os.cliente?.nome }}</td>
                    <td>{{ os.estado }}</td>
                    <td>
                        <button @click="excluirOS(os.id)">Excluir</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <div v-if="abaAtual === 'usuarios'">
        <h3>Cadastrar Funcionário</h3>
        <select v-model="novoFuncionario.tipo">
            <option value="tecnicos">Técnico</option>
            <option value="atendente">Atendente</option>
            <option value="administrador">Administrador</option>
        </select>
        <input v-model="novoFuncionario.nome" placeholder="Nome">
        <input v-model="novoFuncionario.cpf" placeholder="CPF">
        <input type="date" v-model="novoFuncionario.dataNascimento">
        <button @click="cadastrarFuncionario">Salvar</button>
    </div>

    <div v-if="abaAtual === 'relatorios'">
        <h3>Relatórios do Sistema</h3>
        <p>Total de OS: {{ listaOS.length }}</p>
        <p>OS Finalizadas: {{ listaOS.filter(o => o.estado === 'FINALIZADA').length }}</p>
        <p>OS Canceladas: {{ listaOS.filter(o => o.estado === 'CANCELADA').length }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const abaAtual = ref('os');
const listaOS = ref([]);
const novoFuncionario = ref({ tipo: 'tecnicos', nome: '', cpf: '', dataNascimento: '', endereco: 'Empresa' });

const carregarDados = async () => {
    const res = await api.get('/os');
    listaOS.value = res.data;
};

const excluirOS = async (id) => {
    if(confirm("Tem certeza? Isso apagará a OS.")) {
        try {
            await api.delete(`/os/${id}`);
            carregarDados();
        } catch (e) { alert("Não é possível excluir OS em andamento (Regra Backend)"); }
    }
};

const cadastrarFuncionario = async () => {
    try {
        // O endpoint muda dinamicamente: /api/tecnicos, /api/atendente, etc.
        await api.post(`/${novoFuncionario.value.tipo}`, novoFuncionario.value);
        alert('Funcionário cadastrado com sucesso!');
        novoFuncionario.value.nome = '';
        novoFuncionario.value.cpf = '';
    } catch (e) { alert('Erro ao cadastrar'); }
};

const logout = () => { localStorage.clear(); router.push('/'); };
onMounted(carregarDados);
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; }
.tabs { margin: 20px 0; display: flex; gap: 10px; }
.tabs button { padding: 10px; cursor: pointer; }
table { width: 100%; border-collapse: collapse; }
th, td { padding: 8px; text-align: left; }
</style>