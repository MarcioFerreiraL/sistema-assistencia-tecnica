package com.programacaoiii.assistencia_tecnica.repositorios;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.enums.EstadoOSEnum;

import org.springframework.data.jpa.repository.Query; // Importar Query
import org.springframework.data.repository.query.Param; // Importar Param

public interface OrdemServicoRepositorio extends JpaRepository<OrdemServico, UUID>{
    
    List<OrdemServico> findByTecnicoId(UUID tecnicoId);
    List<OrdemServico> findByClienteId(UUID clienteId);
    

    @Query("SELECT COUNT(os) FROM OrdemServico os JOIN os.pecasUtilizadas p WHERE p.numeroSerie = :pecaId")
    long countByPecaId(@Param("pecaId") UUID pecaId);
    

    List<OrdemServico> findByEstado(EstadoOSEnum estado);
    
    List<OrdemServico> findByEstadoAndTecnicoId(EstadoOSEnum estado, UUID tecnicoId);
}