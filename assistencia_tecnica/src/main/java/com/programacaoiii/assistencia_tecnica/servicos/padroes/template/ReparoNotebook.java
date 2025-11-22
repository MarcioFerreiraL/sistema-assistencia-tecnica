package com.programacaoiii.assistencia_tecnica.servicos.padroes.template;

public class ReparoNotebook extends TemplateMethod {

    @Override
    protected void diagnosticarProblema() {
        System.out.println("[REPARO NOTEBOOK]: Executando diagnósticos de hardware (memória, HD, tela).");
    }

    @Override
    protected void realizarReparo() {
        System.out.println("[REPARO NOTEBOOK]: Desmontando carcaça, realizando solda na placa-mãe.");
    }

    @Override
    protected void realizarTestes() {
        System.out.println("[REPARO NOTEBOOK]: Testando boot do sistema e conexão de periféricos.");
    }

    @Override
    protected void separarMateriais() {
        System.out.println("[REPARO NOTEBOOK]: Separando chaves de precisão, pasta térmica e solda.");
    }
    
    @Override
    protected void limpezaFinal() {
        System.out.println("[REPARO NOTEBOOK]: Limpando tela e teclado.");
    }
}