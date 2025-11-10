package com.programacaoiii.assistencia_tecnica.servicos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Quando esta exceção for lançada, o Spring vai retornar um erro 404 (Not Found)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecursoNaoEncontradoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}