package com.programacaoiii.assistencia_tecnica.repositorios;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Tecnico;

public interface TecnicoRepositorio extends JpaRepository<Tecnico, UUID>{

	Optional<Cliente> findAllByCpf(String cpf);

	Optional<Tecnico> findByCpf(String cpf);

}