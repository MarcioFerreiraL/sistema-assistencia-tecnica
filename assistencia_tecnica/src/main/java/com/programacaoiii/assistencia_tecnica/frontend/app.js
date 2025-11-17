// =============================================
// API (O "Telefone" para o Backend)
// =============================================
const API_URL = 'http://localhost:8080/api';

const Api = {
    // --- Funções de Busca (GET) ---
    getClientes: () => fetch(`${API_URL}/clientes`),
    getClientePorCpf: (cpf) => fetch(`${API_URL}/clientes/cpf/${cpf}`),
    
    getTecnicos: () => fetch(`${API_URL}/tecnicos`),
    getTecnicoPorCpf: (cpf) => fetch(`${API_URL}/tecnicos/cpf/${cpf}`),
    
    getAtendentes: () => fetch(`${API_URL}/atendentes`),
    getAtendentePorCpf: (cpf) => fetch(`${API_URL}/atendentes/cpf/${cpf}`),

    getAdmins: () => fetch(`${API_URL}/administradores`),
    getAdminPorCpf: (cpf) => fetch(`${API_URL}/administradores/cpf/${cpf}`),
    
    getOsPorCliente: (id) => fetch(`${API_URL}/os/cliente/${id}`),
    getOsPorTecnico: (id) => fetch(`${API_URL}/os/tecnico/${id}`),
    getOsAbertas: () => fetch(`${API_URL}/os/estado/ABERTA`),
    
    // --- Funções de Criação (POST) ---
    salvar: (endpoint, dto) => fetch(`${API_URL}/${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dto)
    }),
    
    // --- Funções de Ação da OS (POST) ---
    osMudarEstado: (id, acao) => fetch(`${API_URL}/os/${id}/${acao}`, {
        method: 'POST'
    }),
};

// =============================================
// AUTH (O "Segurança" do Frontend)
// =============================================
const Auth = {
    
    // Nosso novo login simulado por CPF
    login: async (cpf, tipo) => {
        let response;
        try {
            // Chama o endpoint específico
            if (tipo === 'cliente') response = await Api.getClientePorCpf(cpf);
            else if (tipo === 'tecnico') response = await Api.getTecnicoPorCpf(cpf);
            else if (tipo === 'atendente') response = await Api.getAtendentePorCpf(cpf);
            else if (tipo === 'admin') response = await Api.getAdminPorCpf(cpf);
            else return false;

            if (!response.ok) {
                // Erro 404 (Não encontrado)
                return false;
            }

            const usuario = await response.json();
            
            // Login bem-sucedido!
            localStorage.setItem('usuario_logado', JSON.stringify(usuario));
            localStorage.setItem('usuario_tipo', tipo);
            return true;
            
        } catch (error) {
            console.error("Erro no login:", error);
            return false;
        }
    },

    logout: () => {
        localStorage.removeItem('usuario_logado');
        localStorage.removeItem('usuario_tipo');
        window.location.href = '../login.html'; // Volta para o login
    },

    // Verifica se o usuário está logado E se é do tipo correto
    checkAuth: (tipoNecessario) => {
        const usuario = Auth.getUsuario();
        const tipo = Auth.getTipo();

        if (!usuario || tipo !== tipoNecessario) {
            alert('Acesso negado. Faça o login.');
            window.location.href = '../login.html';
            throw new Error("Acesso Negado"); 
        }
        return usuario;
    },

    getUsuario: () => JSON.parse(localStorage.getItem('usuario_logado')),
    getTipo: () => localStorage.getItem('usuario_tipo')
};

// =============================================
// Utilitários (Funções de ajuda)
// =============================================
const Utils = {
    // Formata data do Java (ex: 2025-11-17T...) para "17/11/2025"
    formatarData: (dataISO) => {
        if (!dataISO) return 'N/A';
        return new Date(dataISO).toLocaleDateString('pt-BR');
    },

    // Preenche um <select> (dropdown) com dados da API
    preencherSelect: async (idSelect, fnBuscaApi, nomeCampo, idCampo) => {
        const select = document.getElementById(idSelect);
        select.innerHTML = '<option>A carregar...</option>';
        try {
            const response = await fnBuscaApi();
            const dados = await response.json();
            
            select.innerHTML = '<option value="">Selecione...</option>';
            dados.forEach(item => {
                select.add(new Option(item[nomeCampo], item[idCampo]));
            });
        } catch (e) {
            select.innerHTML = '<option>Erro ao carregar</option>';
        }
    },
    
    // Mostra um alerta de sucesso ou erro
    mostrarAlerta: (idAlerta, mensagem, tipo = 'success') => {
        const alerta = document.getElementById(idAlerta);
        alerta.className = `alert alert-${tipo}`; 
        alerta.textContent = mensagem;
        alerta.classList.remove('d-none');
        
        setTimeout(() => alerta.classList.add('d-none'), 5000);
    }
}