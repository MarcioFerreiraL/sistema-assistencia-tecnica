package com.programacaoiii.assistencia_tecnica.controladores;

import com.programacaoiii.assistencia_tecnica.dtos.PecaDto;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Peca;
import com.programacaoiii.assistencia_tecnica.servicos.PecaServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pecas")
@CrossOrigin(origins = "*")
public class PecaControlador {

    private final PecaServico pecaServico;

    public PecaControlador(PecaServico pecaServico) {
        this.pecaServico = pecaServico;
    }

    @GetMapping
    public ResponseEntity<List<Peca>> buscarTodasPecas() {
        return ResponseEntity.ok(pecaServico.buscarTodasPecas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Peca> buscarPecaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pecaServico.buscarPecaPorId(id));
    }

    @PostMapping
    public ResponseEntity<Peca> salvarPeca(@RequestBody PecaDto dto) {
        Peca novaPeca = pecaServico.salvarPeca(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaPeca.getNumeroSerie()).toUri();
        return ResponseEntity.created(uri).body(novaPeca);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Peca> atualizarPeca(@PathVariable Long id, @RequestBody PecaDto dto) {
        return ResponseEntity.ok(pecaServico.atualizarPeca(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPeca(@PathVariable Long id) {
        pecaServico.excluirPeca(id);
        return ResponseEntity.noContent().build();
    }
}