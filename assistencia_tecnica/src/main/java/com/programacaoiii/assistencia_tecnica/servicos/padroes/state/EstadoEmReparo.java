package com.programacaoiii.assistencia_tecnica.servicos.padroes.state;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.enums.EstadoOS;

public class EstadoEmReparo extends EstadoOSAbstrato {
    
    // Note que "executar" não muda o estado, apenas realiza o trabalho.
    @Override
    public void executar(OrdemServico os) {
        System.out.println("Ação: Executando o reparo (estado continua 'EM_REPARO')");
        // A lógica de Template Method seria chamada aqui
    }

    @Override
    public void finalizar(OrdemServico os) {
        System.out.println("Transição: EM_REPARO -> FINALIZADA");
        os.setEstado(EstadoOS.FINALIZADA);
    }
    
    @Override
    public void cancelar(OrdemServico os) {
        System.out.println("Transição: EM_REPARO -> CANCELADA");
        os.setEstado(EstadoOS.CANCELADA);
    }
}