# Projeto Banquinho BD

Projeto simples usando a PokeAPI como fonte de dados.

## Backend

### Credenciais de acesso

As credenciais estao definidas em `backend/docker-compose.yml` e usadas em
`backend/src/main/resources/application.properties`:

- Host: `localhost`
- Porta: `5433`
- Database: `projeto_banquinho`
- Usuario: `dev`
- Senha: `dev`

### Como rodar o banco de dados:

#### Pre-requisitos

- Node.js instalado
- Docker rodando o PostgreSQL (veja o `docker-compose.yml`)
- Variaveis de ambiente configuradas no `.env`

#### Passos

1. **Suba o banco de dados com Docker:**
   ```bash
   docker-compose up -d
   ```

2. **Instale as dependencias (na pasta `db-schema/`):**
   ```bash
   cd db-schema
   npm install
   ```

3. **Crie um arquivo `.env` de ambiente para fazer a migracao, crie o arquivo ainda em `db-schema/`:**
   ```
    DATABASE_URL="postgres://dev:dev@localhost:5433/projeto_banquinho?schema=public"
   ```

4. **Em `db-schema/` rode as migracoes do Prisma:**
   ```bash
   npx prisma migrate dev
   ```

#### Observacoes

- Certifique-se que o banco esta rodando antes de executar comandos do Prisma.
- As variaveis do `.env` sao carregadas automaticamente se voce usar `import "dotenv/config"` no seu projeto.

#### Comandos uteis

- Visualizar o banco com Prisma Studio:
  ```bash
  npx prisma studio
  ```

- Verificar o status das migracoes:
  ```bash
  npx prisma migrate status
  ```

### Dicionario de Dados

| Tabela           | Atributo         | Tipo         | Restricoes   | Semantica/Descricao                             |
|:-----------------|:-----------------|:-------------|:-------------|:------------------------------------------------|
| **Especie**      | nome             | VARCHAR(50)  | PK, NOT NULL | Nome unico da especie (ex: Pikachu).            |
| **Especie**      | imagem_url       | TEXT         | -            | Link para o sprite oficial.                     |
| **Tipo**         | nome             | VARCHAR(30)  | PK, NOT NULL | Nome do elemento (ex: Fogo, Agua).              |
| **Especie_Tipo** | nome_especie     | VARCHAR(50)  | FK, PK       | Ligacao N:N entre Especie e Tipo.               |
| **Especie_Tipo** | nome_tipo        | VARCHAR(30)  | FK, PK       | Ligacao N:N entre Especie e Tipo.               |
| **Ataque**       | nome             | VARCHAR(50)  | PK, NOT NULL | Nome do movimento.                              |
| **Ataque**       | categoria        | VARCHAR(20)  | NOT NULL     | Categoria: 'Physical', 'Special' ou 'Status'.   |
| **Ataque**       | poder            | INT          | -            | Valor da forca. Nulo para golpes de status.     |
| **Ataque**       | nome_tipo        | VARCHAR(30)  | FK           | Elemento ao qual o ataque pertence.             |
| **Pokemon**      | id               | SERIAL       | PK           | ID unico da instancia do Pokemon.               |
| **Pokemon**      | apelido          | VARCHAR(50)  | -            | Nome dado pelo treinador ao especime.           |
| **Pokemon**      | nome_especie     | VARCHAR(50)  | FK, NOT NULL | Especie base deste Pokemon.                     |
| **Pokemon**      | id_time          | INT          | FK           | Time atual ao qual o Pokemon pertence.          |
| **Treinador**    | id               | SERIAL       | PK           | Identificador unico do treinador.               |
| **Treinador**    | nome             | VARCHAR(100) | NOT NULL     | Nome completo do treinador.                     |
| **Time**         | id               | SERIAL       | PK           | Identificador unico da equipe.                  |
| **Time**         | nome             | VARCHAR(100) | -            | Nome da equipe.                                 |
| **Time**         | id_treinador     | INT          | FK, NOT NULL | Dono responsavel pelo time.                     |
| **Torneio**      | id               | SERIAL       | PK           | Identificador da competicao.                    |
| **Torneio**      | data_inicio      | DATE         | NOT NULL     | Data de abertura do torneio.                    |
| **Torneio**      | id_time_vencedor | INT          | FK           | Time campeao do torneio.                        |
| **Batalha**      | id               | SERIAL       | PK           | Identificador unico do confronto.               |
| **Batalha**      | rodada           | INT          | NOT NULL     | Fase da competicao (1: Oitavas, 2: Quartas...). |
| **Batalha**      | id_torneio       | INT          | FK, NOT NULL | Torneio ao qual a batalha pertence.             |
| **Batalha**      | id_time_vencedor | INT          | FK           | Time que venceu este combate especifico.        |

## Links uteis
- API: https://pokeapi.co/
- Documentacao: https://pokeapi.co/docs/v2
