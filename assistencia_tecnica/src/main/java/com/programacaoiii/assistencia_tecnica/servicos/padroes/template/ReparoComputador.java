package com.programacaoiii.assistencia_tecnica.servicos.padroes.template;

public class ReparoComputador extends TemplateMethod {

    @Override
    protected void diagnosticarProblema() {
        System.out.println("[REPARO COMPUTADOR]: Testando fonte de alimentação (PSU) e pentes de memória RAM.");
    }

    @Override
    protected void realizarReparo() {
        System.out.println("[REPARO COMPUTADOR]: Trocando a placa-mãe e aplicando nova pasta térmica no processador.");
    }

    @Override
    protected void realizarTestes() {
        System.out.println("[REPARO COMPUTADOR]: Verificando boot da BIOS e carregamento do Sistema Operacional.");
    }

    @Override
    protected void separarMateriais() {
        System.out.println("[REPARO COMPUTADOR]: Separando chaves phillips, pasta térmica e ar comprimido.");
    }
    
    @Override
    protected void limpezaFinal() {
        System.out.println("[REPARO COMPUTADOR]: Limpando poeira interna do gabinete e organizando cabos.");
    }
}