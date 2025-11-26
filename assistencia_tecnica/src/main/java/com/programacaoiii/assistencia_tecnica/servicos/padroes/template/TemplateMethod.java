package com.programacaoiii.assistencia_tecnica.servicos.padroes.template;

public abstract class TemplateMethod {

    public final void executarProcessoReparo() {
        System.out.println("--- INICIANDO PROTOCOLO DE REPARO ---");
        diagnosticarProblema();
        separarMateriais();
        realizarReparo();
        realizarTestes();
        limpezaFinal();
        System.out.println("--- REPARO CONCLUÍDO ---");
    }

    protected abstract void diagnosticarProblema();
    protected abstract void realizarReparo();
    protected abstract void realizarTestes();
    protected abstract void separarMateriais();
    
    protected void limpezaFinal() {
        System.out.println(">> Executando limpeza padrão do equipamento.");
    }
}