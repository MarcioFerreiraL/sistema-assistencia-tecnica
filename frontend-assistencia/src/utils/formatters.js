export const formatCurrency = (value) => {
  if (value === undefined || value === null) return 'R$ 0,00';
  return new Intl.NumberFormat('pt-BR', { 
    style: 'currency', 
    currency: 'BRL' 
  }).format(value);
};

export const formatDate = (dateString) => {
  if (!dateString) return '-';
  // Corrige problemas de timezone criando a data baseada nas partes
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return dateString; // Retorna original se inválido
  return new Intl.DateTimeFormat('pt-BR').format(date);
};

export const formatDateTime = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short'
  }).format(date);
};