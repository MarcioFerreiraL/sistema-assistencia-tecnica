package com.programacaoiii.assistencia_tecnica.repositorios;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long>{

	Optional<Cliente> findAllByCpf(String cpf);

	Optional<Cliente> findByCpf(String cpf);

}
