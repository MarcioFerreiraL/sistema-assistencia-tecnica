package com.programacaoiii.assistencia_tecnica.repositorios;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Administrador;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdministradorRepositorio extends JpaRepository<Administrador, UUID>{

	Optional<Cliente> findAllByCpf(String cpf);
	
}
