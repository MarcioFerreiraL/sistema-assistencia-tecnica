package com.programacaoiii.assistencia_tecnica.controladores;

import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Atendente;
import com.programacaoiii.assistencia_tecnica.servicos.AtendenteServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/atendente")
@CrossOrigin(origins = "*")
public class AtendenteControlador {

    private final AtendenteServico atendenteServico;

    public AtendenteControlador(AtendenteServico atendenteControlador) {
        this.atendenteServico = atendenteControlador;
    }

    @GetMapping
    public ResponseEntity<List<Atendente>> buscarTodos() {
        return ResponseEntity.ok(atendenteServico.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atendente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(atendenteServico.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Atendente> salvar(@RequestBody PessoaDto dto) {
        Atendente novoAtendente = atendenteServico.salvar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoAtendente.getId()).toUri();
        return ResponseEntity.created(uri).body(novoAtendente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atendente> atualizar(@PathVariable Long id, @RequestBody PessoaDto dto) {
        return ResponseEntity.ok(atendenteServico.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        atendenteServico.excluir(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Atendente> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(atendenteServico.buscarPorCpf(cpf));
    }
}