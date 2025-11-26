package com.programacaoiii.assistencia_tecnica.servicos;

import com.programacaoiii.assistencia_tecnica.dtos.OrdemServicoDto;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Cliente;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Hardware;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Tecnico;
import com.programacaoiii.assistencia_tecnica.modelos.enums.EstadoOSEnum;
import com.programacaoiii.assistencia_tecnica.repositorios.ClienteRepositorio;
import com.programacaoiii.assistencia_tecnica.repositorios.HardwareRepositorio;
import com.programacaoiii.assistencia_tecnica.repositorios.OrdemServicoRepositorio;
import com.programacaoiii.assistencia_tecnica.repositorios.TecnicoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrdemServicoServico {

    private final OrdemServicoRepositorio osRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final HardwareRepositorio hardwareRepositorio;
    private final TecnicoRepositorio tecnicoRepositorio;

    public OrdemServicoServico(OrdemServicoRepositorio osRepositorio, ClienteRepositorio clienteRepositorio, HardwareRepositorio hardwareRepositorio, TecnicoRepositorio tecnicoRepositorio) {
        this.osRepositorio = osRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.hardwareRepositorio = hardwareRepositorio;
        this.tecnicoRepositorio = tecnicoRepositorio;
    }

    @Transactional
    public OrdemServico salvarOS(OrdemServicoDto dto) {
        // buscar todas as entidades por ID
        Cliente cliente = clienteRepositorio.findById(dto.clienteId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + dto.clienteId()));
        
        Hardware hardware = hardwareRepositorio.findById(dto.hardwareId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Hardware não encontrado com ID: " + dto.hardwareId()));
        
        Tecnico tecnico = null;
        if (dto.tecnicoId() != null) {
            tecnico = tecnicoRepositorio.findById(dto.tecnicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Técnico não encontrado com ID: " + dto.tecnicoId()));
        }

        OrdemServico os = new OrdemServico(
            dto.valorOrcamento(),
            dto.descricao(),
            cliente,
            hardware,
            tecnico
        );

        return osRepositorio.save(os);
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> buscarTodasOS() {
        return osRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public OrdemServico buscarOSPorId(UUID id) {
        return osRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de Serviço não encontrada com ID: " + id));
    }

    @Transactional
    public OrdemServico atualizarOS(UUID id, OrdemServicoDto dto) {
        OrdemServico osExistente = buscarOSPorId(id);

        osExistente.setValorOrcamento(dto.valorOrcamento());
        osExistente.setDescricao(dto.descricao());

        if (dto.tecnicoId() != null) {
            Tecnico tecnico = tecnicoRepositorio.findById(dto.tecnicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Técnico não encontrado com ID: " + dto.tecnicoId()));
            osExistente.setTecnicoResponsavel(tecnico);
            
            if (osExistente.getEstado() == EstadoOSEnum.ABERTA) {
                 osExistente.setEstado(EstadoOSEnum.AGUARDANDO_ORCAMENTO);
            }
        }

        return osRepositorio.save(osExistente);
    }

    @Transactional
    public void excluirOS(UUID id) {
        OrdemServico os = buscarOSPorId(id);
        if (os.getEstado() != EstadoOSEnum.ABERTA) {
           throw new IllegalStateException("Não é possível excluir uma OS em andamento.");
        }
        osRepositorio.delete(os);
    }
    
    @Transactional
    public OrdemServico aprovarOrcamento(UUID id) {
        OrdemServico os = buscarOSPorId(id);
        os.aprovar();
        return osRepositorio.save(os);
    }
    
    @Transactional
    public OrdemServico cancelarOS(UUID id) {
        OrdemServico os = buscarOSPorId(id);
        os.cancelar(); 
        return osRepositorio.save(os);
    }
    
    @Transactional
    public OrdemServico executarOS(UUID id) {
        OrdemServico os = buscarOSPorId(id);
        os.executar(); 
        return osRepositorio.save(os);
    }
    
    @Transactional
    public OrdemServico finalizarOS(UUID id) {
        OrdemServico os = buscarOSPorId(id);
        os.finalizar(); 
        return osRepositorio.save(os);
    }
    
    @Transactional
    public OrdemServico orcamentarOS(UUID id) {
        OrdemServico os = buscarOSPorId(id);
        os.orcamentar(); 
        return osRepositorio.save(os);
    }
    
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorClienteId(Long clienteId) {
        return osRepositorio.findByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorTecnicoId(Long tecnicoId) {
        return osRepositorio.findByTecnicoId(tecnicoId);
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorEstado(EstadoOSEnum estado) {
        return osRepositorio.findByEstado(estado);
    }
    
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorEstadoETecnicoId(EstadoOSEnum estado, Long tecnicoId) {
        return osRepositorio.findByEstadoAndTecnicoId(estado, tecnicoId);
    }
    
    @Transactional
    public OrdemServico atualizarObservacoes(UUID id, String texto) {
        OrdemServico os = buscarOSPorId(id);
        os.setObservacoesTecnicas(texto);
        return osRepositorio.save(os);
    }

}