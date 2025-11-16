package com.programacaoiii.assistencia_tecnica.modelos.entidades;

import com.programacaoiii.assistencia_tecnica.modelos.enums.TipoPecaEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_peca")
public class Peca extends ProdutoAbstrato{
	
	private String nome;
	
	@Enumerated(EnumType.STRING)
	private TipoPecaEnum tipoPeca;
	
	public Peca() {
    	super();
    }
	
	public Peca(String nome, TipoPecaEnum tipoPeca) {
		super();
		initUUID();
		this.nome = nome;
		this.tipoPeca = tipoPeca;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public TipoPecaEnum getTipoPeca() {
		return tipoPeca;
	}

	public void setTipoPeca(TipoPecaEnum tipoPeca) {
		this.tipoPeca = tipoPeca;
	}
	
	
	
}
