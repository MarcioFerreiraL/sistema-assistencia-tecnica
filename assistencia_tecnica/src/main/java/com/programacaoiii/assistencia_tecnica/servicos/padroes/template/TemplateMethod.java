package com.programacaoiii.assistencia_tecnica.servicos.padroes.template;

public abstract class TemplateMethod {

    public final void executarProcessoReparo() {
        // 1. Diagnóstico (obrigatório)
        diagnosticarProblema();
        
        // 2. Separação de materiais (obrigatório)
        separarMateriais();
        
        // 3. Reparo (obrigatório)
        realizarReparo();
        
        // 4. Testes (obrigatório)
        realizarTestes();
        
        // 5. Limpeza (opcional)
        limpezaFinal();
    }

    protected abstract void diagnosticarProblema();
    protected abstract void realizarReparo();
    protected abstract void realizarTestes();
    protected abstract void separarMateriais();

    protected void limpezaFinal() {
        System.out.println("Realizando limpeza básica do equipamento.");
    }
}