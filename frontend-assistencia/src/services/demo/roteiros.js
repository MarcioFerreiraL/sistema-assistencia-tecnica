/**
 * Roteiros de reparo do padrão Template Method do backend
 * (TemplateMethod.executarProcessoReparo e as classes Reparo*). A ordem das etapas é fixa;
 * cada tipo de aparelho muda só os passos.
 */

const LIMPEZA_PADRAO = ["Executando limpeza padrão do equipamento."];

const ROTEIROS = {
  NOTEBOOK: {
    diagnostico: ["Verificando integridade do HD/SSD e Memória RAM.", "Testando display e dobradiças."],
    materiais: ["Separando chaves Philips de precisão e pasta térmica."],
    reparo: ["Desmontagem da carcaça inferior e substituição do componente."],
    testes: ["Stress test de CPU e verificação de temperatura."],
  },
  CELULAR: {
    diagnostico: ["Verificando integridade do display e touch.", "Testando consumo da bateria e conector de carga."],
    materiais: ["Separando ventosas, espátulas plásticas e estação de calor.", "Separando cola B-7000 ou fita dupla face."],
    reparo: ["Aquecimento e remoção da tela/tampa traseira.", "Desconexão dos flats e troca do componente."],
    testes: ["Teste de sensibilidade do touch em todos os pontos.", "Teste de câmeras e sensores de proximidade."],
  },
  COMPUTADOR: {
    diagnostico: ["Verificando fonte de alimentação (PSU) e cabos.", "Checando bips da placa-mãe e coolers."],
    materiais: ["Separando chaves fenda/philips e ar comprimido.", "Separando pasta térmica."],
    reparo: ["Abertura do gabinete lateral.", "Substituição de hardware ou limpeza profunda dos contatos."],
    testes: ["Teste de bench por 30 minutos.", "Verificação de fluxo de ar."],
  },
  OUTROS: {
    diagnostico: [
      "Análise genérica de funcionamento elétrico/mecânico.",
      "Verificação visual de componentes queimados ou quebrados.",
    ],
    materiais: ["Separando kit universal de ferramentas.", "Consultando manual técnico do fabricante específico."],
    reparo: ["Desmontagem e reparo conforme especificação do defeito."],
    testes: ["Teste funcional completo das funcionalidades principais."],
    limpeza: ["Limpeza externa com álcool isopropílico e remoção de etiquetas antigas."],
  },
};

/** @returns {{ etapa: string, passos: string[] }[]} */
export function roteiroDe(tipo) {
  const r = ROTEIROS[tipo] ?? ROTEIROS.OUTROS;
  return [
    { etapa: "Diagnóstico", passos: r.diagnostico },
    { etapa: "Materiais", passos: r.materiais },
    { etapa: "Reparo", passos: r.reparo },
    { etapa: "Testes", passos: r.testes },
    { etapa: "Limpeza", passos: r.limpeza ?? LIMPEZA_PADRAO },
  ];
}
