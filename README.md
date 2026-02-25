# Projeto Banquinho BD

Projeto simples usando a PokeAPI como fonte de dados.

## Instruções de como rodar o projeto

### Credenciais de acesso

As credenciais de desenvolvimento estao no arquivo `.env.example` da raiz e sao
consumidas pelo `docker-compose.yml` da raiz e por
`backend/src/main/resources/application.properties`:

- Host: `localhost`
- Porta: `5433`
- Database: `projeto_banquinho`
- Usuario: `dev`
- Senha: `dev`

#### Pre-requisitos

- Docker instalado

#### Passos

1. **(Opcional) Copie o arquivo de exemplo de ambiente:**

   ```bash
   cp .env.example .env
   ```

2. **Suba toda a stack com um único comando (a partir da raiz):**

   ```bash
   docker compose up --build
   ```

3. **As aplicacoes ficarao disponiveis em:**

   ```bash
   Backend:  http://localhost:8080
   Frontend: http://localhost:4200
   ```

#### Observacoes

- O compose da raiz inclui banco, migrate, seeder, backend e frontend.
- Se o arquivo `.env` nao existir, o `docker compose` usa valores padrao de desenvolvimento definidos em `docker-compose.yml`.

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

| Atributo  | Tipo de Dado | Restricoes              | Semantica dos Atributos                             |
| :-------- | :----------- | :---------------------- | :-------------------------------------------------- |
| nome      | VARCHAR(255) | PK, NOT NULL            | Nome do movimento.                                  |
| categoria | VARCHAR(255) | NOT NULL                | Categoria podendo ser: Physical, Special ou Status. |
| poder     | INTEGER      | NOT NULL                | Valor da forca. Use 0 para golpes de status.        |
| tipo_nome | VARCHAR(255) | FK tipo(nome), NOT NULL | Elemento ao qual o ataque pertence.                 |

### Tabela: batalha

Descricao da Tabela: Tabela de batalhas realizadas em torneios.

| Atributo         | Tipo de Dado | Restricoes                          | Semantica dos Atributos                         |
| :--------------- | :----------- | :---------------------------------- | :---------------------------------------------- |
| id               | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador unico do confronto.               |
| rodada           | INTEGER      | NOT NULL                            | Fase da competicao (1: Oitavas, 2: Quartas...). |
| horario_inicio   | TIMESTAMP(6) | NOT NULL                            | Horario do inicio da batalha.                   |
| horario_fim      | TIMESTAMP(6) | NOT NULL                            | Horario do fim da batalha.                      |
| torneio_id       | BIGINT       | FK torneio(id)                      | Torneio ao qual a batalha pertence.             |
| time_vencedor_id | BIGINT       | FK time(id)                         | Time que venceu este combate especifico.        |

### Tabela: batalha_times_participantes

Descricao da Tabela: Times participantes da batalha (maximo 2).

| Atributo               | Tipo de Dado | Restricoes                   | Semantica dos Atributos             |
| :--------------------- | :----------- | :--------------------------- | :---------------------------------- |
| batalha_id             | BIGINT       | PK, FK batalha(id), NOT NULL | Identificador da batalha.           |
| times_participantes_id | BIGINT       | PK, FK time(id), NOT NULL    | Identificador do time participante. |

### Tabela: especie

Descricao da Tabela: Tabela de especies de Pokemon cadastradas.

| Atributo   | Tipo de Dado | Restricoes   | Semantica dos Atributos              |
| :--------- | :----------- | :----------- | :----------------------------------- |
| nome       | VARCHAR(255) | PK, NOT NULL | Nome unico da especie (ex: Pikachu). |
| imagem_url | VARCHAR(255) | NOT NULL     | Link para o sprite oficial.          |

### Tabela: especie_tipos

Descricao da Tabela: Elemento(s) aquela especie pertence.

| Atributo     | Tipo de Dado | Restricoes                     | Semantica dos Atributos   |
| :----------- | :----------- | :----------------------------- | :------------------------ |
| especie_nome | VARCHAR(255) | PK, FK especie(nome), NOT NULL | Identificador da especie. |
| tipos_nome   | VARCHAR(255) | PK, FK tipo(nome), NOT NULL    | Identificador do tipo.    |

### Tabela: pokemon

Descricao da Tabela: Tabela de Pokemon pertencentes a treinadores.

| Atributo     | Tipo de Dado | Restricoes                          | Semantica dos Atributos               |
| :----------- | :----------- | :---------------------------------- | :------------------------------------ |
| id           | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | ID unico da instancia do Pokemon.     |
| apelido      | VARCHAR(255) | NOT NULL, UNIQUE                    | Nome dado pelo treinador ao especime. |
| especie_nome | VARCHAR(255) | FK especie(nome)                    | Especie deste Pokemon.                |
| treinador_id | BIGINT       | FK treinador(id)                    | Id do treinador deste pokemon.        |

### Tabela: pokemon_ataques

Descricao da Tabela: Lista de ataques aprendidos pelo Pokemon (maximo 4).

| Atributo     | Tipo de Dado | Restricoes                    | Semantica dos Atributos   |
| :----------- | :----------- | :---------------------------- | :------------------------ |
| pokemon_id   | BIGINT       | PK, FK pokemon(id), NOT NULL  | Identificador do Pokemon. |
| ataques_nome | VARCHAR(255) | PK, FK ataque(nome), NOT NULL | Identificador do ataque.  |

### Tabela: pokemon_times

Descricao da Tabela: Times em que o Pokemon foi inscrito.

| Atributo   | Tipo de Dado | Restricoes                   | Semantica dos Atributos   |
| :--------- | :----------- | :--------------------------- | :------------------------ |
| pokemon_id | BIGINT       | PK, FK pokemon(id), NOT NULL | Identificador do Pokemon. |
| times_id   | BIGINT       | PK, FK time(id), NOT NULL    | Identificador do time.    |

### Tabela: time

Descricao da Tabela: Tabela de times montados por treinadores.

| Atributo     | Tipo de Dado | Restricoes                          | Semantica dos Atributos        |
| :----------- | :----------- | :---------------------------------- | :----------------------------- |
| id           | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador unico da equipe. |
| nome         | VARCHAR(255) | NOT NULL                            | Nome da equipe.                |
| treinador_id | BIGINT       | FK treinador(id)                    | Dono responsavel pelo time.    |

### Tabela: time_pokemons

Descricao da Tabela: Pokemons pertencentes ao time (maximo 6).

| Atributo    | Tipo de Dado | Restricoes                   | Semantica dos Atributos   |
| :---------- | :----------- | :--------------------------- | :------------------------ |
| pokemons_id | BIGINT       | PK, FK pokemon(id), NOT NULL | Identificador do Pokemon. |
| time_id     | BIGINT       | PK, FK time(id), NOT NULL    | Identificador do time.    |

### Tabela: tipo

Descricao da Tabela: Tabela de tipos elementais dos Pokemon.

| Atributo | Tipo de Dado | Restricoes   | Semantica dos Atributos            |
| :------- | :----------- | :----------- | :--------------------------------- |
| nome     | VARCHAR(255) | PK, NOT NULL | Nome do elemento (ex: Fogo, Agua). |

### Tabela: torneio

Descricao da Tabela: Tabela de torneios e seus periodos de inscricao e execucao.

| Atributo                     | Tipo de Dado | Restricoes                          | Semantica dos Atributos                         |
| :--------------------------- | :----------- | :---------------------------------- | :---------------------------------------------- |
| id                           | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador da competicao.                    |
| nome                         | VARCHAR(255) | NOT NULL                            | Nome daquele torneio.                           |
| max_participantes            | INTEGER      | NOT NULL                            | Quantidade maximo de participantes do torneio.  |
| data_abertura_inscricoes     | TIMESTAMP(6) | NOT NULL                            | Data de abertura das inscricoes do torneio.     |
| data_encerramento_inscricoes | TIMESTAMP(6) | NOT NULL                            | Data de encerramento das inscricoes do torneio. |
| data_inicio                  | TIMESTAMP(6) | NOT NULL                            | Data de inicio do torneio.                      |
| data_fim                     | TIMESTAMP(6) | NOT NULL                            | Data do fim do torneio.                         |

### Tabela: torneio_times

Descricao da Tabela: Times inscritos no torneio.

| Atributo   | Tipo de Dado | Restricoes                   | Semantica dos Atributos   |
| :--------- | :----------- | :--------------------------- | :------------------------ |
| times_id   | BIGINT       | PK, FK time(id), NOT NULL    | Identificador do time.    |
| torneio_id | BIGINT       | PK, FK torneio(id), NOT NULL | Identificador do torneio. |

### Tabela: treinador

Descricao da Tabela: Tabela de treinadores cadastrados.

| Atributo | Tipo de Dado | Restricoes                          | Semantica dos Atributos           |
| :------- | :----------- | :---------------------------------- | :-------------------------------- |
| id       | BIGSERIAL    | PK, NOT NULL, DEFAULT autoincrement | Identificador unico do treinador. |
| nome     | VARCHAR(255) | NOT NULL                            | Nome completo do treinador.       |

## Explicação de como os dados do banco foram povoados

### 📦 Sistema de Seeders

O projeto utiliza **seeders automatizados** para popular o banco de dados com dados realistas usando a [PokeAPI](https://pokeapi.co/) e dados mockados. Os seeders seguem uma ordem de dependências para garantir integridade referencial.

### 🚀 Como Popular o Banco de Dados

#### **Opção 1: Seed Completo (Recomendado)**

Execute o **SeedRunner** que popula TODAS as tabelas automaticamente na ordem correta:

```bash
# Via VS Code (mais fácil)
1. Abra: backend/src/main/java/com/ufape/projetobanquinhobd/seeder/SeedRunner.java
2. Clique com botão direito → Run 'SeedRunner.main()'
```

```bash
# Via terminal (alternativa)
cd backend
./gradlew clean build
# Execute a classe SeedRunner através da IDE ou command line
```

#### **Opção 2: Seeders Individuais**

Para popular apenas uma tabela específica, execute o runner correspondente:

```bash
# Tipos apenas
Run 'SeedTipoRunner.main()'

# Espécies apenas (requer Tipos já populados)
Run 'SeedEspecieRunner.main()'

# E assim por diante...
```

### 📊 Dados Populados

Ao executar o **SeedRunner completo**, o banco será populado com:

| Seeder              | Descrição                        | Quantidade                    | Fonte   |
| ------------------- | -------------------------------- | ----------------------------- | ------- |
| **TipoSeeder**      | Tipos elementais de Pokémon      | 18 tipos                      | PokeAPI |
| **EspecieSeeder**   | Espécies de Pokémon com imagens  | 50 espécies                   | PokeAPI |
| **AtaqueSeeder**    | Movimentos/ataques de Pokémon    | 50 ataques                    | PokeAPI |
| **TreinadorSeeder** | Treinadores icônicos             | 4 treinadores                 | Mockado |
| **PokemonSeeder**   | Pokémons com ataques compatíveis | 24 pokémons (6 por treinador) | Gerado  |
| **TimeSeeder**      | Times competitivos               | 4 times (1 por treinador)     | Gerado  |
| **TorneioSeeder**   | Torneios com inscrições e datas  | 2 torneios                    | Mockado |
| **BatalhaSeeder**   | Batalhas com resultados          | 6 batalhas (3 por torneio)    | Mockado |

### 🔄 Ordem de Execução (Dependências)

O SeedRunner executa os seeders nesta ordem para respeitar as foreign keys:

```
1. Tipos          (base para espécies e ataques)
   ↓
2. Espécies       (requer tipos)
   ↓
3. Ataques        (requer tipos)
   ↓
4. Treinadores    (independente)
   ↓
5. Pokémons       (requer espécies, ataques e treinadores)
   ↓
6. Times          (requer treinadores e pokémons)
   ↓
7. Torneios       (requer times)
   ↓
8. Batalhas       (requer torneios e times)
```

### � Como os Seeders Funcionam Tecnicamente

Os seeders utilizam uma arquitetura modular com Spring Boot, JPA/Hibernate e WebClient para comunicação HTTP reativa. Aqui está o fluxo completo:

#### **Fluxo de Dados: Da API ao Banco**

**Exemplo: EspecieSeeder (com PokeAPI)**

1. **Requisição HTTP (WebClient)**

   ```java
   // Busca lista de pokémons da API
   GET https://pokeapi.co/api/v2/pokemon?limit=50

   WebClient webClient = WebClient.builder()
       .baseUrl("https://pokeapi.co/api/v2")
       .build();

   PokemonListResponse response = webClient.get()
       .uri("/pokemon?limit=50")
       .retrieve()
       .bodyToMono(PokemonListResponse.class)
       .block();
   ```

2. **Deserialização JSON → Objeto Java (DTO)**

   ```java
   // JSON da API:
   {
     "name": "pikachu",
     "url": "https://pokeapi.co/api/v2/pokemon/25/"
   }

   // É transformado em:
   PokemonListResponse.Result {
     name = "pikachu",
     url = "https://pokeapi.co/api/v2/pokemon/25/"
   }
   ```

3. **Busca Detalhes de Cada Pokémon**

   ```java
   // Para cada pokémon, faz nova requisição:
   GET https://pokeapi.co/api/v2/pokemon/25/

   PokemonDetailResponse details = webClient.get()
       .uri("/pokemon/25")
       .retrieve()
       .bodyToMono(PokemonDetailResponse.class)
       .block();
   ```

4. **Extração de Dados do JSON Complexo**

   ```java
   // JSON retornado (simplificado):
   {
     "name": "pikachu",
     "sprites": {
       "versions": {
         "generation-v": {
           "black-white": {
             "front_default": "https://...pikachu.png"
           }
         }
       }
     },
     "types": [
       { "type": { "name": "electric" } }
     ]
   }

   // Extração programática:
   String nome = details.name();
   String imagemUrl = details.sprites()
                             .versions()
                             .generationV()
                             .blackWhite()
                             .frontDefault();
   List<String> tipos = details.types()
                               .stream()
                               .map(t -> t.type().name())
                               .toList();
   ```

5. **Transformação: DTO → Entidade JPA**

   ```java
   // Busca tipos do banco (foreign key)
   Set<Tipo> tiposSet = new HashSet<>();
   for (String tipoNome : tipos) {
       Tipo tipo = tipoService.buscarPorNome(
           capitalizeFirst(tipoNome)
       );
       tiposSet.add(tipo);
   }

   // Cria entidade Especie
   Especie especie = new Especie(
       nome,           // "pikachu"
       imagemUrl,      // URL da imagem
       tiposSet        // Set<Tipo> com objetos do banco
   );
   ```

6. **Persistência no Banco (JPA/Hibernate)**

   ```java
   // Salva usando service layer
   especieService.salvar(especie);

   // Hibernate gera SQL automaticamente:
   INSERT INTO especie (nome, imagem_url)
   VALUES ('pikachu', 'https://...pikachu.png');

   INSERT INTO especie_tipos (especie_nome, tipos_nome)
   VALUES ('pikachu', 'Electric');
   ```

7. **Controle de Duplicação**

   ```java
   // Verifica se já existe antes de salvar
   Optional<Especie> existente =
       especieService.buscarPorNome(nome);

   if (existente.isEmpty()) {
       especieService.salvar(especie);
       System.out.println("✓ Especie criada: " + nome);
   } else {
       System.out.println("↷ Especie já existe: " + nome);
   }
   ```

#### **Seeders sem API Externa (Mockados)**

**Exemplo: TreinadorSeeder**

1. **Lista Hardcoded no Código**

   ```java
   private static final List<String> TREINADORES = List.of(
       "Ash Ketchum",
       "Misty",
       "Brock",
       "Gary Oak"
   );
   ```

2. **Loop de Criação**

   ```java
   for (String nome : TREINADORES) {
       Treinador treinador = new Treinador(nome);
       treinadorService.salvar(treinador);
       System.out.println("✓ Treinador criado: " + nome);
   }
   ```

3. **SQL Gerado pelo Hibernate**
   ```sql
   INSERT INTO treinador (nome) VALUES ('Ash Ketchum');
   INSERT INTO treinador (nome) VALUES ('Misty');
   -- etc...
   ```

#### **Seeders com Lógica Complexa**

**Exemplo: PokemonSeeder (Relacionamentos Múltiplos)**

1. **Busca Dados de Dependências**

   ```java
   List<Treinador> treinadores = treinadorService.listarTodos();
   List<Especie> especies = especieService.listarTodos();
   List<Ataque> ataques = ataqueService.listarTodos();
   ```

2. **Geração Inteligente de Dados**

   ```java
   for (Treinador treinador : treinadores) {
       for (int i = 1; i <= 6; i++) {
           // Escolhe espécie aleatória
           Especie especie = especies.get(random.nextInt(especies.size()));

           // Gera apelido único
           String apelido = especie.getNome() + " de " +
                           treinador.getNome() + " #" + i;

           // Cria pokémon
           Pokemon pokemon = new Pokemon(apelido, especie, treinador);

           // Adiciona 2-4 ataques compatíveis com os tipos
           List<Ataque> ataquesCompativeis =
               buscarAtaquesCompativeis(especie, ataques);
           int qtdAtaques = 2 + random.nextInt(3); // 2 a 4
           for (int j = 0; j < qtdAtaques && j < ataquesCompativeis.size(); j++) {
               pokemon.addAtaque(ataquesCompativeis.get(j));
           }

           pokemonService.salvar(pokemon);
       }
   }
   ```

3. **Transações e Lazy Loading**
   ```java
   @Transactional // Mantém sessão Hibernate aberta
   public void seed() {
       // Acessa relacionamentos lazy (especie.getTipos())
       // sem LazyInitializationException
   }
   ```

#### **Otimizações Implementadas**

- **Delay entre requisições**: 100ms para evitar rate limiting da PokeAPI
- **Buffer aumentado**: WebClient com 10MB buffer para JSONs grandes
- **Capitalização consistente**: `capitalizeFirst()` para nomes de tipos (Normal vs normal)
- **Validação de dados**: Antes de salvar, verifica integridade (ex: time não pode ter >6 pokémons)
- **Logs informativos**: Progresso em tempo real com símbolos (✓ criado, ↷ já existe, ⚠️ erro)

#### **Tecnologias Utilizadas**

| Tecnologia            | Função                                       |
| --------------------- | -------------------------------------------- |
| **Spring Boot 3.2.2** | Framework base, injeção de dependências      |
| **WebClient**         | Cliente HTTP reativo para chamadas à PokeAPI |
| **Jackson**           | Deserialização JSON → Objetos Java (DTOs)    |
| **JPA/Hibernate**     | ORM para mapeamento objeto-relacional        |
| **PostgreSQL JDBC**   | Driver de conexão com o banco                |
| **Lombok**            | Redução de boilerplate (getters/setters)     |

### �📝 Detalhes dos Seeders

#### **1. TipoSeeder**

- Busca os 18 tipos oficiais da PokeAPI
- Capitaliza nomes (Normal, Fire, Water...)
- Evita duplicações

#### **2. EspecieSeeder**

- Busca primeiros 50 Pokémon da PokéAPI
- Extrai sprites da geração 5 (black-white)
- Vincula tipos da geração 6 (ou atual)

#### **3. AtaqueSeeder**

- Busca primeiros 50 movimentos da PokéAPI
- Extrai: nome, categoria (Physical/Special/Status), poder
- Vincula ao tipo correto

#### **4. TreinadorSeeder**

Cria 4 treinadores icônicos:

- Ash Ketchum
- Misty
- Brock
- Gary Oak

#### **5. PokemonSeeder**

- Cria 6 pokémons para cada treinador (24 total)
- Gera apelidos únicos: "Pikachu de Ash #1"
- Adiciona 2-4 ataques compatíveis com os tipos do Pokémon

#### **6. TimeSeeder**

- Cria 1 time para cada treinador (4 total)
- Nomes temáticos: "Time Campeão de Ash", "Time Cascata de Misty"...
- Adiciona os 6 pokémons do treinador ao time

#### **7. TorneioSeeder**

Cria 2 torneios:

- **Liga Kanto Regional** (Janeiro/2026)
- **Copa dos Mestres** (Fevereiro/2026)
- Todos os 4 times participam de ambos

#### **8. BatalhaSeeder**

Cria 6 batalhas (3 por torneio):

**Liga Kanto Regional:**

- Semifinal 1: Ash vs Misty → Ash vence
- Semifinal 2: Brock vs Gary → Gary vence
- Final: Ash vs Gary → **Ash campeão** 🏆

**Copa dos Mestres:**

- Semifinal 1: Ash vs Brock → Brock vence
- Semifinal 2: Misty vs Gary → Misty vence
- Final: Brock vs Misty → **Misty campeã** 🏆

### ⚙️ Configurações Avançadas

**Alterar quantidade de dados:**

Edite os arquivos dos seeders individuais:

```java
// EspecieSeeder.java
especieSeeder.seed(50);  // Altere para 100, 150...

// AtaqueSeeder.java
ataqueSeeder.seed(50);   // Altere para 100, 150...
```

**Adicionar mais treinadores/times:**

Edite `TreinadorSeeder.java` e adicione na lista `TREINADORES`.

### 🔍 Verificar Dados Populados

Após executar os seeders, verifique com Prisma Studio:

```bash
cd backend/db-schema
npx prisma studio
```

Ou consulte diretamente no PostgreSQL:

```bash
psql -h localhost -p 5433 -U dev -d projeto_banquinho
```

### 📚 Documentação Adicional

Para mais detalhes técnicos sobre os seeders, consulte:

- [README dos Seeders](backend/src/main/java/com/ufape/projetobanquinhobd/seeder/README.md)

### ⚠️ Observações Importantes

- **`spring.jpa.hibernate.ddl-auto=create`**: O banco é **recriado** a cada execução, então os dados são perdidos. Execute o SeedRunner após reiniciar a aplicação.
- **Delay entre requisições**: 100ms entre chamadas à PokeAPI para evitar rate limiting
- **Idempotência**: Os seeders podem ser executados múltiplas vezes sem causar duplicações (usam upsert quando possível)

## Links uteis

- API: https://pokeapi.co/
- Documentacao: https://pokeapi.co/docs/v2
