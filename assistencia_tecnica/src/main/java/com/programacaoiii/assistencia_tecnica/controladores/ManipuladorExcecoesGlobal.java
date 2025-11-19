package com.programacaoiii.assistencia_tecnica.controladores;

import com.programacaoiii.assistencia_tecnica.servicos.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ManipuladorExcecoesGlobal {

	// erro 404
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<String> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

     //Captura erros de regras de negócio (ex: CPF duplicado).
     //Retorna um erro 400 (Bad Request).
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> tratarRegraDeNegocio(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    
    // Captura qualquer outro erro inesperado.
     
    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<String> tratarErroGenerico(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Ocorreu um erro interno inesperado no servidor.");
    }*/
}