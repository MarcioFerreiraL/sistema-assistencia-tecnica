package com.programacaoiii.assistencia_tecnica.controladores;

import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Tecnico;
import com.programacaoiii.assistencia_tecnica.servicos.TecnicoServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tecnicos")
@CrossOrigin(origins = "*")
public class TecnicoControlador {

    private final TecnicoServico tecnicoServico;

    public TecnicoControlador(TecnicoServico tecnicoServico) {
        this.tecnicoServico = tecnicoServico;
    }

    @GetMapping
    public ResponseEntity<List<Tecnico>> buscarTodos() {
        return ResponseEntity.ok(tecnicoServico.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tecnico> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(tecnicoServico.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Tecnico> salvar(@RequestBody PessoaDto dto) {
        Tecnico novoTecnico = tecnicoServico.salvar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoTecnico.getId()).toUri();
        return ResponseEntity.created(uri).body(novoTecnico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tecnico> atualizar(@PathVariable UUID id, @RequestBody PessoaDto dto) {
        return ResponseEntity.ok(tecnicoServico.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        tecnicoServico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}