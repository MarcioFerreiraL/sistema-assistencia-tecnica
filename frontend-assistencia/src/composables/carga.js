import { onMounted, ref } from "vue";

/** Carrega dados de uma tela; `recarregar()` busca de novo depois de uma ação. */
export function useCarga(buscar) {
  const dados = ref(null);
  const erro = ref("");
  const carregando = ref(true);

  async function recarregar() {
    try {
      dados.value = await buscar();
      erro.value = "";
    } catch (e) {
      erro.value = e instanceof Error ? e.message : "Não foi possível carregar.";
    } finally {
      carregando.value = false;
    }
  }

  onMounted(recarregar);
  return { dados, erro, carregando, recarregar };
}
