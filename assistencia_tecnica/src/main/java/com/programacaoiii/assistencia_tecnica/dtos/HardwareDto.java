package com.programacaoiii.assistencia_tecnica.dtos;

import java.util.UUID;
import com.programacaoiii.assistencia_tecnica.modelos.enums.TipoHardwareEnum;

public record HardwareDto(TipoHardwareEnum tipoHardware, UUID clienteId) {}