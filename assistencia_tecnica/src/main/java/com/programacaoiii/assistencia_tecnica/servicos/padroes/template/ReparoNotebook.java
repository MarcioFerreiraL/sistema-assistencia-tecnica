package com.programacaoiii.assistencia_tecnica.servicos.padroes.template;

public class ReparoNotebook extends TemplateMethod {

    @Override
    protected void diagnosticarProblema() {
        System.out.println("1. [Notebook] Verificando integridade do HD/SSD e Memória RAM.");
        System.out.println("1. [Notebook] Testando display e dobradiças.");
    }

    @Override
    protected void separarMateriais() {
        System.out.println("2. [Notebook] Separando chaves Philips de precisão e pasta térmica.");
    }

    @Override
    protected void realizarReparo() {
        System.out.println("3. [Notebook] Desmontagem da carcaça inferior e substituição do componente.");
    }

    @Override
    protected void realizarTestes() {
        System.out.println("4. [Notebook] Stress test de CPU e verificação de temperatura.");
    }
}