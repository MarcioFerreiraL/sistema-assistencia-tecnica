package com.programacaoiii.assistencia_tecnica.servicos.padroes.state;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.enums.EstadoOSEnum;

public class EstadoAberta extends EstadoOSAbstrato {

    @Override
    public void orcamentar(OrdemServico os) {
        System.out.println("Transição: ABERTA -> AGUARDANDO_APROVACAO");
        os.setEstado(EstadoOSEnum.AGUARDANDO_APROVACAO);
    }
    
    @Override
    public void cancelar(OrdemServico os) {
        System.out.println("Transição: ABERTA -> CANCELADA");
        os.setEstado(EstadoOSEnum.CANCELADA);
    }
}