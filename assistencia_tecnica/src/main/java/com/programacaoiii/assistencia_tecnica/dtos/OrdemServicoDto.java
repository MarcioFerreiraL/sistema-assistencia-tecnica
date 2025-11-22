package com.programacaoiii.assistencia_tecnica.dtos;

public record OrdemServicoDto(
	    double valorOrcamento, 
	    String descricao, 
	    Long clienteId, 
	    Long hardwareId, 
	    Long tecnicoId
	) {}