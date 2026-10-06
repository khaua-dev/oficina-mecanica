DROP DATABASE IF EXISTS oficina;
CREATE DATABASE oficina CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE oficina;

CREATE TABLE cliente (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    telefone  VARCHAR(20)  NOT NULL
);

CREATE TABLE veiculo (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    placa       VARCHAR(8)  NOT NULL UNIQUE,
    modelo      VARCHAR(50) NOT NULL,
    ano         INT NULL,
    cliente_id  INT NOT NULL,
    CONSTRAINT fk_veiculo_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON DELETE RESTRICT
);

CREATE TABLE mecanico (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    nome  VARCHAR(100) NOT NULL
);

CREATE TABLE servico (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    descricao  VARCHAR(100) NOT NULL,
    preco      DECIMAL(10,2) NOT NULL
);

CREATE TABLE ordem_servico (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    veiculo_id     INT NOT NULL,
    mecanico_id    INT NOT NULL,
    data_abertura  DATE NOT NULL,
    status         ENUM('Aberta','Em andamento','Finalizada') NOT NULL DEFAULT 'Aberta',
    CONSTRAINT fk_ordem_veiculo  FOREIGN KEY (veiculo_id)  REFERENCES veiculo (id)  ON DELETE RESTRICT,
    CONSTRAINT fk_ordem_mecanico FOREIGN KEY (mecanico_id) REFERENCES mecanico (id) ON DELETE RESTRICT
);

CREATE TABLE ordem_servico_item (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    ordem_id    INT NOT NULL,
    servico_id  INT NOT NULL,
    preco       DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_ordem   FOREIGN KEY (ordem_id)   REFERENCES ordem_servico (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_servico FOREIGN KEY (servico_id) REFERENCES servico (id)       ON DELETE RESTRICT
);

INSERT INTO cliente (nome, telefone) VALUES
    ('João Pereira', '(11) 99999-0000'),
    ('Ana Lima',     '(11) 98888-1111'),
    ('Pedro Alves',  '(11) 97777-2222');

INSERT INTO veiculo (placa, modelo, ano, cliente_id) VALUES
    ('ABC-1234', 'Gol',   2015, 1),
    ('XYZ-7788', 'Uno',   2012, 1),
    ('QWE-5678', 'Onix',  2019, 2),
    ('JKL-9012', 'Palio', 2010, 3);

INSERT INTO mecanico (nome) VALUES ('Carlos Souza'), ('Rafael Dias');

INSERT INTO servico (descricao, preco) VALUES
    ('Troca de óleo',      120.00),
    ('Revisão dos freios', 250.00),
    ('Alinhamento',         90.00),
    ('Balanceamento',       80.00),
    ('Troca de pastilhas', 180.00);

INSERT INTO ordem_servico (veiculo_id, mecanico_id, data_abertura, status) VALUES
    (1, 1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Em andamento'),
    (3, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Aberta'),
    (4, 2, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'Finalizada');

INSERT INTO ordem_servico_item (ordem_id, servico_id, preco) VALUES
    (1, 1, 120.00), (1, 2, 250.00),
    (2, 3,  90.00),
    (3, 5, 180.00);
