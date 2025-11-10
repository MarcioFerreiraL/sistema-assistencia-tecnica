package com.programacaoiii.assistencia_tecnica.servicos.padroes.template;

public class ReparoCelular extends TemplateMethod {

    @Override
    protected void diagnosticarProblema() {
        System.out.println("[REPARO CELULAR]: Testando bateria e resposta da tela touch.");
    }

    @Override
    protected void realizarReparo() {
        System.out.println("[REPARO CELULAR]: Aquecendo carcaça para descolar a tela, trocando a bateria.");
    }

    @Override
    protected void realizarTestes() {
        System.out.println("[REPARO CELULAR]: Verificando ciclos de carga da nova bateria e resposta da tela.");
    }

    @Override
    protected void separarMateriais() {
        System.out.println("[REPARO CELULAR]: Separando ventosas, pistola de calor e kit de chaves de precisão.");
    }
}