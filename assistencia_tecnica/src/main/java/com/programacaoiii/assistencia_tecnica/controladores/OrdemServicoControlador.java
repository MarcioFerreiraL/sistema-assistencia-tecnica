package com.programacaoiii.assistencia_tecnica.controladores;

import com.programacaoiii.assistencia_tecnica.dtos.OrdemServicoDto;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.OrdemServico;
import com.programacaoiii.assistencia_tecnica.modelos.enums.EstadoOSEnum;
import com.programacaoiii.assistencia_tecnica.servicos.OrdemServicoServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/os")
@CrossOrigin(origins = "*")
public class OrdemServicoControlador {

    private final OrdemServicoServico osServico;

    public OrdemServicoControlador(OrdemServicoServico osServico) {
        this.osServico = osServico;
    }

    @GetMapping
    public ResponseEntity<List<OrdemServico>> buscarTodasOS() {
        return ResponseEntity.ok(osServico.buscarTodasOS());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServico> buscarOSPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(osServico.buscarOSPorId(id));
    }

    @PostMapping
    public ResponseEntity<OrdemServico> salvarOS(@RequestBody OrdemServicoDto dto) {
        OrdemServico novaOS = osServico.salvarOS(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaOS.getId()).toUri();
        return ResponseEntity.created(uri).body(novaOS);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServico> atualizarOS(@PathVariable UUID id, @RequestBody OrdemServicoDto dto) {
        return ResponseEntity.ok(osServico.atualizarOS(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirOS(@PathVariable UUID id) {
        osServico.excluirOS(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/orcamentar")
    public ResponseEntity<OrdemServico> orcamentarOS(@PathVariable UUID id) {
        return ResponseEntity.ok(osServico.orcamentarOS(id));
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<OrdemServico> aprovarOrcamento(@PathVariable UUID id) {
        return ResponseEntity.ok(osServico.aprovarOrcamento(id));
    }

    @PostMapping("/{id}/executar")
    public ResponseEntity<OrdemServico> executarOS(@PathVariable UUID id) {
        return ResponseEntity.ok(osServico.executarOS(id));
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServico> finalizarOS(@PathVariable UUID id) {
        return ResponseEntity.ok(osServico.finalizarOS(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<OrdemServico> cancelarOS(@PathVariable UUID id) {
        return ResponseEntity.ok(osServico.cancelarOS(id));
    }
    
    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<OrdemServico>> buscarPorCliente(@PathVariable Long id) {
        return ResponseEntity.ok(osServico.buscarPorClienteId(id));
    }

    @GetMapping("/tecnico/{id}")
    public ResponseEntity<List<OrdemServico>> buscarPorTecnico(@PathVariable Long id) {
        return ResponseEntity.ok(osServico.buscarPorTecnicoId(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<OrdemServico>> buscarPorEstado(@PathVariable EstadoOSEnum estado) {
        return ResponseEntity.ok(osServico.buscarPorEstado(estado));
    }

    @GetMapping("/estado/{estado}/tecnico/{id}")
    public ResponseEntity<List<OrdemServico>> buscarPorEstadoETecnico(
            @PathVariable EstadoOSEnum estado, @PathVariable Long id) {
        return ResponseEntity.ok(osServico.buscarPorEstadoETecnicoId(estado, id));
    }
    
    @PatchMapping("/{id}/observacoes")
    public ResponseEntity<OrdemServico> salvarObservacoes(@PathVariable UUID id, @RequestBody String texto) {
        return ResponseEntity.ok(osServico.atualizarObservacoes(id, texto));
    }
}