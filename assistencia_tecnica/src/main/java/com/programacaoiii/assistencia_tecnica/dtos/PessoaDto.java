package com.programacaoiii.assistencia_tecnica.dtos;

import java.time.LocalDate;

public record PessoaDto(String nome, String cpf, LocalDate dataNascimento, String endereco) {}
