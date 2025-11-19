package com.programacaoiii.assistencia_tecnica.dtos;

import com.programacaoiii.assistencia_tecnica.modelos.enums.TipoHardwareEnum;

public record HardwareDto(TipoHardwareEnum tipoHardware, Long clienteId) {}