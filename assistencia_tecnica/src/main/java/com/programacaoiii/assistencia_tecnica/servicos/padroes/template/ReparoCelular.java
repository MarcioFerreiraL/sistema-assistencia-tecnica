package com.programacaoiii.assistencia_tecnica.servicos.padroes.template;

public class ReparoCelular extends TemplateMethod {

    @Override
    protected void diagnosticarProblema() {
        System.out.println("1. [Celular] Verificando integridade do display e touch.");
        System.out.println("1. [Celular] Testando consumo da bateria e conector de carga.");
    }

    @Override
    protected void separarMateriais() {
        System.out.println("2. [Celular] Separando ventosas, espátulas plásticas e estação de calor.");
        System.out.println("2. [Celular] Separando cola B-7000 ou fita dupla face.");
    }

    @Override
    protected void realizarReparo() {
        System.out.println("3. [Celular] Aquecimento e remoção da tela/tampa traseira.");
        System.out.println("3. [Celular] Desconexão dos flats e troca do componente.");
    }

    @Override
    protected void realizarTestes() {
        System.out.println("4. [Celular] Teste de sensibilidade do touch em todos os pontos.");
        System.out.println("4. [Celular] Teste de câmeras e sensores de proximidade.");
    }
}