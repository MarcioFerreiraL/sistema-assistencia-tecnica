package com.programacaoiii.assistencia_tecnica.modelos.entidades;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ProdutoAbstrato {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long numeroSerie;

	public ProdutoAbstrato() {
    }
	

	public Long getNumeroSerie() {
		return numeroSerie;
	}
	
	
}
