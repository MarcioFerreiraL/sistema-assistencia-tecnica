package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.dtos.PecaDto;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Peca;
import com.programacaoiii.assistencia_tecnica.repositorios.OrdemServicoRepositorio;
import com.programacaoiii.assistencia_tecnica.repositorios.PecaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PecaServico {

	private final PecaRepositorio pecaRepositorio;
    private final OrdemServicoRepositorio ordemServicoRepositorio;

    public PecaServico(PecaRepositorio pecaRepositorio, OrdemServicoRepositorio ordemServicoRepositorio) {
        this.pecaRepositorio = pecaRepositorio;
        this.ordemServicoRepositorio = ordemServicoRepositorio;
    }

    @Transactional
    public Peca salvarPeca(PecaDto dto) {
        Peca peca = new Peca(
            dto.nome(),
            dto.tipoPeca()
        );
        return pecaRepositorio.save(peca);
    }

    @Transactional(readOnly = true)
    public List<Peca> buscarTodasPecas() {
        return pecaRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Peca buscarPecaPorId(UUID id) {
        return pecaRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada com ID: " + id));
    }

    @Transactional
    public Peca atualizarPeca(UUID id, PecaDto dto) {
        Peca pecaExistente = buscarPecaPorId(id);

        pecaExistente.setNome(dto.nome());
        pecaExistente.setTipoPeca(dto.tipoPeca());
        
        return pecaRepositorio.save(pecaExistente);
    }

    @Transactional
    public void excluirPeca(UUID id) {
        Peca peca = buscarPecaPorId(id); 
        
        long count = ordemServicoRepositorio.countByPecaId(id);
        
        if (count > 0) {
            throw new IllegalStateException("Não é possível excluir esta peça, pois ela está " +
                                            "associada a " + count + " Ordem(ns) de Serviço.");
        }
        
        pecaRepositorio.delete(peca);
    }
}