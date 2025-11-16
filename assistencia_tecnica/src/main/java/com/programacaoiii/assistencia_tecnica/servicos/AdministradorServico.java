package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Administrador;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;
import com.programacaoiii.assistencia_tecnica.repositorios.AdministradorRepositorio;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class AdministradorServico extends PessoaServicoAbstrato<Administrador, AdministradorRepositorio> {

    public AdministradorServico(AdministradorRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    public Administrador salvar(PessoaDto dto) {
    	
        Optional<Cliente> jaExiste = repositorio.findAllByCpf(dto.cpf());
        if (jaExiste.isPresent()) {
            throw new IllegalStateException("Já existe um Cliente com o CPF: " + dto.cpf());
        }
        
        Administrador tecnico = new Administrador(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(tecnico);
    }
}