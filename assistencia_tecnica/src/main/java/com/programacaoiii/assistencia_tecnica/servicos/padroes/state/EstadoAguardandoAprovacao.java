package com.programacaoiii.assistencia_tecnica.servicos.padroes.state;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.enums.EstadoOSEnum;

public class EstadoAguardandoAprovacao extends EstadoOSAbstrato {

    @Override
    public void aprovar(OrdemServico os) {
        System.out.println("Transição: AGUARDANDO_APROVACAO -> EM_REPARO");
        os.setEstado(EstadoOSEnum.EM_REPARO);
    }
    
    @Override
    public void cancelar(OrdemServico os) {
        System.out.println("Transição: AGUARDANDO_APROVACAO -> CANCELADA");
        os.setEstado(EstadoOSEnum.CANCELADA);
    }
}