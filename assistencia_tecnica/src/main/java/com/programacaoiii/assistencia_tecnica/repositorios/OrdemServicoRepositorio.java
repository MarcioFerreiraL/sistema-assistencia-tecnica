package com.programacaoiii.assistencia_tecnica.repositorios;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;


public interface OrdemServicoRepositorio extends JpaRepository<OrdemServico, UUID>{

}