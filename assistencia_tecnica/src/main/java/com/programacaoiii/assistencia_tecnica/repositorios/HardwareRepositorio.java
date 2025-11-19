package com.programacaoiii.assistencia_tecnica.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Hardware;

public interface HardwareRepositorio extends JpaRepository<Hardware, Long>{

}
