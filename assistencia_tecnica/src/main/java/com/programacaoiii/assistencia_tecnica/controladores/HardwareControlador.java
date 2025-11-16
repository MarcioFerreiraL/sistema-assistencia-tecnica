package com.programacaoiii.assistencia_tecnica.controladores;

import com.programacaoiii.assistencia_tecnica.dtos.HardwareDto;
import com.programacaoiii.assistencia_tecnica.modelos.entidades.Hardware;
import com.programacaoiii.assistencia_tecnica.servicos.HardwareServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hardware")
@CrossOrigin(origins = "*")
public class HardwareControlador {

    private final HardwareServico hardwareServico;

    public HardwareControlador(HardwareServico hardwareServico) {
        this.hardwareServico = hardwareServico;
    }

    @GetMapping
    public ResponseEntity<List<Hardware>> buscarTodosHardwares() {
        return ResponseEntity.ok(hardwareServico.buscarTodosHardwares());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hardware> buscarHardwarePorId(@PathVariable UUID id) {
        return ResponseEntity.ok(hardwareServico.buscarHardwarePorId(id));
    }

    @PostMapping
    public ResponseEntity<Hardware> salvarHardware(@RequestBody HardwareDto dto) {
        Hardware novoHardware = hardwareServico.salvarHardware(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoHardware.getNumeroSerie()).toUri();
        return ResponseEntity.created(uri).body(novoHardware);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hardware> atualizarHardware(@PathVariable UUID id, @RequestBody HardwareDto dto) {
        return ResponseEntity.ok(hardwareServico.atualizarHardware(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirHardware(@PathVariable UUID id) {
        hardwareServico.excluirHardware(id);
        return ResponseEntity.noContent().build();
    }
}