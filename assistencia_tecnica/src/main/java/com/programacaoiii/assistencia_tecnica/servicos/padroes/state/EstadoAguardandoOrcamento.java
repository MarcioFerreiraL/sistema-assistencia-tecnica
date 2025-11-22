package com.programacaoiii.assistencia_tecnica.servicos.padroes.state;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.enums.EstadoOSEnum;

public class EstadoAguardandoOrcamento extends EstadoOSAbstrato{
    @Override
    public void orcamentar(OrdemServico os) {
        System.out.println("Tecnico fazendo orcamento");
        os.setEstado(EstadoOSEnum.EM_REPARO);
    }
    
    @Override
    public void cancelar(OrdemServico os) {
        System.out.println("Cancelado");
        os.setEstado(EstadoOSEnum.CANCELADA);
    }
}
