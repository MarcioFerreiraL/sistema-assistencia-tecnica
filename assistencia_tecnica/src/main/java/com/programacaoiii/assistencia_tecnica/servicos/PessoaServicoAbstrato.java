package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.PessoaAbstrato;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public abstract class PessoaServicoAbstrato<
    E extends PessoaAbstrato, 
    R extends JpaRepository<E, Long>
> {

    protected final R repositorio;

    public PessoaServicoAbstrato(R repositorio) {
        this.repositorio = repositorio;
    }

    protected void validarIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalStateException("A data de nascimento é obrigatória.");
        }
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        if (idade < 18) {
            throw new IllegalStateException("A pessoa deve ter pelo menos 18 anos. Idade calculada: " + idade);
        }
    }

    @Transactional
    public abstract E salvar(PessoaDto dto);
    @Transactional(readOnly = true)
    public E buscarPorId(Long id) {
                                  
        return repositorio.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Objeto não encontrado(a) com ID: " + id
            ));
    }

    @Transactional(readOnly = true)
    public List<E> buscarTodos() {
        return repositorio.findAll();
    }


    @Transactional
    public E atualizar(Long id, PessoaDto dto) {
        validarIdade(dto.dataNascimento());
        
        E entidadeExistente = buscarPorId(id); 

        entidadeExistente.setNome(dto.nome());
        entidadeExistente.setCpf(dto.cpf());
        entidadeExistente.setDataNascimento(dto.dataNascimento());
        entidadeExistente.setEndereco(dto.endereco());
        
        return repositorio.save(entidadeExistente);
    }

    @Transactional
    public void excluir(Long id) {
        E entidade = buscarPorId(id);
        repositorio.delete(entidade);
    }
}