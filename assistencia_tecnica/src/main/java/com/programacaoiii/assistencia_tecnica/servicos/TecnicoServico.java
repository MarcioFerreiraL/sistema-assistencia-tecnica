package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Tecnico;
import com.programacaoiii.assistencia_tecnica.repositorios.TecnicoRepositorio;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class TecnicoServico extends PessoaServicoAbstrato<Tecnico, TecnicoRepositorio> {

    public TecnicoServico(TecnicoRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    public Tecnico salvar(PessoaDto dto) {
        Optional<Cliente> jaExiste = repositorio.findAllByCpf(dto.cpf());
        if (jaExiste.isPresent()) {
            throw new IllegalStateException("Já existe um Cliente com o CPF: " + dto.cpf());
        }
        Tecnico tecnico = new Tecnico(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(tecnico);
    }
}