# 📚 Dicionário de Dados

<div align="center">

**[🏠 Início](../README.md)** • **[🧠 Esquema do BD](esquema-bd.md)** • **[🌱 Povoamento](povoamento-seeders.md)** • **[📊 Views SQL](views-sql.md)**

</div>

---

## Índice

- [Visão Geral](#visão-geral)
- [Tabelas Principais](#tabelas-principais)
  - [ataque](#tabela-ataque)
  - [batalha](#tabela-batalha)
  - [especie](#tabela-especie)
  - [pokemon](#tabela-pokemon)
  - [time](#tabela-time)
  - [tipo](#tabela-tipo)
  - [torneio](#tabela-torneio)
  - [treinador](#tabela-treinador)
- [Tabelas Associativas](#tabelas-associativas)

---

## Visão Geral

Este documento detalha todas as tabelas do banco de dados do sistema de torneios Pokémon, incluindo atributos, tipos de dados, restrições e semântica.

> **Nota:** As tabelas seguem a nomenclatura em português para facilitar o entendimento acadêmico, com exceção de campos técnicos que mantêm convenções SQL padrão.

---

## Tabelas Principais

### Tabela: `ataque`

**Descrição:** Tabela de ataques disponíveis para Pokémon.

| Atributo  | Tipo de Dado | Restrições              | Semântica dos Atributos                             |
| :-------- | :----------- | :---------------------- | :-------------------------------------------------- |
| nome      | VARCHAR(255) | PK, NOT NULL            | Nome do movimento.                                  |
| categoria | VARCHAR(255) | NOT NULL                | Categoria podendo ser: Physical, Special ou Status. |
| poder     | INTEGER      | NOT NULL                | Valor da força. Use 0 para golpes de status.        |
| tipo_nome | VARCHAR(255) | FK tipo(nome), NOT NULL | Elemento ao qual o ataque pertence.                 |

---

### Tabela: `batalha`

**Descrição:** Tabela de batalhas realizadas em torneios.

| Atributo         | Tipo de Dado | Restrições                          | Semântica dos Atributos                         |
| :--------------- | :----------- | :---------------------------------- | :---------------------------------------------- |
| id               | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador único do confronto.               |
| rodada           | INTEGER      | NOT NULL                            | Fase da competição (1: Oitavas, 2: Quartas...). |
| horario_inicio   | TIMESTAMP(6) | NOT NULL                            | Horário do início da batalha.                   |
| horario_fim      | TIMESTAMP(6) | NOT NULL                            | Horário do fim da batalha.                      |
| torneio_id       | BIGINT       | FK torneio(id)                      | Torneio ao qual a batalha pertence.             |
| time_vencedor_id | BIGINT       | FK time(id)                         | Time que venceu este combate específico.        |

---

### Tabela: `especie`

**Descrição:** Tabela de espécies de Pokémon cadastradas.

| Atributo   | Tipo de Dado | Restrições   | Semântica dos Atributos              |
| :--------- | :----------- | :----------- | :----------------------------------- |
| nome       | VARCHAR(255) | PK, NOT NULL | Nome único da espécie (ex: Pikachu). |
| imagem_url | VARCHAR(255) | NOT NULL     | Link para o sprite oficial.          |

---

### Tabela: `pokemon`

**Descrição:** Tabela de Pokémon pertencentes a treinadores.

| Atributo     | Tipo de Dado | Restrições                          | Semântica dos Atributos               |
| :----------- | :----------- | :---------------------------------- | :------------------------------------ |
| id           | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | ID único da instância do Pokémon.     |
| apelido      | VARCHAR(255) | NOT NULL, UNIQUE                    | Nome dado pelo treinador ao espécime. |
| especie_nome | VARCHAR(255) | FK especie(nome)                    | Espécie deste Pokémon.                |
| treinador_id | BIGINT       | FK treinador(id)                    | Id do treinador deste pokémon.        |

---

### Tabela: `time`

**Descrição:** Tabela de times montados por treinadores.

| Atributo     | Tipo de Dado | Restrições                          | Semântica dos Atributos        |
| :----------- | :----------- | :---------------------------------- | :----------------------------- |
| id           | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador único da equipe. |
| nome         | VARCHAR(255) | NOT NULL                            | Nome da equipe.                |
| treinador_id | BIGINT       | FK treinador(id)                    | Dono responsável pelo time.    |

---

### Tabela: `tipo`

**Descrição:** Tabela de tipos elementais dos Pokémon.

| Atributo | Tipo de Dado | Restrições   | Semântica dos Atributos            |
| :------- | :----------- | :----------- | :--------------------------------- |
| nome     | VARCHAR(255) | PK, NOT NULL | Nome do elemento (ex: Fogo, Água). |

---

### Tabela: `torneio`

**Descrição:** Tabela de torneios e seus períodos de inscrição e execução.

| Atributo                     | Tipo de Dado | Restrições                          | Semântica dos Atributos                         |
| :--------------------------- | :----------- | :---------------------------------- | :---------------------------------------------- |
| id                           | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador da competição.                    |
| nome                         | VARCHAR(255) | NOT NULL                            | Nome daquele torneio.                           |
| max_participantes            | INTEGER      | NOT NULL                            | Quantidade máximo de participantes do torneio.  |
| data_abertura_inscricoes     | TIMESTAMP(6) | NOT NULL                            | Data de abertura das inscrições do torneio.     |
| data_encerramento_inscricoes | TIMESTAMP(6) | NOT NULL                            | Data de encerramento das inscrições do torneio. |
| data_inicio                  | TIMESTAMP(6) | NOT NULL                            | Data de início do torneio.                      |
| data_fim                     | TIMESTAMP(6) | NOT NULL                            | Data do fim do torneio.                         |

---

### Tabela: `treinador`

**Descrição:** Tabela de treinadores cadastrados.

| Atributo | Tipo de Dado | Restrições                          | Semântica dos Atributos           |
| :------- | :----------- | :---------------------------------- | :-------------------------------- |
| id       | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador único do treinador. |
| nome     | VARCHAR(255) | NOT NULL                            | Nome completo do treinador.       |

---

## Tabelas Associativas

### Tabela: `batalha_times_participantes`

**Descrição:** Times participantes da batalha (máximo 2).

| Atributo               | Tipo de Dado | Restrições                   | Semântica dos Atributos             |
| :--------------------- | :----------- | :--------------------------- | :---------------------------------- |
| batalha_id             | BIGINT       | PK, FK batalha(id), NOT NULL | Identificador da batalha.           |
| times_participantes_id | BIGINT       | PK, FK time(id), NOT NULL    | Identificador do time participante. |

---

### Tabela: `especie_tipos`

**Descrição:** Elemento(s) aquela espécie pertence.

| Atributo     | Tipo de Dado | Restrições                     | Semântica dos Atributos   |
| :----------- | :----------- | :----------------------------- | :------------------------ |
| especie_nome | VARCHAR(255) | PK, FK especie(nome), NOT NULL | Identificador da espécie. |
| tipos_nome   | VARCHAR(255) | PK, FK tipo(nome), NOT NULL    | Identificador do tipo.    |

---

### Tabela: `pokemon_ataques`

**Descrição:** Lista de ataques aprendidos pelo Pokémon (máximo 4).

| Atributo     | Tipo de Dado | Restrições                    | Semântica dos Atributos   |
| :----------- | :----------- | :---------------------------- | :------------------------ |
| pokemon_id   | BIGINT       | PK, FK pokemon(id), NOT NULL  | Identificador do Pokémon. |
| ataques_nome | VARCHAR(255) | PK, FK ataque(nome), NOT NULL | Identificador do ataque.  |

---

### Tabela: `pokemon_times`

**Descrição:** Times em que o Pokémon foi inscrito.

| Atributo   | Tipo de Dado | Restrições                   | Semântica dos Atributos   |
| :--------- | :----------- | :--------------------------- | :------------------------ |
| pokemon_id | BIGINT       | PK, FK pokemon(id), NOT NULL | Identificador do Pokémon. |
| times_id   | BIGINT       | PK, FK time(id), NOT NULL    | Identificador do time.    |

---

### Tabela: `time_pokemons`

**Descrição:** Pokémons pertencentes ao time (máximo 6).

| Atributo    | Tipo de Dado | Restrições                   | Semântica dos Atributos   |
| :---------- | :----------- | :--------------------------- | :------------------------ |
| pokemons_id | BIGINT       | PK, FK pokemon(id), NOT NULL | Identificador do Pokémon. |
| time_id     | BIGINT       | PK, FK time(id), NOT NULL    | Identificador do time.    |

---

### Tabela: `torneio_times`

**Descrição:** Times inscritos no torneio.

| Atributo   | Tipo de Dado | Restrições                   | Semântica dos Atributos   |
| :--------- | :----------- | :--------------------------- | :------------------------ |
| times_id   | BIGINT       | PK, FK time(id), NOT NULL    | Identificador do time.    |
| torneio_id | BIGINT       | PK, FK torneio(id), NOT NULL | Identificador do torneio. |

---

<div align="center">

**[⬆️ Voltar ao topo](#-dicionário-de-dados)** • **[🏠 Voltar ao início](../README.md)**

</div>

---

> 📚 **Documento gerado para:** Projeto BD Pokémon Tournament  
> 🔗 **Documentos relacionados:** [Esquema BD](esquema-bd.md) | [Povoamento](povoamento-seeders.md) | [Views SQL](views-sql.md)
