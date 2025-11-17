package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Tecnico;
import com.programacaoiii.assistencia_tecnica.repositorios.OrdemServicoRepositorio;
import com.programacaoiii.assistencia_tecnica.repositorios.TecnicoRepositorio;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID; 

@Service
public class TecnicoServico extends PessoaServicoAbstrato<Tecnico, TecnicoRepositorio> {

    private final OrdemServicoRepositorio ordemServicoRepositorio;

    public TecnicoServico(TecnicoRepositorio repositorio, OrdemServicoRepositorio ordemServicoRepositorio) {
        super(repositorio);
        this.ordemServicoRepositorio = ordemServicoRepositorio;
    }

    @Override
    public Tecnico salvar(PessoaDto dto) {
        Optional<Tecnico> jaExiste = repositorio.findByCpf(dto.cpf());
        if (jaExiste.isPresent()) {
            throw new IllegalStateException("Já existe um Técnico com o CPF: " + dto.cpf());
        }
        
        validarIdade(dto.dataNascimento());
        
        Tecnico tecnico = new Tecnico(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(tecnico);
    }
    
    @Override
    @Transactional
    public void excluir(UUID id) {
        List<OrdemServico> listaOS = ordemServicoRepositorio.findByTecnicoId(id);
        
        if (!listaOS.isEmpty()) {
            throw new IllegalStateException("Não é possível excluir este técnico, pois ele está associado a " + 
                                            listaOS.size() + " Ordem(ns) de Serviço.");
        }
        
        super.excluir(id);
    }
    
    @Transactional(readOnly = true)
    public Tecnico buscarPorCpf(String cpf) {
        return repositorio.findByCpf(cpf)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Tecnico com CPF " + cpf + " não encontrado."));
    }
}