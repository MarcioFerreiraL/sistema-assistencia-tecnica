package com.programacaoiii.assistencia_tecnica.modelos.entidades;

import java.util.UUID;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ProdutoAbstrato {
	@Id
	private UUID numeroSerie;

	public ProdutoAbstrato() {
    }
	
	public void initUUID() {
		this.numeroSerie = UUID.randomUUID();
	}

	public UUID getNumeroSerie() {
		return numeroSerie;
	}
	
	
}
