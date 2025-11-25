package com.programacaoiii.assistencia_tecnica.servicos.padroes.state;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.enums.EstadoOSEnum;

public class EstadoAguardandoAprovacao extends EstadoOSAbstrato {

    @Override
    public void aprovar(OrdemServico os) {
        os.setEstado(EstadoOSEnum.EM_REPARO);
    }
    
    @Override
    public void cancelar(OrdemServico os) {
        os.setEstado(EstadoOSEnum.CANCELADA);
    }
}