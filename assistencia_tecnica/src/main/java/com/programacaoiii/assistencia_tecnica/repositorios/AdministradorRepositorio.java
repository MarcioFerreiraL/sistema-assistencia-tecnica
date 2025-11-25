package com.programacaoiii.assistencia_tecnica.repositorios;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdministradorRepositorio extends JpaRepository<Administrador, Long>{

	Optional<Administrador> findAllByCpf(String cpf);

	Optional<Administrador> findByCpf(String cpf);
	
}
