package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Atendente;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;
import com.programacaoiii.assistencia_tecnica.repositorios.AtendenteRepositorio;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class AtendenteServico extends PessoaServicoAbstrato<Atendente, AtendenteRepositorio> {

    public AtendenteServico(AtendenteRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    public Atendente salvar(PessoaDto dto) {
        Optional<Cliente> jaExiste = repositorio.findAllByCpf(dto.cpf());
        if (jaExiste.isPresent()) {
            throw new IllegalStateException("Já existe um Cliente com o CPF: " + dto.cpf());
        }
    	Atendente tecnico = new Atendente(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(tecnico);
    }
}