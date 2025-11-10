package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;
import com.programacaoiii.assistencia_tecnica.repositorios.ClienteRepositorio;
import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import org.springframework.stereotype.Service;

@Service
public class ClienteServico extends PessoaServicoAbstrato<Cliente, ClienteRepositorio> {

    public ClienteServico(ClienteRepositorio repositorio) {
        super(repositorio); // Passa o repositório para o pai
    }

    @Override
    public Cliente salvar(PessoaDto dto) {
        // implementar lógica de negocio
        Cliente cliente = new Cliente(
            dto.nome(), 
            dto.cpf(), 
            dto.dataNascimento(), 
            dto.endereco()
        );
        return repositorio.save(cliente);
    }
}