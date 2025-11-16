package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;
import com.programacaoiii.assistencia_tecnica.repositorios.ClienteRepositorio;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ClienteServico extends PessoaServicoAbstrato<Cliente, ClienteRepositorio> {

    public ClienteServico(ClienteRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    public Cliente salvar(PessoaDto dto) {
        Optional<Cliente> jaExiste = repositorio.findAllByCpf(dto.cpf());
        if (jaExiste.isPresent()) {
            throw new IllegalStateException("Já existe um Cliente com o CPF: " + dto.cpf());
        }
        
        Cliente cliente = new Cliente(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(cliente);
    }
}