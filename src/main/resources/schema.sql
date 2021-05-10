Drop table if exists Marcas;

Create Table Marcas(id BIGINT(6) PRIMARY KEY AUTO_INCREMENT,
nombre VARCHAR(45) NOT NULL);

Drop table if exists Coches;

Create Table Coches(ID BIGINT(6) PRIMARY KEY AUTO_INCREMENT,
modelo VARCHAR(45) NOT NULL,
matricula CHAR(8) NOT NULL,
marca_id BIGINT(6) NOT NULL,
FOREIGN KEY (marca_id) REFERENCES Marcas(id));
