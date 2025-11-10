package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.PessoaAbstrato;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

public abstract class PessoaServicoAbstrato<
    E extends PessoaAbstrato, 
    R extends JpaRepository<E, UUID>
> {

    protected final R repositorio;

    public PessoaServicoAbstrato(R repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public abstract E salvar(PessoaDto dto); // A criação é específica

    @Transactional(readOnly = true)
    public List<E> buscarTodos() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public E buscarPorId(UUID id) {
                                  
        return repositorio.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Objeto não encontrado(a) com ID: " + id
            ));
    }

    @Transactional
    public E atualizar(UUID id, PessoaDto dto) {
        E entidadeExistente = buscarPorId(id); // Reusa o método de busca

        // Atualiza os campos da PessoaAbstrata
        entidadeExistente.setNome(dto.nome());
        entidadeExistente.setCpf(dto.cpf());
        entidadeExistente.setDataNascimento(dto.dataNascimento());
        entidadeExistente.setEndereco(dto.endereco());
        
        return repositorio.save(entidadeExistente);
    }

    @Transactional
    public void excluir(UUID id) {
        E entidade = buscarPorId(id);
        // Aqui poderiam entrar regras de negócio genéricas
        // antes de excluir.
        repositorio.delete(entidade);
    }
}