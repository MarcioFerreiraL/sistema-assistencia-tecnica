package com.programacaoiii.assistencia_tecnica.modelos.entidades;

import java.time.LocalDate;

import com.programacaoiii.assistencia_tecnica.servicos.Relatorio;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_administrador")
public class Administrador extends PessoaAbstrato implements Relatorio{
	
	public Administrador() {
		super();
	}

	public Administrador(String nome, String cpf, LocalDate dataNascimento, String endereco) {
		super(nome, cpf, dataNascimento, endereco);
	}
	
	public void abrirOS(Cliente cliente, Hardware hardware) {
		
	}
	
	public void atualizarOS(OrdemServico os, Cliente cliente, Hardware hardware) {
		
	}
	
	public void excluirOS(OrdemServico os) {
		
	}

	@Override
	public void verRelatorioFinanceiro() {
		
	}

	@Override
	public void verRelatorioEstoque() {
		
	}

	@Override
	public void verRelatorioOS() {		
		
	}

}
