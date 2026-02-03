CREATE TABLE Tipo (
    nome VARCHAR(30) PRIMARY KEY
);

CREATE TABLE Especie (
    nome VARCHAR(50) PRIMARY KEY,
    imagem_url TEXT
);

CREATE TABLE Treinador (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

-- Relacionamento N:N entre Especie e Tipo
CREATE TABLE Especie_Tipo (
    nome_especie VARCHAR(50) REFERENCES Especie(nome),
    nome_tipo VARCHAR(30) REFERENCES Tipo(nome),
    PRIMARY KEY (nome_especie, nome_tipo)
);

CREATE TABLE Ataque (
    nome VARCHAR(50) PRIMARY KEY,
    categoria VARCHAR(20),
    poder INT,
    nome_tipo VARCHAR(30) REFERENCES Tipo(nome)
);

CREATE TABLE Time (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100),
    id_treinador INT REFERENCES Treinador(id)
);

CREATE TABLE Pokemon (
    id SERIAL PRIMARY KEY,
    apelido VARCHAR(50),
    nome_especie VARCHAR(50) REFERENCES Especie(nome),
    id_time INT REFERENCES Time(id)
);

-- Relacionamento N:N entre Pokemon e Ataque
CREATE TABLE Pokemon_Ataque (
    id_pokemon INT REFERENCES Pokemon(id),
    nome_ataque VARCHAR(50) REFERENCES Ataque(nome),
    PRIMARY KEY (id_pokemon, nome_ataque)
);

CREATE TABLE Torneio (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100),
    data_inicio DATE,
    data_fim DATE,
    id_time_vencedor INT REFERENCES Time(id)
);

CREATE TABLE Batalha (
    id SERIAL PRIMARY KEY,
    rodada INT,
    hora_inicio TIMESTAMP,
    hora_fim TIMESTAMP,
    id_torneio INT REFERENCES Torneio(id),
    id_time_vencedor INT REFERENCES Time(id)
);

-- Tabelas Associativas
CREATE TABLE Time_Torneio (
    id_time INT REFERENCES Time(id),
    id_torneio INT REFERENCES Torneio(id),
    PRIMARY KEY (id_time, id_torneio)
);

CREATE TABLE Time_Batalha (
    id_time INT REFERENCES Time(id),
    id_batalha INT REFERENCES Batalha(id),
    PRIMARY KEY (id_time, id_batalha)
);