package com.programacaoiii.assistencia_tecnica.servicos.configuracoes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/assistencia_tecnica";
    private static final String USER = "postgres";
    private static final String PASS = "5002";

    private static volatile DatabaseConnector instance;

    private Connection connection;


    private DatabaseConnector() {
        try {
            Class.forName("org.postgresql.Driver");
            
            this.connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("LOG: Conexão com o Banco de Dados estabelecida.");
            
        } catch (SQLException e) {
            System.err.println("LOG: Falha ao conectar ao banco de dados.");
            throw new RuntimeException("Erro na conexão com o banco de dados", e);
        } catch (ClassNotFoundException e) {
            System.err.println("LOG: Driver JDBC do PostgreSQL não encontrado.");
            throw new RuntimeException("Driver JDBC não encontrado", e);
        }
    }


    public static DatabaseConnector getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnector.class) {
                if (instance == null) {
                    instance = new DatabaseConnector();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
         try {
            if (this.connection == null || this.connection.isClosed()) {
                 System.err.println("LOG: Conexão estava fechada, recriando...");
                 this.connection = DriverManager.getConnection(DB_URL, USER, PASS);
            }
        } catch (SQLException e) {
             throw new RuntimeException("Erro ao verificar ou recriar conexão", e);
        }
        return this.connection;
    }
}