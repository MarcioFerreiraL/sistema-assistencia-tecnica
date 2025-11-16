package com.programacaoiii.assistencia_tecnica.controladores;

import com.programacaoiii.assistencia_tecnica.dtos.PessoaDto;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Administrador;
import com.programacaoiii.assistencia_tecnica.servicos.AdministradorServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/administrador")
@CrossOrigin(origins = "*")
public class AdministradorControlador {

    private final AdministradorServico administradorServico;

    public AdministradorControlador(AdministradorServico administradorServico) {
        this.administradorServico = administradorServico;
    }

    @GetMapping
    public ResponseEntity<List<Administrador>> buscarTodos() {
        return ResponseEntity.ok(administradorServico.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(administradorServico.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Administrador> salvar(@RequestBody PessoaDto dto) {
        Administrador novoAdministrador = administradorServico.salvar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoAdministrador.getId()).toUri();
        return ResponseEntity.created(uri).body(novoAdministrador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrador> atualizar(@PathVariable UUID id, @RequestBody PessoaDto dto) {
        return ResponseEntity.ok(administradorServico.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        administradorServico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}