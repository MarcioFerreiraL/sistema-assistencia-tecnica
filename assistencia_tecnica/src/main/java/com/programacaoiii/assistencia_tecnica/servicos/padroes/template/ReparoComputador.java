package com.programacaoiii.assistencia_tecnica.servicos.padroes.template;

public class ReparoComputador extends TemplateMethod {

    @Override
    protected void diagnosticarProblema() {
        System.out.println("1. [Computador] Verificando fonte de alimentação (PSU) e cabos.");
        System.out.println("1. [Computador] Checando bips da placa-mãe e coolers.");
    }

    @Override
    protected void separarMateriais() {
        System.out.println("2. [Computador] Separando chaves fenda/philips e ar comprimido.");
        System.out.println("2. [Computador] Separando pasta térmica.");
    }

    @Override
    protected void realizarReparo() {
        System.out.println("3. [Computador] Abertura do gabinete lateral.");
        System.out.println("3. [Computador] Substituição de hardware ou limpeza profunda dos contatos.");
    }

    @Override
    protected void realizarTestes() {
        System.out.println("4. [Computador] Teste de bench por 30 minutos.");
        System.out.println("4. [Computador] Verificação de fluxo de ar.");
    }
    
}