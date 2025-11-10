package com.programacaoiii.assistencia_tecnica.dtos;

import java.util.UUID;

public record OrdemServicoDto(
	    double valorOrcamento, 
	    String descricao, 
	    UUID clienteId, 
	    UUID hardwareId, 
	    UUID tecnicoId
	) {}