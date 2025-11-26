-- 1. Limpeza (Opcional - Remove dados antigos e reseta os IDs)
TRUNCATE TABLE 
    tb_os_pecas,
    tb_ordem_servico,
    tb_hardware,
    tb_peca,
    tb_cliente,
    tb_tecnico,
    tb_atendente,
    tb_administrador
RESTART IDENTITY CASCADE;

-- 2. Inserir Administrador
INSERT INTO tb_administrador (nome, cpf, data_nascimento, endereco) VALUES 
('Carlos Admin', '11111111111', '1985-05-10', 'Av. Paulista, 1000 - São Paulo, SP');

-- 3. Inserir Atendentes
INSERT INTO tb_atendente (nome, cpf, data_nascimento, endereco) VALUES 
('Ana Clara Souza', '22222222222', '1995-03-15', 'Rua das Flores, 123 - Curitiba, PR'),
('Beatriz Lima', '33333333333', '1998-07-20', 'Rua XV de Novembro, 50 - Curitiba, PR');

-- 4. Inserir Técnicos
INSERT INTO tb_tecnico (nome, cpf, data_nascimento, endereco) VALUES 
('Roberto Técnico', '44444444444', '1990-01-10', 'Rua da Manutenção, 88 - Curitiba, PR'),
('Julia Reparos', '55555555555', '1992-12-05', 'Av. dos Estados, 900 - Curitiba, PR');

-- 5. Inserir Clientes
INSERT INTO tb_cliente (nome, cpf, data_nascimento, endereco) VALUES 
('Marcos Silva', '66666666666', '1988-09-25', 'Rua A, 10 - Bairro Novo'),
('Fernanda Oliveira', '77777777777', '2000-02-14', 'Rua B, 20 - Centro'),
('João Pedro Santos', '88888888888', '1975-11-30', 'Rua C, 30 - Jardim Botânico'),
('Camila Pereira', '99999999999', '1993-06-18', 'Rua D, 40 - Batel');

-- 6. Inserir Peças (Estoque)
-- Tipos: PROCESSADOR, MEMORIA_RAM, PLACA_MAE, TELA, BATERIA
INSERT INTO tb_peca (nome, tipo_peca) VALUES 
('Tela LED 15.6 Slim', 'TELA'),
('Bateria Samsung Original', 'BATERIA'),
('SSD Kingston 480GB', 'MEMORIA_RAM'), -- Simulando armazenamento/memória
('Memória RAM 8GB DDR4', 'MEMORIA_RAM'),
('Placa Mãe Asus H310', 'PLACA_MAE'),
('Processador Intel i5', 'PROCESSADOR');

-- 7. Inserir Hardware (Vinculado aos Clientes)
-- Tipos: NOTEBOOK, CELULAR, COMPUTADOR, OUTROS
-- IDs dos clientes assumidos pela ordem de inserção (1, 2, 3, 4)
INSERT INTO tb_hardware (tipo_hardware, cliente_id) VALUES 
('NOTEBOOK', 1),   -- Hardware 1 (Marcos)
('CELULAR', 2),    -- Hardware 2 (Fernanda)
('COMPUTADOR', 3), -- Hardware 3 (João)
('OUTROS', 4);     -- Hardware 4 (Camila - ex: Console)

-- 8. Inserir Ordens de Serviço (OS)
-- Estados: ABERTA, AGUARDANDO_ORCAMENTO, AGUARDANDO_APROVACAO, EM_REPARO, FINALIZADA, CANCELADA
-- UUIDs gerados estaticamente para compatibilidade

-- OS 1: Aberta (Cliente Marcos, Hardware 1)
INSERT INTO tb_ordem_servico (id, descricao, valor_orcamento, estado, data_criacao, data_atualizacao, cliente_id, hardware_id, tecnico_id, observacoes_tecnicas) VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'Notebook não liga, possível problema na fonte.', 0.0, 'ABERTA', NOW(), NOW(), 1, 1, NULL, NULL);

-- OS 2: Em Reparo (Cliente Fernanda, Hardware 2, Técnico Roberto)
INSERT INTO tb_ordem_servico (id, descricao, valor_orcamento, estado, data_criacao, data_atualizacao, cliente_id, hardware_id, tecnico_id, observacoes_tecnicas) VALUES 
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'Troca de tela quebrada.', 350.00, 'EM_REPARO', NOW(), NOW(), 2, 2, 1, 'Tela removida, aguardando cola secar.');

-- OS 3: Finalizada (Cliente João, Hardware 3, Técnica Julia)
INSERT INTO tb_ordem_servico (id, descricao, valor_orcamento, estado, data_criacao, data_atualizacao, cliente_id, hardware_id, tecnico_id, observacoes_tecnicas) VALUES 
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Limpeza e formatação.', 150.00, 'FINALIZADA', NOW(), NOW(), 3, 3, 2, 'Serviço concluído com sucesso. Backup realizado.');

-- OS 4: Aguardando Aprovação (Cliente Camila, Hardware 4, Técnico Roberto)
INSERT INTO tb_ordem_servico (id, descricao, valor_orcamento, estado, data_criacao, data_atualizacao, cliente_id, hardware_id, tecnico_id, observacoes_tecnicas) VALUES 
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a04', 'Console esquentando muito.', 200.00, 'AGUARDANDO_APROVACAO', NOW(), NOW(), 4, 4, 1, 'Necessário troca de pasta térmica e limpeza interna.');

-- 9. Vincular Peças às OS (Tabela de Join tb_os_pecas)
-- OS 2 (iPhone) usou a Peça 1 (Tela)
INSERT INTO tb_os_pecas (ordem_servico_id, peca_id) VALUES 
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 1);

-- OS 3 (PC Gamer) usou a Peça 6 (Processador - hipotético, só para exemplo)
INSERT INTO tb_os_pecas (ordem_servico_id, peca_id) VALUES 
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 6);