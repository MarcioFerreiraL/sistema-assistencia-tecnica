package com.programacaoiii.assistencia_tecnica.servicos.padroes.state;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;

public interface StateInterface {
	public abstract void abrir(OrdemServico os);
	public abstract void orcamentar(OrdemServico os);
	public abstract void aprovar(OrdemServico os);
	public abstract void executar(OrdemServico os);
	public abstract void finalizar(OrdemServico os);
	public abstract void cancelar(OrdemServico os);
}