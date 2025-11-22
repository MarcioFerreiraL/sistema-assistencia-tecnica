package com.programacaoiii.assistencia_tecnica.dtos;

import com.programacaoiii.assistencia_tecnica.modelos.enums.TipoPecaEnum;

public record PecaDto(String nome, TipoPecaEnum tipoPeca) {}