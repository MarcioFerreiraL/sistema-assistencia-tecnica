package com.programacaoiii.assistencia_tecnica.servicos.padroes.state;

// Estado final, nenhuma transição é permitida.
public class EstadoFinalizada extends EstadoOSAbstrato {
    
    public EstadoFinalizada() {
        System.out.println("OS Finalizada. Pronta para retirada.");
    }
    
    // Nenhuma transição é válida a partir daqui.
}