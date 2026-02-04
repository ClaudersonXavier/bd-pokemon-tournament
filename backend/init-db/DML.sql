-- ############################################################################
-- # ARQUIVO DML - EM DESENVOLVIMENTO
-- # Contém dados temporários para testes.
-- ############################################################################

INSERT INTO Tipo (nome) VALUES 
('Grass'), ('Poison'), ('Fire'), ('Flying'), ('Water'), ('Bug'), ('Normal'), ('Electric');

INSERT INTO Especie (nome, imagem_url) VALUES 
('Bulbasaur', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png'),
('Ivysaur', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/2.png'),
('Venusaur', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/3.png'),
('Charmander', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png'),
('Charmeleon', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/5.png'),
('Charizard', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png'),
('Squirtle', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png'),
('Wartortle', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/8.png'),
('Blastoise', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/9.png'),
('Pikachu', 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png');

INSERT INTO Especie_Tipo (nome_especie, nome_tipo) VALUES 
('Bulbasaur', 'Grass'), ('Bulbasaur', 'Poison'),
('Charmander', 'Fire'),
('Charizard', 'Fire'), ('Charizard', 'Flying'),
('Squirtle', 'Water'),
('Pikachu', 'Electric');

INSERT INTO Ataque (nome, categoria, poder, nome_tipo) VALUES 
('Vine Whip', 'Physical', 45, 'Grass'),
('Flamethrower', 'Special', 90, 'Fire'),
('Water Gun', 'Special', 40, 'Water'),
('Thunderbolt', 'Special', 90, 'Electric'),
('Slash', 'Physical', 70, 'Normal');

INSERT INTO Treinador (nome) VALUES ('Red'), ('Blue');

INSERT INTO Time (nome, id_treinador) VALUES 
('Kanto Starters', 1),
('Blue Rivals', 2);

INSERT INTO Pokemon (apelido, nome_especie, id_time) VALUES 
('Bulba', 'Bulbasaur', 1),
('Lagarto', 'Charizard', 1),
('João', 'Pikachu', 1),
('Tartaruga', 'Blastoise', 2);

INSERT INTO Pokemon_Ataque (id_pokemon, nome_ataque) VALUES 
(1, 'Vine Whip'),
(2, 'Flamethrower'),
(2, 'Slash'),
(3, 'Thunderbolt'),
(4, 'Water Gun');

-- TORNEIO E BATALHA
INSERT INTO Torneio (nome, data_inicio, data_fim) VALUES 
('Pokémon League Championship', '2026-02-01', '2026-02-28');

INSERT INTO Batalha (rodada, hora_inicio, hora_fim, id_torneio) VALUES 
(1, '2026-02-03 10:00:00', '2026-02-03 10:15:00', 1);

INSERT INTO Time_Batalha (id_time, id_batalha) VALUES (1, 1), (2, 1);