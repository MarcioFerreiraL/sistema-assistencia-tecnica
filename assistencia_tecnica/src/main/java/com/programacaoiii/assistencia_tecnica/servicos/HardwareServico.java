package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.dtos.HardwareDto;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Hardware;
import com.programacaoiii.assistencia_tecnica.repositorios.ClienteRepositorio;
import com.programacaoiii.assistencia_tecnica.repositorios.HardwareRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class HardwareServico {

    private final HardwareRepositorio hardwareRepositorio;
    private final ClienteRepositorio clienteRepositorio;

    public HardwareServico(HardwareRepositorio hardwareRepositorio, ClienteRepositorio clienteRepositorio) {
        this.hardwareRepositorio = hardwareRepositorio;
        this.clienteRepositorio = clienteRepositorio;
    }

    @Transactional
    public Hardware salvarHardware(HardwareDto dto) {
        Cliente cliente = clienteRepositorio.findById(dto.clienteId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + dto.clienteId()));

        Hardware hardware = new Hardware(
            dto.tipoHardware(),
            cliente
        );
        return hardwareRepositorio.save(hardware);
    }

    @Transactional(readOnly = true)
    public List<Hardware> buscarTodosHardwares() {
        return hardwareRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Hardware buscarHardwarePorId(Long id) {
        return hardwareRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Hardware não encontrado com ID: " + id));
    }

    @Transactional
    public Hardware atualizarHardware(Long id, HardwareDto dto) {
        Hardware hardwareExistente = buscarHardwarePorId(id);
        
        // Se o cliente mudou, busca o novo cliente
        if (!hardwareExistente.getCliente().getId().equals(dto.clienteId())) {
             Cliente novoCliente = clienteRepositorio.findById(dto.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + dto.clienteId()));
            hardwareExistente.setCliente(novoCliente);
        }
        
        hardwareExistente.setTipoHardware(dto.tipoHardware());
        
        return hardwareRepositorio.save(hardwareExistente);
    }

    @Transactional
    public void excluirHardware(Long id) {
        Hardware hardware = buscarHardwarePorId(id);
        hardwareRepositorio.delete(hardware);
    }
}