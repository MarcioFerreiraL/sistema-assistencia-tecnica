package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Atendente;
import com.programacaoiii.assistencia_tecnica.repositorios.AtendenteRepositorio;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import org.springframework.stereotype.Service;

@Service
public class AtendenteServico extends PessoaServicoAbstrato<Atendente, AtendenteRepositorio> {

    public AtendenteServico(AtendenteRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    public Atendente salvar(PessoaDto dto) {
        // Lógica
    	Atendente tecnico = new Atendente(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(tecnico);
    }
}