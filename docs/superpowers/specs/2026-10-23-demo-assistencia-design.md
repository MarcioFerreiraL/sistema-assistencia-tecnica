# Demo pública e nova identidade (Ponto de Solda)

## Por quê

O Márcio quer uma demo de cada projeto principal no ar, ligada ao estudo de caso em
marciodev.com/projetos/assistencia-tecnica. Um recrutador abre no celular, entra com um
clique em cada perfil (cliente, atendente, técnico, administrador) e vê a ordem de serviço
andar pela máquina de estados, sem servidor por trás.

## Decisões do Márcio

- Só frontend, com dados de exemplo no navegador (sem backend nem banco no ar).
- Redesenho completo, com identidade nova, no mesmo molde da Mandacaru Barbearia.

## Como a demo funciona

- `VITE_DEMO=1` no build troca o cliente HTTP (`src/services/http.js`, axios) por um backend
  em memória (`src/services/demo/`), com as mesmas funções. Sem a variável, o frontend fala
  com a API Spring Boot como antes.
- O backend de demo segue as regras da API:
  - State: ABERTA → (assumir) AGUARDANDO_ORCAMENTO → (orçamentar) AGUARDANDO_APROVACAO →
    (aprovar) EM_REPARO → (finalizar) FINALIZADA; orçamentar também vale a partir de ABERTA;
    cancelar vale em ABERTA, AGUARDANDO_ORCAMENTO, AGUARDANDO_APROVACAO e EM_REPARO; qualquer
    outra ação responde "Ação '<ação>' não é permitida neste estado.".
  - Template Method: executar só em EM_REPARO, exige técnico ("Não é possível executar o
    reparo. Nenhum técnico foi atribuído a esta OS.") e devolve o roteiro do tipo de aparelho
    (diagnóstico, materiais, reparo, testes, limpeza), com os mesmos passos das classes
    `ReparoNotebook`, `ReparoCelular`, `ReparoComputador` e `ReparoOutros`.
  - Pessoas: CPF único por perfil, idade mínima de 18 anos; não exclui cliente ou técnico com
    OS, nem peça usada em OS; OS só é excluída enquanto ABERTA.
- Dados no `localStorage` do visitante, com datas geradas em relação a hoje.
- Login com um clique por perfil e botão "Restaurar dados de exemplo"; faixa fixa avisando
  que é demonstração com dados fictícios.
- Build estático do Vite publicado como Worker de assets estáticos da Cloudflare em
  `assistencia.marciodev.com`, com fallback de SPA.

## Identidade (decisão minha, o Márcio vê na prévia)

- Nome fictício: Ponto de Solda, assistência técnica em Surubim (PE).
- Conceito: a etiqueta de papel amarrada no aparelho que entra na bancada, com o número da
  OS em fita de rotuladora (Dymo) e os furos marcando cada etapa do conserto.
- Cores: papel manila `#F3ECDC` (fundo), grafite `#1D2026` (texto e fita), amarelo de
  multímetro `#F5C518` (só sobre grafite), carbono `#34379C` (links, foco, ações),
  linha `#D8CCB0`, apagado `#5E5A50`. Sem verde.
- Tipo: Archivo em duas larguras (expandida e pesada nos títulos e na fita; normal no texto).
- Sem fotos externas.

## Fora do escopo

- Mudanças no backend (inclusive a mensagem "Já existe um Cliente" no cadastro de atendente,
  que a demo corrige só no texto).
- Autenticação real: como na API, o login é por perfil e CPF.
