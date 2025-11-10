package com.programacaoiii.assistencia_tecnica.repositorios;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Peca;

public interface PecaRepositorio extends JpaRepository<Peca, UUID>{
	
}