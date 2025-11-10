package com.programacaoiii.assistencia_tecnica.servicos.configuracoes;

import java.sql.Connection;

public class DatabaseConnector {

	private static DatabaseConnector instance;
	private Connection connection;
	
	private DatabaseConnector() {
		
	}

	public static DatabaseConnector getInstance() {
		return instance;
	}
	
}
