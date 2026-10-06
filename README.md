# Oficina Mecânica – Sistema de Gestão de Ordens de Serviço

## Status do projeto
🚧 Em desenvolvimento

## Tecnologias
- **Java** (JDK) – linguagem do sistema
- **Java Swing** – interface gráfica desktop
- **NetBeans** – ambiente de desenvolvimento (IDE)
- **JDBC** (MySQL Connector/J) – conexão do Java com o banco de dados
- **MySQL** e **MySQL Workbench** – banco de dados
- **Git** e **GitHub** – versionamento e repositório remoto

## Time de desenvolvedores
- Khauã Correia

## Objetivo do software
Ajudar uma oficina mecânica a organizar o atendimento do dia a dia: cadastrar clientes e seus veículos, abrir ordens de serviço e acompanhar o andamento de cada uma, substituindo anotações em papel por um sistema com os dados guardados em banco de dados.

## Funcionalidades (requisitos)
- Cadastrar, consultar, alterar e excluir clientes
- Buscar clientes por nome ou placa do veículo
- Cadastrar e remover veículos de um cliente (um cliente pode ter vários veículos)
- Garantir que a placa não se repita
- Abrir ordem de serviço para um veículo, com um mecânico e um ou mais serviços
- Calcular automaticamente o total da ordem (soma dos serviços)
- Acompanhar o status da ordem: Aberta → Em andamento → Finalizada
- Impedir alterações nos serviços de uma ordem finalizada
- Consultar ordens filtrando por status, cliente e placa
- Impedir a exclusão de clientes e veículos que possuem ordens de serviço
- Painel inicial com a quantidade de ordens por status e as últimas ordens abertas

## Como executar
1. No MySQL Workbench, execute o script `oficina.sql` (cria o banco `oficina` com dados de teste).
2. Adicione o driver **MySQL Connector/J** (`.jar`) às *Libraries* do projeto no NetBeans.
3. Em `ConexaoBD.java`, ajuste `USUARIO` e `SENHA` para os do seu MySQL.
4. Execute a classe `Main`.
