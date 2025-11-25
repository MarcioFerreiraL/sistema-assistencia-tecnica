package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Administrador;
import com.programacaoiii.assistencia_tecnica.repositorios.AdministradorRepositorio;

import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministradorServico extends PessoaServicoAbstrato<Administrador, AdministradorRepositorio> {

    public AdministradorServico(AdministradorRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    public Administrador salvar(PessoaDto dto) {
    	
        Optional<Administrador> jaExiste = repositorio.findAllByCpf(dto.cpf());
        if (jaExiste.isPresent()) {
            throw new IllegalStateException("Já existe um Cliente com o CPF: " + dto.cpf());
        }
        
        validarIdade(dto.dataNascimento());
        
        Administrador tecnico = new Administrador(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(tecnico);
    }
    
    @Transactional(readOnly = true)
    public Administrador buscarPorCpf(String cpf) {
        return repositorio.findByCpf(cpf)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Administrador com CPF " + cpf + " não encontrado."));
    }
}