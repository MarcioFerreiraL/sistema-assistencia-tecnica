# Sistema de Assistência Técnica

Este projeto é um sistema de gerenciamento de Ordens de Serviço (OS) para uma assistência técnica de equipamentos eletrônicos. Ele foi desenvolvido em Java utilizando o framework Spring Boot e segue uma arquitetura modular que separa responsabilidades entre diferentes atores (Atendente, Administrador, Técnico e Cliente).

O objetivo é gerenciar o fluxo completo de reparo, desde a abertura do chamado até a sua finalização, utilizando padrões de projeto para garantir flexibilidade e manutenibilidade.

## 🏛️ Arquitetura e Padrões de Projeto

A arquitetura foi construída sobre três padrões de projeto fundamentais: **State**, **Template Method** e **Singleton**.

### 1. Padrão State: Gerenciando o Fluxo da OS (Kanban)

* **Problema:** Uma `OrdemServico` passa por diversos estágios (Aberta, Aguardando Aprovação, Em Reparo, Finalizada, etc.). O comportamento esperado da OS (quais ações são permitidas) muda drasticamente em cada estágio. Implementar essa lógica com condicionais (if/else) dentro da classe `OrdemServico` a tornaria complexa e difícil de manter.
* **Solução:** O padrão State foi aplicado para encapsular o comportamento de cada estágio em sua própria classe.
    * **`StateInterface`**: Define um contrato comum para todos os estados (ex: `aprovar()`, `finalizar()`).
    * **Classes de Estado Concretas** (`EstadoAberta`, `EstadoAguardandoAprovacao`, etc.): Cada classe implementa a lógica específica para aquele estado. Por exemplo, `EstadoAguardandoAprovacao` permite a transição para `EM_REPARO` (ao aprovar) ou `CANCELADA` (ao cancelar).
    * **`OrdemServico` (Contexto):** A classe `OrdemServico` mantém uma referência ao seu objeto de estado atual (`comportamentoEstado`) e delega as chamadas de ação (como `aprovar()`) para esse objeto.

### 2. Padrão Template Method: Padronizando o Processo de Reparo

* **Problema:** O processo de reparo de diferentes tipos de hardware (Notebook, Celular) segue um fluxo de passos semelhante (diagnóstico, separação de materiais, reparo, testes, limpeza), mas a execução de cada passo é diferente.
* **Solução:** O padrão Template Method define o "esqueleto" de um algoritmo em uma superclasse (`TemplateMethod`), permitindo que subclasses redefinam certos passos sem alterar a estrutura do algoritmo.
    * **`TemplateMethod` (Classe Abstrata):** Contém o método final `executarProcessoReparo()` que chama a sequência de passos (ex: `diagnosticarProblema()`, `realizarReparo()`, `realizarTestes()`).
    * **Classes Concretas** (`ReparoNotebook`, `ReparoCelular`): Herdam de `TemplateMethod` e implementam apenas os passos abstratos, fornecendo os detalhes específicos para cada tipo de hardware.
    * **`Tecnico` (Executor):** A classe `Tecnico`, ao `executarOS()`, seleciona a implementação concreta do Template Method apropriada com base no `TipoHardware` da OS.

### 3. Padrão Singleton: Conexão com o Banco de Dados

* **Problema:** Garantir um ponto de acesso central e único para o banco de dados, evitando a criação de múltiplas conexões desnecessárias.
* **Solução:** O padrão Singleton garante que a classe `DatabaseConnector` tenha apenas uma instância e fornece um ponto de acesso global a ela (`getInstance()`). A classe possui um construtor privado para impedir a instanciação direta.

*(Nota: Em uma aplicação Spring Boot, o próprio framework gerencia o ciclo de vida dos beans, como os Repositórios e o DataSource, tipicamente como Singletons, mas este padrão foi incluído na documentação do projeto.)*

## 🛠️ Tecnologias Utilizadas

O projeto foi construído com as seguintes tecnologias:

* **Java 21**
* **Spring Boot 3.5.7**
* **Spring Data JPA** (para persistência de dados)
* **Spring Web** (para criação de APIs REST)
* **PostgreSQL** (Banco de dados relacional)
* **Maven** (Gerenciador de dependências)

## 🚀 Como Executar o Projeto

### Pré-requisitos

1.  **Java JDK 21**.
2.  **Maven** 3.x ou superior.
3.  **PostgreSQL** (preferencialmente a versão 14 ou superior) instalado e rodando.

### Configuração do Banco de Dados

1.  Acesse seu terminal PostgreSQL (ex: `psql`) e crie o banco de dados:
    ```sql
    CREATE DATABASE assistencia_tecnica;
    ```
2.  Abra o arquivo `src/main/resources/application.properties`.
3.  Altere as propriedades `spring.datasource.username` e `spring.datasource.password` para corresponder às suas credenciais do PostgreSQL.

    ```properties
    spring.application.name=Assistencia Tecnica
    
    spring.datasource.url=jdbc:postgresql://localhost:5432/assistencia_tecnica
    spring.datasource.username=postgres
    spring.datasource.password=5002
    spring.jpa.hibernate.ddl-auto=update
    
    spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
    spring.jpa.show-sql=true
    ```

### Execução

1.  Clone este repositório:
    ```bash
    git clone <url-do-repositorio>
    cd assistencia_tecnica
    ```
2.  Use o Maven para compilar e executar a aplicação:
    ```bash
    mvn spring-boot:run
    ```
    *Alternativamente, você pode usar o Maven Wrapper incluído:*
    ```bash
    # No Linux/macOS
    ./mvnw spring-boot:run
    
    # No Windows
    ./mvnw.cmd spring-boot:run
    ```

3.  A aplicação estará disponível em `http://localhost:8080`.

## 📦 Estrutura do Projeto

Aqui está o README.md completo, formatado como um bloco de código Markdown:
Markdown

# Sistema de Assistência Técnica

Este projeto é um sistema de gerenciamento de Ordens de Serviço (OS) para uma assistência técnica de equipamentos eletrônicos. Ele foi desenvolvido em Java utilizando o framework Spring Boot e segue uma arquitetura modular que separa responsabilidades entre diferentes atores (Atendente, Administrador, Técnico e Cliente).

O objetivo é gerenciar o fluxo completo de reparo, desde a abertura do chamado até a sua finalização, utilizando padrões de projeto para garantir flexibilidade e manutenibilidade.

## 🏛️ Arquitetura e Padrões de Projeto

A arquitetura foi construída sobre três padrões de projeto fundamentais: **State**, **Template Method** e **Singleton**.

### 1. Padrão State: Gerenciando o Fluxo da OS (Kanban)

* **Problema:** Uma `OrdemServico` passa por diversos estágios (Aberta, Aguardando Aprovação, Em Reparo, Finalizada, etc.). O comportamento esperado da OS (quais ações são permitidas) muda drasticamente em cada estágio. Implementar essa lógica com condicionais (if/else) dentro da classe `OrdemServico` a tornaria complexa e difícil de manter.
* **Solução:** O padrão State foi aplicado para encapsular o comportamento de cada estágio em sua própria classe.
    * **`StateInterface`**: Define um contrato comum para todos os estados (ex: `aprovar()`, `finalizar()`).
    * **Classes de Estado Concretas** (`EstadoAberta`, `EstadoAguardandoAprovacao`, etc.): Cada classe implementa a lógica específica para aquele estado. Por exemplo, `EstadoAguardandoAprovacao` permite a transição para `EM_REPARO` (ao aprovar) ou `CANCELADA` (ao cancelar).
    * **`OrdemServico` (Contexto):** A classe `OrdemServico` mantém uma referência ao seu objeto de estado atual (`comportamentoEstado`) e delega as chamadas de ação (como `aprovar()`) para esse objeto.

### 2. Padrão Template Method: Padronizando o Processo de Reparo

* **Problema:** O processo de reparo de diferentes tipos de hardware (Notebook, Celular) segue um fluxo de passos semelhante (diagnóstico, separação de materiais, reparo, testes, limpeza), mas a execução de cada passo é diferente.
* **Solução:** O padrão Template Method define o "esqueleto" de um algoritmo em uma superclasse (`TemplateMethod`), permitindo que subclasses redefinam certos passos sem alterar a estrutura do algoritmo.
    * **`TemplateMethod` (Classe Abstrata):** Contém o método final `executarProcessoReparo()` que chama a sequência de passos (ex: `diagnosticarProblema()`, `realizarReparo()`, `realizarTestes()`).
    * **Classes Concretas** (`ReparoNotebook`, `ReparoCelular`): Herdam de `TemplateMethod` e implementam apenas os passos abstratos, fornecendo os detalhes específicos para cada tipo de hardware.
    * **`Tecnico` (Executor):** A classe `Tecnico`, ao `executarOS()`, seleciona a implementação concreta do Template Method apropriada com base no `TipoHardware` da OS.

### 3. Padrão Singleton: Conexão com o Banco de Dados

* **Problema:** Garantir um ponto de acesso central e único para o banco de dados, evitando a criação de múltiplas conexões desnecessárias.
* **Solução:** O padrão Singleton garante que a classe `DatabaseConnector` tenha apenas uma instância e fornece um ponto de acesso global a ela (`getInstance()`). A classe possui um construtor privado para impedir a instanciação direta.

*(Nota: Em uma aplicação Spring Boot, o próprio framework gerencia o ciclo de vida dos beans, como os Repositórios e o DataSource, tipicamente como Singletons, mas este padrão foi incluído na documentação do projeto.)*

## 🛠️ Tecnologias Utilizadas

O projeto foi construído com as seguintes tecnologias:

* **Java 21**
* **Spring Boot 3.5.7**
* **Spring Data JPA** (para persistência de dados)
* **Spring Web** (para criação de APIs REST)
* **PostgreSQL** (Banco de dados relacional)
* **Maven** (Gerenciador de dependências)

## 🚀 Como Executar o Projeto

### Pré-requisitos

1.  **Java JDK 21**.
2.  **Maven** 3.x ou superior.
3.  **PostgreSQL** (preferencialmente a versão 14 ou superior) instalado e rodando.

### Configuração do Banco de Dados

1.  Acesse seu terminal PostgreSQL (ex: `psql`) e crie o banco de dados:
    ```sql
    CREATE DATABASE assistencia_tecnica;
    ```
2.  Abra o arquivo `src/main/resources/application.properties`.
3.  Altere as propriedades `spring.datasource.username` e `spring.datasource.password` para corresponder às suas credenciais do PostgreSQL.

    ```properties
    spring.application.name=Assistencia Tecnica
    
    spring.datasource.url=jdbc:postgresql://localhost:5432/assistencia_tecnica
    spring.datasource.username=postgres
    spring.datasource.password=password
    spring.jpa.hibernate.ddl-auto=update
    
    spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
    spring.jpa.show-sql=true
    ```

### Execução

1.  Clone este repositório:
    ```bash
    git clone <url-do-repositorio>
    cd assistencia_tecnica
    ```
2.  Use o Maven para compilar e executar a aplicação:
    ```bash
    mvn spring-boot:run
    ```
    *Alternativamente, você pode usar o Maven Wrapper incluído:*
    ```bash
    # No Linux/macOS
    ./mvnw spring-boot:run
    
    # No Windows
    ./mvnw.cmd spring-boot:run
    ```

3.  A aplicação estará disponível em `http://localhost:8080`.

## 📦 Estrutura do Projeto

assistencia_tecnica/ │ ├── src/ │ ├── main/ │ │ ├── java/com/programacaoiii/assistencia_tecnica/ │ │ │ ├── controladores/ # (Não implementado, mas planejado) │ │ │ ├── dtos/ # Data Transfer Objects (Records) │ │ │ ├── modelos/ │ │ │ │ ├── entidades/ # Classes de entidade JPA (Ex: OrdemServico, Cliente) │ │ │ │ └── enums/ # Enumerações (Ex: EstadoOS, TipoHardware) │ │ │ ├── repositorios/ # Interfaces Spring Data JPA │ │ │ └── servicos/ # Lógica de negócio (Services) │ │ │ ├── padroes/ │ │ │ │ ├── state/ # Implementação do Padrão State │ │ │ │ └── template/ # Implementação do Padrão Template Method │ │ │ └── configuracoes/ # Classes de configuração (Ex: DatabaseConnector) │ │ │ │ │ └── resources/ │ │ └── application.properties # Configurações do Spring │ │ │ └── test/ # Testes unitários │ ├── documents/ # Diagramas e documentação │ ├── diagrama_banco_de_dados.png │ ├── diagrama_casos_de_uso_v3.puml │ ├── diagrama_de_classes_v3.puml │ └── documentação.docx │ └── pom.xml # Arquivo Maven

## Schema do Banco de Dados

O diagrama abaixo ilustra a estrutura do banco de dados relacional utilizado pelo sistema.

![Diagrama do Banco de Dados](assistencia_tecnica/documents/diagrama_banco_de_dados_v2.png)