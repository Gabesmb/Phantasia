DROP DATABASE IF EXISTS Phantasia;
CREATE DATABASE IF NOT EXISTS Phantasia;
USE Phantasia;

CREATE TABLE IF NOT EXISTS Cliente (
    id_cliente INT AUTO_INCREMENT,
    username_cliente VARCHAR(255) NOT NULL,
    senha_cliente VARCHAR(255) NOT NULL,
    nome_cliente VARCHAR(255),
    e_mail VARCHAR(255),
    DDD_telefone VARCHAR(2),
    numero_telefone VARCHAR(9),
    UNIQUE (username_cliente),
    PRIMARY KEY (id_cliente)
);

CREATE TABLE IF NOT EXISTS DBAdmin (
    id_admin INT AUTO_INCREMENT,
    username_admin VARCHAR(255) NOT NULL,
    senha_admin VARCHAR(255) NOT NULL,
    nome_admin VARCHAR(255),
    e_mail VARCHAR(255),
    DDD_telefone VARCHAR(2),
    numero_telefone VARCHAR(9),
    allow_insert BOOLEAN NOT NULL,
    allow_update BOOLEAN NOT NULL,
    allow_delete BOOLEAN NOT NULL,
    UNIQUE (username_admin),
    PRIMARY KEY (id_admin)
);

CREATE TABLE IF NOT EXISTS Fantasia (
    id_fantasia INT AUTO_INCREMENT,
    nome_fantasia VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL,
    tamanho ENUM('PP', 'P', 'M', 'G', 'GG', 'XG') NOT NULL,
    valor_aluguel_dia DECIMAL(8,2) NOT NULL,
    id_admin INT NOT NULL,
    data_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_fantasia),
    FOREIGN KEY (id_admin) REFERENCES DBAdmin(id_admin)
);

CREATE TABLE IF NOT EXISTS Categoria (
    id_categoria INT AUTO_INCREMENT,
    nome_categoria VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_categoria)
);

CREATE TABLE IF NOT EXISTS Fantasia_Categoria (
    id_fantasia INT NOT NULL,
    id_categoria INT NOT NULL,
    CONSTRAINT PK_Fantasia_Categoria PRIMARY KEY (id_fantasia, id_categoria),
    FOREIGN KEY (id_fantasia) REFERENCES Fantasia(id_fantasia),
    FOREIGN KEY (id_categoria) REFERENCES Categoria(id_categoria)
);

CREATE TABLE IF NOT EXISTS Aluguel (
    id_aluguel INT AUTO_INCREMENT,
    id_fantasia INT NOT NULL,
    id_cliente INT NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_inicio DATE NOT NULL DEFAULT (CURRENT_DATE),
    data_fim DATE NOT NULL,
    valor_total DECIMAL(8,2),
    status_aluguel ENUM ("ativo", "inativo", "devolvido", "devolvido com atraso") NOT NULL DEFAULT "inativo",
    PRIMARY KEY (id_aluguel),
    FOREIGN KEY (id_fantasia) REFERENCES Fantasia(id_fantasia),
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente)
);

# DROP TRIGGER ValidateRentInsertTimestamps_onInsert;
DELIMITER §

CREATE TRIGGER ValidateRentInsertTimestamps_onInsert BEFORE INSERT ON Aluguel
FOR EACH ROW
BEGIN
	DECLARE msg TEXT;
    DECLARE total DECIMAL(8,2);
    IF (new.data_fim < new.data_inicio) THEN
		SET @msg = "Erro de inserção: o intervalo de tempo inserido é inválido!";
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @msg;
    END IF;
	IF ((SELECT quantidade FROM Fantasia WHERE id_fantasia = NEW.id_fantasia) - (SELECT count(*) FROM Aluguel WHERE id_fantasia = NEW.id_fantasia AND status_aluguel = 'ativo') <= 0) THEN
		SET @msg = "Erro de inserção: não há unidades da fantasia selecionada disponíveis para aluguel!";
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @msg;
    END IF;
    SET @total = (SELECT valor_aluguel_dia FROM FANTASIA WHERE id_fantasia = NEW.id_fantasia);
	SET NEW.valor_total = datediff(NEW.data_fim, NEW.data_inicio)*@total;    
END§

CREATE TRIGGER ValidateRentInsertTimestamps_onUpdate BEFORE UPDATE ON Aluguel
FOR EACH ROW
BEGIN
	DECLARE msg TEXT;
    DECLARE total DECIMAL(8,2);
    IF (new.data_fim < new.data_inicio) THEN
		SET @msg = "Erro de atualização: o intervalo de tempo inserido é inválido!";
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @msg;
    END IF;
	IF ((OLD.status_aluguel != 'ativo') AND (NEW.status_aluguel = 'ativo') 
			AND (SELECT quantidade FROM Fantasia WHERE id_fantasia = NEW.id_fantasia) - (SELECT count(*) FROM Aluguel WHERE id_fantasia = NEW.id_fantasia AND status_aluguel = 'ativo') <= 0) THEN
		SET @msg = "Erro de inserção: não há unidades da fantasia selecionada disponíveis para aluguel!";
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @msg;
    END IF;    
    SET @total = (SELECT valor_aluguel_dia FROM FANTASIA WHERE id_fantasia = NEW.id_fantasia);
	SET NEW.valor_total = datediff(NEW.data_fim, NEW.data_inicio)*@total;    
END§

DELIMITER ;

INSERT INTO DBAdmin(username_admin, senha_admin, allow_insert, allow_update, allow_delete)
	VALUES ("admin", "admin", true, true, true);

INSERT INTO Categoria(nome_categoria)
	VALUES ("Terror");
    
INSERT INTO Cliente(username_cliente, senha_cliente)
	VALUES ("teste", "teste");
    
INSERT INTO Fantasia(nome_fantasia, quantidade, tamanho, valor_aluguel_dia, id_admin)
	VALUES ("Fantasia Vampiro", 5, 'P', 122.30, 1);
    
INSERT INTO Aluguel(id_fantasia, id_cliente, data_fim, status_aluguel)
	VALUES (1, 1, "2023-02-28", 'ativo');

INSERT INTO Aluguel(id_fantasia, id_cliente, data_fim, status_aluguel)
	VALUES (1, 1, "2023-02-28", 'ativo');
    
INSERT INTO Aluguel(id_fantasia, id_cliente, data_fim, status_aluguel)
	VALUES (1, 1, "2023-02-28", 'ativo');

    
UPDATE Aluguel SET data_fim = date_add(data_fim, INTERVAL 2 DAY) WHERE id_aluguel = 1;

SELECT * FROM Aluguel;