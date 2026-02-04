# Projeto Banquinho BD

Projeto simples usando a PokeAPI como fonte de dados.

## Instruções de como rodar o projeto

### Credenciais de acesso

As credenciais estao definidas em `backend/docker-compose.yml` e usadas em
`backend/src/main/resources/application.properties`:

- Host: `localhost`
- Porta: `5433`
- Database: `projeto_banquinho`
- Usuario: `dev`
- Senha: `dev`

### Banco de dados

#### Pre-requisitos

- Node.js instalado
- Docker rodando o PostgreSQL (veja o `docker-compose.yml`)
- Variaveis de ambiente configuradas no `.env`

#### Passos

1. **Entre na pasta `backend/`:**
   ```bash
   cd backend
   ```

2. **Suba o banco de dados com Docker:**
   ```bash
   docker-compose up -d
   ```

3. **Instale as dependencias (na pasta `db-schema/`):**
   ```bash
   cd db-schema
   npm install
   ```

4. **Crie um arquivo `.env` de ambiente para fazer a migracao, crie o arquivo ainda em `db-schema/`:**
   ```
    DATABASE_URL="postgres://dev:dev@localhost:5433/projeto_banquinho?schema=public"
   ```

5. **Em `db-schema/` rode as migracoes do Prisma:**
   ```bash
   npx prisma migrate dev
   ```

   > **Pronto!** A migracao foi aplicada, o DDL foi executado e o banco ja tem as tabelas basicas do projeto.

#### Observacoes

- Certifique-se que o banco esta rodando antes de executar comandos do Prisma.

#### Comandos uteis

- Visualizar o banco com Prisma Studio:
  ```bash
  npx prisma studio
  ```

- Verificar o status das migracoes:
  ```bash
  npx prisma migrate status
  ```

## Esquema conceitual do BD atualizado

![Esquema conceitual](<backend/Modelo ER.png>)

Caso queira ver o arquivo bruto ele está em: `backend/Modelo ER.png`

## Dicionario de dados

### Tabela: ataque

Descricao da Tabela: Tabela de ataques disponiveis para Pokemon.

| Atributo  | Tipo de Dado  | Restricoes                  | Semantica dos Atributos                            |
|:----------|:--------------|:----------------------------|:---------------------------------------------------|
| nome      | VARCHAR(255)  | PK, NOT NULL                | Nome do movimento.                                 |
| categoria | VARCHAR(255)  | NOT NULL                    | Categoria podendo ser: Physical, Special ou Status.|
| poder     | INTEGER       | NOT NULL                    | Valor da forca. Nulo para golpes de status.        |
| tipo_nome | VARCHAR(255)  | FK tipo(nome), NOT NULL     | Elemento ao qual o ataque pertence.                |

### Tabela: batalha

Descricao da Tabela: Tabela de batalhas realizadas em torneios.

| Atributo        | Tipo de Dado   | Restricoes                            | Semantica dos Atributos                           |
|:----------------|:---------------|:--------------------------------------|:--------------------------------------------------|
| id              | BIGSERIAL      | PK, NOT NULL, DEFAULT autoincrement   | Identificador unico do confronto.                 |
| rodada          | INTEGER        | NOT NULL                              | Fase da competicao (1: Oitavas, 2: Quartas...).    |
| horario_inicio  | TIMESTAMP(6)   | NOT NULL                              | Horario do inicio da batalha.                     |
| horario_fim     | TIMESTAMP(6)   | NOT NULL                              | Horario do fim da batalha.                        |
| torneio_id      | BIGINT         | FK torneio(id)                        | Torneio ao qual a batalha pertence.               |
| time_vencedor_id| BIGINT         | FK time(id), UNIQUE                   | Time que venceu este combate especifico.          |

### Tabela: batalha_times_participantes

Descricao da Tabela: Times participantes da batalha (maximo 2).

| Atributo               | Tipo de Dado | Restricoes                              | Semantica dos Atributos                 |
|:-----------------------|:-------------|:----------------------------------------|:----------------------------------------|
| batalha_id             | BIGINT       | PK, FK batalha(id), NOT NULL            | Identificador da batalha.               |
| times_participantes_id | BIGINT       | PK, FK time(id), NOT NULL               | Identificador do time participante.     |

### Tabela: especie

Descricao da Tabela: Tabela de especies de Pokemon cadastradas.

| Atributo  | Tipo de Dado | Restricoes       | Semantica dos Atributos                  |
|:----------|:-------------|:-----------------|:-----------------------------------------|
| nome      | VARCHAR(255) | PK, NOT NULL     | Nome unico da especie (ex: Pikachu).     |
| imagem_url| VARCHAR(255) | NOT NULL         | Link para o sprite oficial.              |

### Tabela: especie_tipos

Descricao da Tabela: Elemento(s) aquela especie pertence.

| Atributo     | Tipo de Dado | Restricoes                               | Semantica dos Atributos                  |
|:-------------|:-------------|:-----------------------------------------|:-----------------------------------------|
| especie_nome | VARCHAR(255) | PK, FK especie(nome), NOT NULL           | Identificador da especie.                |
| tipos_nome   | VARCHAR(255) | PK, FK tipo(nome), NOT NULL              | Identificador do tipo.                   |

### Tabela: pokemon

Descricao da Tabela: Tabela de Pokemon pertencentes a treinadores.

| Atributo     | Tipo de Dado | Restricoes                          | Semantica dos Atributos                       |
|:-------------|:-------------|:------------------------------------|:----------------------------------------------|
| id           | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | ID unico da instancia do Pokemon.             |
| apelido      | VARCHAR(255) | NOT NULL, UNIQUE                    | Nome dado pelo treinador ao especime.         |
| especie_nome | VARCHAR(255) | FK especie(nome)                    | Especie deste Pokemon.                        |
| treinador_id | BIGINT       | FK treinador(id)                    | Id do treinador deste pokemon.                |

### Tabela: pokemon_ataques

Descricao da Tabela: Lista de ataques aprendidos pelo Pokemon (maximo 4).

| Atributo     | Tipo de Dado | Restricoes                               | Semantica dos Atributos                 |
|:-------------|:-------------|:-----------------------------------------|:----------------------------------------|
| pokemon_id   | BIGINT       | PK, FK pokemon(id), NOT NULL             | Identificador do Pokemon.               |
| ataques_nome | VARCHAR(255) | PK, FK ataque(nome), NOT NULL            | Identificador do ataque.                |

### Tabela: pokemon_times

Descricao da Tabela: Times em que o Pokemon foi inscrito.

| Atributo   | Tipo de Dado | Restricoes                               | Semantica dos Atributos                 |
|:-----------|:-------------|:-----------------------------------------|:----------------------------------------|
| pokemon_id | BIGINT       | PK, FK pokemon(id), NOT NULL             | Identificador do Pokemon.               |
| times_id   | BIGINT       | PK, FK time(id), NOT NULL                | Identificador do time.                  |

### Tabela: time

Descricao da Tabela: Tabela de times montados por treinadores.

| Atributo     | Tipo de Dado | Restricoes                          | Semantica dos Atributos            |
|:-------------|:-------------|:------------------------------------|:-----------------------------------|
| id           | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador unico da equipe.     |
| nome         | VARCHAR(255) | NOT NULL                            | Nome da equipe.                    |
| treinador_id | BIGINT       | FK treinador(id)                    | Dono responsavel pelo time.        |

### Tabela: time_pokemons

Descricao da Tabela: Pokemons pertencentes ao time (maximo 6).

| Atributo    | Tipo de Dado | Restricoes                               | Semantica dos Atributos                 |
|:------------|:-------------|:-----------------------------------------|:----------------------------------------|
| pokemons_id | BIGINT       | PK, FK pokemon(id), NOT NULL             | Identificador do Pokemon.               |
| time_id     | BIGINT       | PK, FK time(id), NOT NULL                | Identificador do time.                  |

### Tabela: time_torneios

Descricao da Tabela: Torneios em que o time esta inscrito.

| Atributo   | Tipo de Dado | Restricoes                               | Semantica dos Atributos                 |
|:-----------|:-------------|:-----------------------------------------|:----------------------------------------|
| time_id    | BIGINT       | PK, FK time(id), NOT NULL                | Identificador do time.                  |
| torneios_id| BIGINT       | PK, FK torneio(id), NOT NULL             | Identificador do torneio.               |

### Tabela: tipo

Descricao da Tabela: Tabela de tipos elementais dos Pokemon.

| Atributo | Tipo de Dado | Restricoes   | Semantica dos Atributos            |
|:---------|:-------------|:-------------|:-----------------------------------|
| nome     | VARCHAR(255) | PK, NOT NULL | Nome do elemento (ex: Fogo, Agua). |

### Tabela: torneio

Descricao da Tabela: Tabela de torneios e seus periodos de inscricao e execucao.

| Atributo                   | Tipo de Dado | Restricoes                          | Semantica dos Atributos                               |
|:---------------------------|:-------------|:------------------------------------|:------------------------------------------------------|
| id                         | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador da competicao.                          |
| nome                       | VARCHAR(255) | NOT NULL                            | Nome daquele torneio.                                 |
| max_participantes          | INTEGER      | NOT NULL                            | Quantidade maximo de participantes do torneio.        |
| data_abertura_inscricoes   | TIMESTAMP(6) | NOT NULL                            | Data de abertura das inscricoes do torneio.           |
| data_encerramento_inscricoes| TIMESTAMP(6)| NOT NULL                            | Data de encerramento das inscricoes do torneio.       |
| data_inicio                | TIMESTAMP(6) | NOT NULL                            | Data de inicio do torneio.                            |
| data_fim                   | TIMESTAMP(6) | NOT NULL                            | Data do fim do torneio.                               |

### Tabela: torneio_times

Descricao da Tabela: Times inscritos no torneio.

| Atributo   | Tipo de Dado | Restricoes                               | Semantica dos Atributos                 |
|:-----------|:-------------|:-----------------------------------------|:----------------------------------------|
| times_id   | BIGINT       | PK, FK time(id), NOT NULL                | Identificador do time.                  |
| torneio_id | BIGINT       | PK, FK torneio(id), NOT NULL             | Identificador do torneio.               |

### Tabela: treinador

Descricao da Tabela: Tabela de treinadores cadastrados.

| Atributo | Tipo de Dado | Restricoes                          | Semantica dos Atributos             |
|:---------|:-------------|:------------------------------------|:------------------------------------|
| id       | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador unico do treinador.   |
| nome     | VARCHAR(255) | NOT NULL                            | Nome completo do treinador.         |

## Explicação de como os dados do banco foram povoados
// TODO: Ainda vamos fazer isso

## Links uteis
- API: https://pokeapi.co/
- Documentacao: https://pokeapi.co/docs/v2
