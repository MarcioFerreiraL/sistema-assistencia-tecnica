package com.programacaoiii.assistencia_tecnica.modelos.entidades;

import java.time.LocalDate;

import com.programacaoiii.assistencia_tecnica.modelos.enums.TipoHardware;
import com.programacaoiii.assistencia_tecnica.servicos.padroes.template.ReparoCelular;
import com.programacaoiii.assistencia_tecnica.servicos.padroes.template.ReparoNotebook;
import com.programacaoiii.assistencia_tecnica.servicos.padroes.template.TemplateMethod;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
@Entity
@Table(name = "tb_tecnico")
public class Tecnico extends PessoaAbstrato{

	public Tecnico() {
		super();
	}
	
	public Tecnico(String nome, String cpf, LocalDate dataNascimento, String endereco) {
		super(nome, cpf, dataNascimento, endereco);
	}
	
	public void fazerOrcamento(OrdemServico os, float valor) {
		// Lógica para adicionar orçamento
	}
	
	public void assumirOS(OrdemServico os) {
		// Lógica para se atribuir à OS
	}
	
	public void executarOS(OrdemServico os) {
		System.out.println("Técnico iniciando execução da OS #" + os.getId());
        
        // 1. Seleciona o Template Method apropriado
        TemplateMethod reparo;
        TipoHardware tipo = os.getHardware().getTipoHardware();

        if (tipo == TipoHardware.NOTEBOOK) {
            reparo = new ReparoNotebook();
        } else if (tipo == TipoHardware.CELULAR) {
            reparo = new ReparoCelular();
        } else {
            // Um template padrão para COMPUTADOR ou OUTROS
            // (Você pode criar o ReparoComputador.java da mesma forma)
            System.out.println("Usando processo de reparo padrão.");
            reparo = new ReparoNotebook(); // Usando notebook como padrão por enquanto
        }

        // 2. Executa o processo
        reparo.executarProcessoReparo();
        
        // 3. Altera o estado da OS
        os.finalizar(); // Tenta finalizar a OS
        
        System.out.println("Técnico finalizou a OS #" + os.getId());
	}
	
	public void finalizarOS(OrdemServico os) {
		os.finalizar();
	}
}