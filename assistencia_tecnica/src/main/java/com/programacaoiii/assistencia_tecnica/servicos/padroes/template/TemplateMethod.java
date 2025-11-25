package com.programacaoiii.assistencia_tecnica.servicos.padroes.template;

public abstract class TemplateMethod {

    public final void executarProcessoReparo() {
        diagnosticarProblema();
        separarMateriais();
        realizarReparo();
        realizarTestes();
        limpezaFinal();
    }

    protected abstract void diagnosticarProblema();
    protected abstract void realizarReparo();
    protected abstract void realizarTestes();
    protected abstract void separarMateriais();
    protected abstract void limpezaFinal();
}