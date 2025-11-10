package com.programacaoiii.assistencia_tecnica.servicos.padroes.state;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;

public abstract class EstadoOSAbstrato implements StateInterface {

    protected void proibir(String acao) {
        throw new IllegalStateException("Ação '" + acao + "' não é permitida neste estado.");
    }

    @Override
    public void abrir(OrdemServico os) {
        proibir("Abrir");
    }

    @Override
    public void orcamentar(OrdemServico os) {
        proibir("Orçamentar");
    }

    @Override
    public void aprovar(OrdemServico os) {
        proibir("Aprovar");
    }

    @Override
    public void executar(OrdemServico os) {
        proibir("Executar");
    }

    @Override
    public void finalizar(OrdemServico os) {
        proibir("Finalizar");
    }

    @Override
    public void cancelar(OrdemServico os) {
        proibir("Cancelar");
    }
}