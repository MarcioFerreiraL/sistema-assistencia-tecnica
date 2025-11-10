package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Tecnico;
import com.programacaoiii.assistencia_tecnica.repositorios.TecnicoRepositorio;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import org.springframework.stereotype.Service;

@Service
public class TecnicoServico extends PessoaServicoAbstrato<Tecnico, TecnicoRepositorio> {

    public TecnicoServico(TecnicoRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    public Tecnico salvar(PessoaDto dto) {
        // implementar lógica de negocio
        Tecnico tecnico = new Tecnico(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(tecnico);
    }
}