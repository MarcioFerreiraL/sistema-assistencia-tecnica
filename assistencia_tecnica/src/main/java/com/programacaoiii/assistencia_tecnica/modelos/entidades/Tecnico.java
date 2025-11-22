package com.programacaoiii.assistencia_tecnica.modelos.entidades;

import java.time.LocalDate;
import com.programacaoiii.assistencia_tecnica.modelos.enums.TipoHardwareEnum;
import com.programacaoiii.assistencia_tecnica.servicos.padroes.template.ReparoCelular;
import com.programacaoiii.assistencia_tecnica.servicos.padroes.template.ReparoComputador;
import com.programacaoiii.assistencia_tecnica.servicos.padroes.template.ReparoNotebook;
import com.programacaoiii.assistencia_tecnica.servicos.padroes.template.ReparoOutros;
import com.programacaoiii.assistencia_tecnica.servicos.padroes.template.TemplateMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_tecnico")
public class Tecnico extends PessoaAbstrato{
	
	public Tecnico() {
		
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
        
        TemplateMethod reparo;
        TipoHardwareEnum tipo = os.getHardware().getTipoHardware();

        if (tipo == TipoHardwareEnum.NOTEBOOK) {
            reparo = new ReparoNotebook();
        } else if (tipo == TipoHardwareEnum.CELULAR) {
            reparo = new ReparoCelular();
        } else if (tipo == TipoHardwareEnum.COMPUTADOR){
            reparo = new ReparoComputador();
        } else {
        	reparo = new ReparoOutros();
        }

        reparo.executarProcessoReparo();
        
        System.out.println("Técnico finalizou a EXECUÇÃO do reparo na OS #" + os.getId());
	}
	
	public void finalizarOS(OrdemServico os) {
		os.finalizar();
	}
}