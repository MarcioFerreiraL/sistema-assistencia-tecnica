package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Administrador;
import com.programacaoiii.assistencia_tecnica.repositorios.AdministradorRepositorio;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import org.springframework.stereotype.Service;

@Service
public class AdministradorServico extends PessoaServicoAbstrato<Administrador, AdministradorRepositorio> {

    public AdministradorServico(AdministradorRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    public Administrador salvar(PessoaDto dto) {
        // Lógica 
        Administrador tecnico = new Administrador(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(tecnico);
    }
}