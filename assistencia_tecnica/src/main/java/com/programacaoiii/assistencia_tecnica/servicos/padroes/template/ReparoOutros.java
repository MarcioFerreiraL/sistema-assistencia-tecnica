package com.programacaoiii.assistencia_tecnica.servicos.padroes.template;

public class ReparoOutros extends TemplateMethod {

    @Override
    protected void diagnosticarProblema() {
        System.out.println("1. [Outros] Análise genérica de funcionamento elétrico/mecânico.");
        System.out.println("1. [Outros] Verificação visual de componentes queimados ou quebrados.");
    }

    @Override
    protected void separarMateriais() {
        System.out.println("2. [Outros] Separando kit universal de ferramentas.");
        System.out.println("2. [Outros] Consultando manual técnico do fabricante específico.");
    }

    @Override
    protected void realizarReparo() {
        System.out.println("3. [Outros] Desmontagem e reparo conforme especificação do defeito.");
    }

    @Override
    protected void realizarTestes() {
        System.out.println("4. [Outros] Teste funcional completo das funcionalidades principais.");
    }
    
    @Override
    protected void limpezaFinal() {
        System.out.println(">> [Outros] Limpeza externa com álcool isopropílico e remoção de etiquetas antigas.");
    }
}