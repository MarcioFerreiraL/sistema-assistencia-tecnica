package com.programacaoiii.assistencia_tecnica.repositorios;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.enums.EstadoOSEnum;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; 

public interface OrdemServicoRepositorio extends JpaRepository<OrdemServico, UUID>{
    
    List<OrdemServico> findByTecnicoId(Long tecnicoId);
    List<OrdemServico> findByClienteId(Long clienteId);
    

    @Query("SELECT COUNT(os) FROM OrdemServico os JOIN os.pecasUtilizadas p WHERE p.numeroSerie = :pecaId")
    long countByPecaId(@Param("pecaId") Long pecaId);
    

    List<OrdemServico> findByEstado(EstadoOSEnum estado);
    
    List<OrdemServico> findByEstadoAndTecnicoId(EstadoOSEnum estado, Long tecnicoId);
}