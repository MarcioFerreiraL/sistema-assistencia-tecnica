package com.programacaoiii.assistencia_tecnica.dtos;

import java.util.UUID;
import com.programacaoiii.assistencia_tecnica.modelos.enums.TipoHardware;

public record HardwareDto(TipoHardware tipoHardware, UUID clienteId) {}