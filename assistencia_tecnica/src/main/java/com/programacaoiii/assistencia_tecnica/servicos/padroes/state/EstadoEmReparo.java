package com.programacaoiii.assistencia_tecnica.servicos.padroes.state;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Tecnico;
import com.programacaoiii.assistencia_tecnica.modelos.enums.EstadoOSEnum;

public class EstadoEmReparo extends EstadoOSAbstrato {
    
    @Override
    public void executar(OrdemServico os) {
        System.out.println("Ação: Executando o reparo (estado continua 'EM_REPARO')");

        // Buscar o técnico responsável pela OS
        Tecnico tecnico = os.getTecnicoResponsavel();

        if (tecnico == null) {
            throw new IllegalStateException("Não é possível executar o reparo. Nenhum técnico foi atribuído a esta OS.");
        }
      
        tecnico.executarOS(os);
    }

    @Override
    public void finalizar(OrdemServico os) {
        System.out.println("Transição: EM_REPARO -> FINALIZADA");
        os.setEstado(EstadoOSEnum.FINALIZADA);
    }
    
    @Override
    public void cancelar(OrdemServico os) {
        System.out.println("Transição: EM_REPARO -> CANCELADA");
        os.setEstado(EstadoOSEnum.CANCELADA);
    }
}