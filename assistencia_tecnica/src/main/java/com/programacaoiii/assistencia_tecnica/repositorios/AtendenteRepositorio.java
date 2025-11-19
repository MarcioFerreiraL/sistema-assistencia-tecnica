package com.programacaoiii.assistencia_tecnica.repositorios;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Atendente;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;

public interface AtendenteRepositorio extends JpaRepository<Atendente, Long>{

	Optional<Cliente> findAllByCpf(String cpf);

	Optional<Atendente> findByCpf(String cpf);

}
