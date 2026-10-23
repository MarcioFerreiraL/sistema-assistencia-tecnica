/**
 * Dados de exemplo do Ponto de Solda (fictício). Datas são geradas em relação a "agora",
 * para a bancada nunca parecer parada. Os CPFs têm dígitos verificadores errados de propósito:
 * nenhum deles pode ser de uma pessoa de verdade.
 */

export const DEMO_USERS = {
  cliente: { perfil: "clientes", cpf: "52147836900", nome: "Rita Araújo", rotulo: "Cliente" },
  atendente: { perfil: "atendentes", cpf: "30418529600", nome: "Joana Freitas", rotulo: "Atendente" },
  tecnico: { perfil: "tecnicos", cpf: "41736925800", nome: "Severino Lopes", rotulo: "Técnico" },
  admin: { perfil: "administradores", cpf: "60293817400", nome: "Cícero Barbosa", rotulo: "Dono da loja" },
};

const pessoa = (id, nome, cpf, dataNascimento, endereco) => ({ id, nome, cpf, dataNascimento, endereco });

/** UUID v4 estável a partir de um número, para os códigos de OS variarem como os reais. */
function uuidDe(n) {
  const h = (x) => (Math.imul(x, 2654435761) >>> 0).toString(16).padStart(8, "0");
  return `${h(n)}-${h(n + 97).slice(0, 4)}-4${h(n + 31).slice(1, 4)}-a${h(n + 7).slice(1, 4)}-${h(n + 13)}${h(n + 17).slice(0, 4)}`;
}

function horasAtras(agora, h) {
  return new Date(agora.getTime() - h * 3600 * 1000).toISOString();
}

export function createSeed(agora) {
  const clientes = [
    pessoa(1, "Rita Araújo", DEMO_USERS.cliente.cpf, "1988-03-14", "Rua Siqueira Campos, 210, Centro"),
    pessoa(2, "Marcos Vinícius Silva", "13579246800", "1995-07-02", "Rua do Rosário, 45, Coqueiral"),
    pessoa(3, "Fernanda Oliveira", "24681357900", "1979-11-23", "Av. Agamenon Magalhães, 1020, São José"),
    pessoa(4, "José Wellington Cabral", "11223344500", "1964-01-30", "Sítio Cachoeira, zona rural"),
    pessoa(5, "Luana Bezerra", "55667788900", "2001-09-17", "Rua Padre Nestor, 88, Cabaceiras"),
    pessoa(6, "Antônio Carlos Melo", "99887766500", "1971-05-08", "Rua Dr. Agripino, 12, Centro"),
    pessoa(7, "Camila Pereira", "44332211000", "1998-12-01", "Conj. Alto do Cruzeiro, 7"),
    pessoa(8, "Edvaldo Santos", "77665544300", "1983-04-19", "Rua São Sebastião, 301, Mulungu"),
  ];
  const tecnicos = [
    pessoa(1, "Severino Lopes", DEMO_USERS.tecnico.cpf, "1985-06-11", "Rua Coronel Baltazar, 77"),
    pessoa(2, "Paulo Henrique Costa", "36925814700", "1993-02-27", "Rua Joaquim Nabuco, 150"),
  ];
  const atendentes = [pessoa(1, "Joana Freitas", DEMO_USERS.atendente.cpf, "1997-08-05", "Rua da Matriz, 33")];
  const administradores = [pessoa(1, "Cícero Barbosa", DEMO_USERS.admin.cpf, "1975-10-09", "Rua Siqueira Campos, 500")];

  const pecas = [
    { numeroSerie: 1, nome: "Tela 6,5\" com touch", tipoPeca: "CELULAR" },
    { numeroSerie: 2, nome: "Bateria 4000 mAh", tipoPeca: "CELULAR" },
    { numeroSerie: 3, nome: "Conector de carga USB-C", tipoPeca: "CELULAR" },
    { numeroSerie: 4, nome: "SSD SATA 480 GB", tipoPeca: "NOTEBOOK" },
    { numeroSerie: 5, nome: "Memória DDR4 8 GB", tipoPeca: "NOTEBOOK" },
    { numeroSerie: 6, nome: "Fonte ATX 500 W", tipoPeca: "COMPUTADOR" },
    { numeroSerie: 7, nome: "Pasta térmica 4 g", tipoPeca: "COMPUTADOR" },
    { numeroSerie: 8, nome: "Controle remoto universal", tipoPeca: "OUTROS" },
  ];

  const hardwares = [];
  const ordens = [];
  let hw = 0;
  let n = 0;

  /** Monta uma OS como a entidade JPA serializa: cliente, hardware e técnico aninhados. */
  function os({ cliente, tipo, descricao, estado, tecnico = null, valor = 0, laudo = null, horas, atualizada, pecasIds = [] }) {
    hw += 1;
    n += 1;
    hardwares.push({ numeroSerie: hw, tipoHardware: tipo, clienteId: cliente });
    ordens.push({
      id: uuidDe(n),
      valorOrcamento: valor,
      descricao,
      observacoesTecnicas: laudo,
      dataCriacao: horasAtras(agora, horas),
      dataAtualizacao: horasAtras(agora, atualizada ?? Math.max(0, horas - 2)),
      estado,
      clienteId: cliente,
      hardwareId: hw,
      tecnicoId: tecnico,
      pecasIds,
    });
  }

  // Rita, a cliente da demo: um orçamento esperando a aprovação dela e um conserto antigo.
  os({ cliente: 1, tipo: "CELULAR", descricao: "Tela trincada no canto, touch falhando embaixo.", estado: "AGUARDANDO_APROVACAO", tecnico: 1, valor: 280, laudo: "Display com trinca e falha no digitalizador. Troca da tela completa.", horas: 30, atualizada: 3 });
  os({ cliente: 1, tipo: "NOTEBOOK", descricao: "Muito lento para ligar.", estado: "FINALIZADA", tecnico: 2, valor: 320, laudo: "HD com setores defeituosos. Troca por SSD e clonagem do sistema.", horas: 24 * 40, atualizada: 24 * 37, pecasIds: [4] });

  // Fila do balcão, ainda sem técnico.
  os({ cliente: 2, tipo: "NOTEBOOK", descricao: "Não liga, possível problema na fonte.", estado: "ABERTA", horas: 2 });
  os({ cliente: 5, tipo: "CELULAR", descricao: "Não carrega, só funciona com o cabo em certa posição.", estado: "ABERTA", horas: 5 });
  os({ cliente: 8, tipo: "OUTROS", descricao: "Caixa de som Bluetooth sem áudio de um lado.", estado: "ABERTA", horas: 20 });

  // Com o Severino.
  os({ cliente: 3, tipo: "CELULAR", descricao: "Troca de tela quebrada.", estado: "EM_REPARO", tecnico: 1, valor: 250, laudo: "Tela removida, aguardando a cola secar.", horas: 52, atualizada: 4, pecasIds: [1] });
  os({ cliente: 6, tipo: "COMPUTADOR", descricao: "Desliga sozinho depois de meia hora.", estado: "AGUARDANDO_ORCAMENTO", tecnico: 1, horas: 26, atualizada: 6 });
  os({ cliente: 7, tipo: "OUTROS", descricao: "Console esquentando muito.", estado: "AGUARDANDO_APROVACAO", tecnico: 1, valor: 150, laudo: "Pasta térmica ressecada e cooler com poeira.", horas: 70, atualizada: 22 });

  // Com o Paulo.
  os({ cliente: 4, tipo: "COMPUTADOR", descricao: "Não dá vídeo.", estado: "EM_REPARO", tecnico: 2, valor: 390, laudo: "Fonte em curto. Troca da fonte.", horas: 96, atualizada: 8, pecasIds: [6] });

  // Histórico.
  os({ cliente: 5, tipo: "CELULAR", descricao: "Bateria estufada.", estado: "FINALIZADA", tecnico: 1, valor: 190, laudo: "Troca da bateria.", horas: 24 * 12, atualizada: 24 * 11, pecasIds: [2] });
  os({ cliente: 2, tipo: "CELULAR", descricao: "Conector de carga solto.", estado: "FINALIZADA", tecnico: 2, valor: 120, laudo: "Troca do conector de carga.", horas: 24 * 20, atualizada: 24 * 19, pecasIds: [3] });
  os({ cliente: 6, tipo: "NOTEBOOK", descricao: "Teclado com teclas falhando.", estado: "CANCELADA", tecnico: 2, valor: 260, laudo: "Teclado precisa ser trocado inteiro.", horas: 24 * 8, atualizada: 24 * 6 });
  os({ cliente: 3, tipo: "COMPUTADOR", descricao: "Barulho alto no gabinete.", estado: "FINALIZADA", tecnico: 1, valor: 80, laudo: "Cooler da CPU limpo e lubrificado; pasta trocada.", horas: 24 * 5, atualizada: 24 * 4, pecasIds: [7] });

  return {
    clientes,
    tecnicos,
    atendentes,
    administradores,
    pecas,
    hardwares,
    ordens,
    proximo: { pessoa: 100, hardware: hw + 1, peca: pecas.length + 1, os: n + 1 },
  };
}
