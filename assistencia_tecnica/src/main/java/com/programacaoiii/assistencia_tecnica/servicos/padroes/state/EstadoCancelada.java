package com.programacaoiii.assistencia_tecnica.servicos.padroes.state;

// Estado final, nenhuma transição é permitida.
public class EstadoCancelada extends EstadoOSAbstrato {

    public EstadoCancelada() {
        System.out.println("OS foi Cancelada.");
    }
    
    // Nenhuma transição é válida a partir daqui.
}