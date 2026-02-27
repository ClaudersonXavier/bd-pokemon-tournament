# 📚 Seeds do Banco de Dados

- Este módulo contém os seeders para popular o banco de dados com dados iniciais.
---

## 🎯 Seeders Disponíveis

### 1. Seed de Tipos (TipoSeeder)

Popula o banco com os 18 tipos de pokémon da PokeAPI.

**Arquivo:** `SeedTipoRunner.java`  
**O que faz:**

- Busca lista de tipos da PokeAPI (endpoint `/type?limit=18`)
- Para cada tipo:
  - Verifica se já existe no banco
  - Se não existir, cria e salva
  - Se já existir, pula

**⚠️ Importante:** Executar ANTES do seed de Espécies, pois Espécie depende de Tipo.

### 2. Seed de Espécies (EspecieSeeder)

Popula o banco com espécies de pokémons da PokeAPI.

**Arquivo:** `SeedEspecieRunner.java`  
**O que faz:**

- Busca lista de pokémons da PokeAPI
- Para cada pokémon:
  - Extrai nome
  - Extrai **imagem da 5ª geração** (black-white)
  - Extrai **tipos da 6ª geração** (ou atual se não houver)
  - Cria tipos automaticamente se não existirem
  - Salva a espécie no banco

### 3. Seed de Ataques (AtaqueSeeder)

Popula o banco com movimentos/ataques da PokeAPI.

**Arquivo:** `SeedAtaqueRunner.java`  
**O que faz:**

- Busca lista de movimentos da PokeAPI
- Para cada ataque:
  - Extrai nome, categoria (Physical/Special/Status) e poder
  - Vincula ao tipo correto
  - Salva no banco

### 4. Seed de Treinadores (TreinadorSeeder)

Popula o banco com treinadores icônicos.

**Arquivo:** `SeedTreinadorRunner.java`  
**O que faz:**

- Cria treinadores mockados (Ash Ketchum, Misty, Brock, Gary Oak)
- Inclui credenciais embutidas para login

### 5. Seed de Pokémons (PokemonSeeder)

Popula o banco com pokémons de cada treinador.

**Arquivo:** `SeedPokemonRunner.java`  
**O que faz:**

- Cria 6 pokémons para cada treinador (24 total)
- Gera apelidos únicos
- Adiciona 2-4 ataques compatíveis com os tipos do pokémon

### 6. Seed de Times (TimeSeeder)

Popula o banco com times competitivos.

**Arquivo:** `SeedTimeRunner.java`  
**O que faz:**

- Cria 1 time para cada treinador (4 total)
- Nomes temáticos baseados no treinador
- Adiciona os 6 pokémons do treinador ao time

### 7. Seed de Torneios (TorneioSeeder)

Popula o banco com torneios.

**Arquivo:** `SeedTorneioRunner.java`  
**O que faz:**

- Cria 2 torneios (Liga Kanto Regional, Copa dos Mestres)
- Define datas e status
- Todos os 4 times participam de ambos

### 8. Seed de Batalhas (BatalhaSeeder)

Popula o banco com batalhas entre times.

**Arquivo:** `SeedBatalhaRunner.java`  
**O que faz:**

- Cria 6 batalhas (3 por torneio)
- Define vencedores e resultados
- Simula semifinais e finais

## 🚀 Como Executar

### Opção A: Executar TODOS os seeds (Recomendado)

```bash
# Via IDE
- Abra SeedRunner.java
- Run 'SeedRunner.main()'
```

### Opção B: Executar seed ESPECÍFICO

```bash
# Apenas tipos (executar primeiro!)
- Abra SeedTipoRunner.java
- Run 'SeedTipoRunner.main()'

# Apenas espécies
- Abra SeedEspecieRunner.java
- Run 'SeedEspecieRunner.main()'
```

### Configurações

- **Quantidade de espécies:** Edite `SeedRunner.java` linha com `especieSeeder.seed(50);`
- **Quantidade de ataques:** Edite `SeedRunner.java` linha com `ataqueSeeder.seed(50);`
- **Outros seeders:** Adicione no `SeedRunner.java` conforme criados

## 📁 Estrutura

```
seeder/
├── README.md                       # Este arquivo
├── SeedConfiguration.java          # Configuração Spring
├── SeedRunner.java                 # ⭐ Seed GERAL (orquestra todos)
│
├── tipos/
│   ├── TipoSeeder.java            # Lógica do seed de tipos
│   └── SeedTipoRunner.java        # Runner individual
│
├── especies/
│   ├── EspecieSeeder.java         # Lógica do seed de espécies
│   └── SeedEspecieRunner.java     # Runner individual
│
├── ataques/
│   ├── AtaqueSeeder.java          # Lógica do seed de ataques
│   └── SeedAtaqueRunner.java      # Runner individual
│
├── treinadores/
│   ├── TreinadorSeeder.java       # Lógica do seed de treinadores
│   └── SeedTreinadorRunner.java   # Runner individual
│
├── pokemons/
│   ├── PokemonSeeder.java         # Lógica do seed de pokémons
│   └── SeedPokemonRunner.java     # Runner individual
│
├── times/
│   ├── TimeSeeder.java            # Lógica do seed de times
│   └── SeedTimeRunner.java        # Runner individual
│
├── torneios/
│   ├── TorneioSeeder.java         # Lógica do seed de torneios
│   └── SeedTorneioRunner.java     # Runner individual
│
├── batalhas/
│   ├── BatalhaSeeder.java         # Lógica do seed de batalhas
│   └── SeedBatalhaRunner.java     # Runner individual
│
└── dtos/
    ├── TypeListResponse.java       # DTO para lista de tipos
    ├── PokemonListResponse.java    # DTO para lista de pokémons
    ├── PokemonDetailResponse.java  # DTO para detalhes do pokémon
    └── MoveListResponse.java       # DTO para lista de movimentos
```

## ⚙️ Observações

- Delay de 100ms entre requisições para não sobrecarregar a API
- Imagens: tenta gen 5 primeiro, fallback para padrão
- Tipos: tenta gen 6 primeiro, fallback para atual
- Tipos são criados automaticamente se não existirem
- Seeds podem ser executados múltiplas vezes (usa `save` que faz upsert)
- Limite de buffer do WebClient: 10MB (para JSONs grandes da PokeAPI)

## 🔗 Links Úteis

- **PokeAPI:** https://pokeapi.co/
- **Docs PokeAPI:** https://pokeapi.co/docs/v2

---

