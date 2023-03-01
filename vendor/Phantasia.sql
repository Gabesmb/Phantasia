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

INSERT INTO Cliente (username_cliente, senha_cliente, nome_cliente, e_mail, DDD_telefone, numero_telefone)
VALUES
('johndoe', '123456', 'John Doe', 'johndoe@email.com', '11', '987654321'),
('janedoe', '654321', 'Jane Doe', 'janedoe@email.com', '11', '987654322'),
('johndoejr', 'password', 'John Doe Jr', 'johndoejr@email.com', '11', '987654323'),
('marysmith', 'mypass', 'Mary Smith', 'marysmith@email.com', '11', '987654324'),
('bobsanders', 'pass123', 'Bob Sanders', 'bobsanders@email.com', '11', '987654325'),
('sarahbrown', 'brown123', 'Sarah Brown', 'sarahbrown@email.com', '11', '987654326'),
('tomjones', 'pass456', 'Tom Jones', 'tomjones@email.com', '11', '987654327'),
('jennyjackson', 'pass789', 'Jenny Jackson', 'jennyjackson@email.com', '11', '987654328'),
('markjones', 'password123', 'Mark Jones', 'markjones@email.com', '11', '987654329'),
('katewilliams', 'qwerty', 'Kate Williams', 'katewilliams@email.com', '11', '987654330'),
('chrisbrown', 'passbrown', 'Chris Brown', 'chrisbrown@email.com', '11', '987654331'),
('alexsmith', 'passalex', 'Alex Smith', 'alexsmith@email.com', '11', '987654332'),
('mariasouza', '12345678', 'Maria Souza', 'mariasouza@email.com', '11', '987654333'),
('renatoalmeida', 'almeida123', 'Renato Almeida', 'renatoalmeida@email.com', '11', '987654334'),
('claudiolima', 'lima456', 'Claudio Lima', 'claudiolima@email.com', '11', '987654335'),
('carlosmartins', 'martins789', 'Carlos Martins', 'carlosmartins@email.com', '11', '987654336'),
('andressasilva', '123456789', 'Andressa Silva', 'andressasilva@email.com', '11', '987654337'),
('andrejardim', 'jardim123', 'Andre Jardim', 'andrejardim@email.com', '11', '987654338'),
('juliasantos', 'santos456', 'Julia Santos', 'juliasantos@email.com', '11', '987654339'),
('pedropereira', 'pereira789', 'Pedro Pereira', 'pedropereira@email.com', '11', '987654340');


INSERT INTO DBAdmin (username_admin, senha_admin, nome_admin, e_mail, DDD_telefone, numero_telefone, allow_insert, allow_update, allow_delete) VALUES
('gabriel.admin', '123456', 'Gabriel Silva', 'gabriel.silva@gmail.com', '11', '987654321', true, true, false),
('maria.admin', 'abcdef', 'Maria Santos', 'maria.santos@yahoo.com', '21', '912345678', true, false, true),
('roberto.admin', 'senha123', 'Roberto Oliveira', 'roberto.oliveira@hotmail.com', '31', '945678123', true, false, false),
('fernanda.admin', 'abc123', 'Fernanda Costa', 'fernanda.costa@gmail.com', '11', '978945612', false, true, true),
('lucas.admin', 'p4ssw0rd', 'Lucas Souza', 'lucas.souza@yahoo.com', '21', '965874123', false, true, false),
('isabel.admin', 'qwerty', 'Isabel Pereira', 'isabel.pereira@hotmail.com', '31', '945631258', true, false, true),
('pedro.admin', 'senha456', 'Pedro Rodrigues', 'pedro.rodrigues@gmail.com', '11', '912345789', false, false, true),
('julia.admin', '123abc', 'Júlia Oliveira', 'julia.oliveira@yahoo.com', '21', '956874123', true, true, true),
('ana.admin', 'admin123', 'Ana Silva', 'ana.silva@hotmail.com', '31', '945631874', false, true, false),
('felipe.admin', 'senha123abc', 'Felipe Santos', 'felipe.santos@gmail.com', '11', '987456321', true, false, true),
('giovanna.admin', 'abc123xyz', 'Giovanna Almeida', 'giovanna.almeida@yahoo.com', '21', '912345963', false, true, false),
('rodrigo.admin', 'qwerty123', 'Rodrigo Costa', 'rodrigo.costa@gmail.com', '31', '965478123', true, true, true),
('beatriz.admin', 'senha1234', 'Beatriz Mendes', 'beatriz.mendes@hotmail.com', '11', '945631278', true, false, false),
('vitor.admin', 'password', 'Vitor Lima', 'vitor.lima@yahoo.com', '21', '956874963', true, false, true),
('camila.admin', 'adminadmin', 'Camila Santos', 'camila.santos@gmail.com', '31', '945631852', false, true, true),
('lucas2.admin', 'lucas123', 'Lucas Silva', 'lucas2.silva@gmail.com', '11', '987456978', false, true, false),
('ana2.admin', 'ana456', 'Ana Souza', 'ana2.souza@hotmail.com', '21', '945631987', true, false, true),
('felipe2.admin', 'felipe456', 'Felipe Oliveira', 'felipe2.oliveira@yahoo.com.br', '31', '912345741', false, false, true),
('roberto2.admin', 'roberto123', 'Roberto Santos', 'roberto2.santos@outlook.com', '11', '965478963', true, true, true),
('julia2.admin', 'julia456', 'Julia Pereira', 'julia2.pereira@gmail.com', '21', '987456231', true, false, false);

INSERT INTO Fantasia (nome_fantasia, quantidade, tamanho, valor_aluguel_dia, id_admin) VALUES
('Capitão América', 5, 'M', 50.00, 2),
('Mulher Maravilha', 7, 'G', 60.00, 5),
('Batman', 10, 'GG', 70.00, 3),
('Superman', 8, 'P', 60.00, 1),
('Mulher Gato', 3, 'M', 50.00, 4),
('Flash', 6, 'G', 55.00, 2),
('Arlequina', 4, 'GG', 65.00, 3),
('Thor', 9, 'PP', 45.00, 1),
('Homem de Ferro', 2, 'P', 75.00, 5),
('Viuva Negra', 5, 'M', 50.00, 4),
('Hulk', 7, 'G', 65.00, 2),
('Pantera Negra', 4, 'GG', 80.00, 3),
('Gamora', 3, 'P', 60.00, 1),
('Doutor Estranho', 6, 'M', 70.00, 5),
('Capitã Marvel', 2, 'G', 75.00, 4),
('Robin', 8, 'PP', 45.00, 2);

INSERT INTO Categoria (nome_categoria) VALUES
('Super-Heróis'),
('Vilões'),
('Personagens de Quadrinhos'),
('Personagens de Filmes'),
('Personagens de Séries'),
('Animais'),
('Personagens Infantis'),
('Fantasia Medieval'),
('Fantasia de Profissões'),
('Fantasia de Animes');

INSERT INTO Fantasia_Categoria VALUES
(1, 1),
(1, 3),
(2, 1),
(2, 4),
(3, 1),
(3, 2),
(4, 1),
(4, 4),
(5, 2),
(5, 5),
(6, 1),
(6, 3),
(7, 1),
(7, 2),
(8, 1),
(8, 4),
(9, 1),
(9, 5),
(10, 2),
(10, 3),
(11, 1),
(11, 4),
(12, 1),
(12, 2),
(13, 1),
(13, 5),
(14, 2),
(14, 3),
(15, 1),
(15, 4),
(16, 1),
(16, 2);

INSERT INTO Aluguel (id_fantasia, id_cliente, data_inicio, data_fim, status_aluguel) VALUES
(1, 2, '2023-02-01', '2023-02-05', 'devolvido'),
(1, 3, '2023-02-03', '2023-02-10', 'devolvido'),
(1, 5, '2023-02-08', '2023-02-15', 'devolvido'),
(1, 7, '2023-02-12', '2023-02-18', 'devolvido'),
(2, 1, '2023-02-05', '2023-02-12', 'devolvido'),
(2, 4, '2023-02-10', '2023-02-18', 'devolvido'),
(2, 8, '2023-02-15', '2023-02-22', 'devolvido'),
(3, 6, '2023-02-08', '2023-02-14', 'devolvido'),
(3, 10, '2023-02-12', '2023-02-21', 'devolvido'),
(4, 9, '2023-02-15', '2023-02-25', 'devolvido'),
(5, 11, '2023-02-18', '2023-02-26', 'devolvido'),
(5, 13, '2023-02-20', '2023-02-28', 'devolvido'),
(6, 12, '2023-02-22', '2023-03-02', 'ativo'),
(6, 15, '2023-02-25', '2023-03-05', 'ativo'),
(7, 17, '2023-02-28', '2023-03-06', 'ativo'),
(7, 18, '2023-03-01', '2023-03-08', 'ativo'),
(8, 19, '2023-03-01', '2023-03-10', 'ativo'),
(8, 20, '2023-03-01', '2023-03-15', 'ativo'),
(9, 2, '2023-02-02', '2023-02-09', 'devolvido'),
(9, 5, '2023-02-07', '2023-02-13', 'devolvido'),
(10, 8, '2023-02-14', '2023-02-22', 'devolvido'),
(11, 11, '2023-02-17', '2023-02-23', 'devolvido'),
(11, 14, '2023-02-21', '2023-02-28', 'devolvido'),
(12, 16, '2023-02-26', '2023-03-02', 'ativo'),
(12, 17, '2023-02-27', '2023-03-05', 'ativo'),
(13, 19, '2023-03-01', '2023-03-08', 'ativo'),
(14, 1, '2023-02-03', '2023-02-10', 'devolvido'),
(15, 4, '2023-02-10', '2023-02-17', 'devolvido');

#SELECT * FROM Aluguel;
#SELECT * From DBAdmin;

INSERT INTO DBAdmin (username_admin, senha_admin, allow_insert, allow_delete, allow_update) VALUES ('PhantasiaAdmin', 'Phantasia2.0', true, true, true);
INSERT INTO Cliente (username_cliente, senha_cliente) VALUES ('PhantasiaClient', 'Phantasia2.0');

SELECT * FROM DBAdmin;

#SELECT Fantasia.nome_fantasia, Categoria.nome_categoria FROM Fantasia, Categoria, Fantasia_Categoria WHERE Fantasia_Categoria.id_fantasia = Fantasia.id_fantasia AND Fantasia_Categoria.id_categoria = Categoria.id_categoria;

#SELECT Cliente.nome_cliente, Fantasia.nome_fantasia, Aluguel.data_inicio, Aluguel.data_fim, Aluguel.status_aluguel FROM Cliente, Fantasia, Aluguel WHERE Aluguel.id_cliente = Cliente.id_cliente AND Aluguel.id_fantasia = Fantasia.id_fantasia
