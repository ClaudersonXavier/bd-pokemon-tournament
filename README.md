# Projeto Banquinho BD

Projeto simples usando a PokeAPI como fonte de dados.

## Docker (PostgreSQL)
O backend usa um banco PostgreSQL via Docker Compose.

### Como rodar
1. Na raiz do repo:
   - `cd backend`
   - `docker compose up -d`
2. Para parar:
   - `docker compose down`

### Credenciais de acesso
As credenciais estão definidas em `backend/docker-compose.yml` e usadas em `backend/src/main/resources/application.properties`:
- Host: `localhost`
- Porta: `5432`
- Database: `projeto_banquinho`
- Usuário: `dev`
- Senha: `dev`

## Dicionário de Dados
| Tabela | Atributo | Tipo | Restrições | Semântica/Descrição |
| :--- | :--- | :--- | :--- | :--- |
| **Especie** | nome | VARCHAR(50) | PK, NOT NULL | Nome único da espécie (ex: Pikachu). |
| **Especie** | imagem_url | TEXT | - | Link para o sprite oficial. |
| **Tipo** | nome | VARCHAR(30) | PK, NOT NULL | Nome do elemento (ex: Fogo, Água). |
| **Especie_Tipo**| nome_especie | VARCHAR(50) | FK, PK | Ligação N:N entre Espécie e Tipo. |
| **Especie_Tipo**| nome_tipo | VARCHAR(30) | FK, PK | Ligação N:N entre Espécie e Tipo. |
| **Ataque** | nome | VARCHAR(50) | PK, NOT NULL | Nome do movimento. |
| **Ataque** | categoria | VARCHAR(20) | NOT NULL | Categoria: 'Physical', 'Special' ou 'Status'. |
| **Ataque** | poder | INT | - | Valor da força. Nulo para golpes de status. |
| **Ataque** | nome_tipo | VARCHAR(30) | FK | Elemento ao qual o ataque pertence. |
| **Pokemon** | id | SERIAL | PK | ID único da instância do Pokémon. |
| **Pokemon** | apelido | VARCHAR(50) | - | Nome dado pelo treinador ao espécime. |
| **Pokemon** | nome_especie | VARCHAR(50) | FK, NOT NULL | Espécie base deste Pokémon. |
| **Pokemon** | id_time | INT | FK | Time atual ao qual o Pokémon pertence. |
| **Treinador** | id | SERIAL | PK | Identificador único do treinador. |
| **Treinador** | nome | VARCHAR(100) | NOT NULL | Nome completo do treinador. |
| **Time** | id | SERIAL | PK | Identificador único da equipe. |
| **Time** | nome | VARCHAR(100) | - | Nome da equipe. |
| **Time** | id_treinador | INT | FK, NOT NULL | Dono responsável pelo time. |
| **Torneio** | id | SERIAL | PK | Identificador da competição. |
| **Torneio** | data_inicio | DATE | NOT NULL | Data de abertura do torneio. |
| **Torneio** | id_time_vencedor| INT | FK | Time campeão do torneio. |
| **Batalha** | id | SERIAL | PK | Identificador único do confronto. |
| **Batalha** | rodada | INT | NOT NULL | Fase da competição (1: Oitavas, 2: Quartas...). |
| **Batalha** | id_torneio | INT | FK, NOT NULL | Torneio ao qual a batalha pertence. |
| **Batalha** | id_time_vencedor| INT | FK | Time que venceu este combate específico. |

## Links uteis
- API: https://pokeapi.co/
- Documentacao: https://pokeapi.co/docs/v2