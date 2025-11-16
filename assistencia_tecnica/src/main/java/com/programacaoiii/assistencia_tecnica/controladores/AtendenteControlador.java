package com.programacaoiii.assistencia_tecnica.controladores;

import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Atendente;
import com.programacaoiii.assistencia_tecnica.servicos.AtendenteServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/atendente")
@CrossOrigin(origins = "*")
public class AtendenteControlador {

    private final AtendenteServico atendenteControlador;

    public AtendenteControlador(AtendenteServico atendenteControlador) {
        this.atendenteControlador = atendenteControlador;
    }

    @GetMapping
    public ResponseEntity<List<Atendente>> buscarTodos() {
        return ResponseEntity.ok(atendenteControlador.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atendente> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(atendenteControlador.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Atendente> salvar(@RequestBody PessoaDto dto) {
        Atendente novoAtendente = atendenteControlador.salvar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoAtendente.getId()).toUri();
        return ResponseEntity.created(uri).body(novoAtendente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atendente> atualizar(@PathVariable UUID id, @RequestBody PessoaDto dto) {
        return ResponseEntity.ok(atendenteControlador.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        atendenteControlador.excluir(id);
        return ResponseEntity.noContent().build();
    }
}