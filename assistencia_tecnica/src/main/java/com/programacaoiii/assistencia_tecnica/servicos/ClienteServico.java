package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.repositorios.ClienteRepositorio;
import com.programacaoiii.assistencia_tecnica.repositorios.OrdemServicoRepositorio;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteServico extends PessoaServicoAbstrato<Cliente, ClienteRepositorio> {

    private final OrdemServicoRepositorio ordemServicoRepositorio;

    public ClienteServico(ClienteRepositorio repositorio, OrdemServicoRepositorio ordemServicoRepositorio) {
        super(repositorio);
        this.ordemServicoRepositorio = ordemServicoRepositorio;
    }


    @Override
    public Cliente salvar(PessoaDto dto) {
        Optional<Cliente> jaExiste = repositorio.findByCpf(dto.cpf());
        if (jaExiste.isPresent()) {
            throw new IllegalStateException("Já existe um Cliente com o CPF: " + dto.cpf());
        }
        
        validarIdade(dto.dataNascimento());
        
        Cliente cliente = new Cliente(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(cliente);
    }
    
    @Override
    @Transactional
    public void excluir(UUID id) {
        List<OrdemServico> listaOS = ordemServicoRepositorio.findByClienteId(id);
        
        if (!listaOS.isEmpty()) {
            throw new IllegalStateException("Não é possível excluir este cliente, pois ele está associado a " + 
                                            listaOS.size() + " Ordem(ns) de Serviço.");
        }
        super.excluir(id);
    }
    
    @Transactional(readOnly = true)
    public Cliente buscarPorCpf(String cpf) {
        return repositorio.findByCpf(cpf)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente com CPF " + cpf + " não encontrado."));
    }
}