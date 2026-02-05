# Seeds do Banco de Dados

Este módulo contém os seeders para popular o banco de dados com dados iniciais.

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

### 3. Outros Seeders (Em desenvolvimento)

- `AtaqueSeeder` - Popular ataques
- `TreinadorSeeder` - Popular treinadores
- etc.

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

- **Quantidade de espécies:** Edite `SeedEspecieRunner.java` linha com `int limit = 50;`
- **Outros seeders:** Adicione no `SeedRunner.java` conforme criados

## 📁 Estrutura

TipoRunner.java # Runner para tipos apenas
├── SeedEspecieRunner.java # Runner para espécies apenas
├── TipoSeeder.java # Lógica do seed de tipos
├── EspecieSeeder.java # Lógica do seed de espécies
└── dtos/
├── TypeListResponse.java # DTO para lista de tipos
├── SeedRunner.java # Seed GERAL (orquestra todos)
├── SeedEspecieRunner.java # Runner para espécies apenas
├── EspecieSeeder.java # Lógica do seed de espécies
└── dtos/
├── PokemonListResponse.java # DTO para lista de pokémons
└── PokemonDetailResponse.java # DTO para detalhes do pokémon

```

## ⚙️ Observações

- Delay de 100ms entre requisições para não sobrecarregar a API
- Imagens: tenta gen 5 primeiro, fallback para padrão
- Tipos: tenta gen 6 primeiro, fallback para atual
- Tipos são criados automaticamente se não existirem
- Seeds podem ser executados múltiplas vezes (usa `save` que faz upsert)
- Limite de buffer do WebClient: 10MB (para JSONs grandes da PokeAPI)
```
