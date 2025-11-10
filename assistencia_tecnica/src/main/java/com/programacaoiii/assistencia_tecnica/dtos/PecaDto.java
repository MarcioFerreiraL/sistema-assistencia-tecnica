package com.programacaoiii.assistencia_tecnica.dtos;

import com.programacaoiii.assistencia_tecnica.modelos.enums.TipoPeca;

public record PecaDto(String nome, TipoPeca tipoPeca) {}