package com.programacaoiii.assistencia_tecnica.repositorios;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Tecnico;

public interface TecnicoRepositorio extends JpaRepository<Tecnico, UUID>{

}