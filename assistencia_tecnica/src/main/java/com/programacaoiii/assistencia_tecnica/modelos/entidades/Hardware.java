package com.programacaoiii.assistencia_tecnica.modelos.entidades;

import com.programacaoiii.assistencia_tecnica.modelos.enums.TipoHardwareEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_hardware")
public class Hardware extends ProdutoAbstrato{
	
	@Enumerated(EnumType.STRING)
	private TipoHardwareEnum tipoHardware;
	
	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;
	
	public Hardware() {
    	super();
    }
	
	public Hardware(TipoHardwareEnum tipoHardware, Cliente cliente) {
		super();
		this.tipoHardware = tipoHardware;
		this.cliente = cliente;
	}

	public TipoHardwareEnum getTipoHardware() {
		return tipoHardware;
	}

	public void setTipoHardware(TipoHardwareEnum tipoHardware) {
		this.tipoHardware = tipoHardware;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	
	
}
