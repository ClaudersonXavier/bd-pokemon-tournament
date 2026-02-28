# 🌱 Explicação de Como os Dados do Banco Foram Povoados

<div align="center">

**[🏠 Início](../README.md)** • **[🧠 Esquema do BD](esquema-bd.md)** • **[📚 Dicionário de Dados](dicionario-de-dados.md)** • **[📊 Views SQL](views-sql.md)**

</div>

---

## Índice

- [Sistema de Seeders](#-sistema-de-seeders)
- [Como Popular o Banco de Dados](#-como-popular-o-banco-de-dados)
- [Dados Populados](#-dados-populados)
- [Ordem de Execução](#-ordem-de-execução-dependências)
- [Como os Seeders Funcionam Tecnicamente](#-como-os-seeders-funcionam-tecnicamente)
- [Detalhes dos Seeders](#-detalhes-dos-seeders)
- [Configurações Avançadas](#-configurações-avançadas)
- [Verificar Dados Populados](#-verificar-dados-populados)
- [Observações Importantes](#-observações-importantes)

---

## 📦 Sistema de Seeders

O projeto utiliza **seeders automatizados** para popular o banco de dados com dados realistas usando a [PokeAPI](https://pokeapi.co/) e dados mockados. Os seeders seguem uma ordem de dependências para garantir integridade referencial.

---

## 🚀 Como Popular o Banco de Dados

> ⏱️ **Tempo estimado:** A execução completa do SeedRunner leva aproximadamente 30-60 segundos, dependendo da conexão com a PokeAPI.

### **Opção 1: Seed Completo (Recomendado)**

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

### **Opção 2: Seeders Individuais**

Para popular apenas uma tabela específica, execute o runner correspondente:

```bash
# Tipos apenas
Run 'SeedTipoRunner.main()'

# Espécies apenas (requer Tipos já populados)
Run 'SeedEspecieRunner.main()'

# E assim por diante...
```

---

## 📊 Dados Populados

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

---

## 🔄 Ordem de Execução (Dependências)

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

---

## 🛠️ Como os Seeders Funcionam Tecnicamente

Os seeders utilizam uma arquitetura modular com Spring Boot, JPA/Hibernate e WebClient para comunicação HTTP reativa. Aqui está o fluxo completo:

### **Fluxo de Dados: Da API ao Banco**

#### **Exemplo: EspecieSeeder (com PokeAPI)**

**1. Requisição HTTP (WebClient)**

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

**2. Deserialização JSON → Objeto Java (DTO)**

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

**3. Busca Detalhes de Cada Pokémon**

```java
// Para cada pokémon, faz nova requisição:
GET https://pokeapi.co/api/v2/pokemon/25/

PokemonDetailResponse details = webClient.get()
    .uri("/pokemon/25")
    .retrieve()
    .bodyToMono(PokemonDetailResponse.class)
    .block();
```

**4. Extração de Dados do JSON Complexo**

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

**5. Transformação: DTO → Entidade JPA**

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

**6. Persistência no Banco (JPA/Hibernate)**

```java
// Salva usando service layer
especieService.salvar(especie);

// Hibernate gera SQL automaticamente:
INSERT INTO especie (nome, imagem_url)
VALUES ('pikachu', 'https://...pikachu.png');

INSERT INTO especie_tipos (especie_nome, tipos_nome)
VALUES ('pikachu', 'Electric');
```

**7. Controle de Duplicação**

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

---

#### **Seeders sem API Externa (Mockados)**

**Exemplo: TreinadorSeeder**

**1. Lista Hardcoded no Código**

```java
private static final List<String> TREINADORES = List.of(
    "Ash Ketchum",
    "Misty",
    "Brock",
    "Gary Oak"
);
```

**2. Loop de Criação**

```java
for (String nome : TREINADORES) {
    Treinador treinador = new Treinador(nome);
    treinadorService.salvar(treinador);
    System.out.println("✓ Treinador criado: " + nome);
}
```

**3. SQL Gerado pelo Hibernate**

```sql
INSERT INTO treinador (nome) VALUES ('Ash Ketchum');
INSERT INTO treinador (nome) VALUES ('Misty');
-- etc...
```

---

#### **Seeders com Lógica Complexa**

**Exemplo: PokemonSeeder (Relacionamentos Múltiplos)**

**1. Busca Dados de Dependências**

```java
List<Treinador> treinadores = treinadorService.listarTodos();
List<Especie> especies = especieService.listarTodos();
List<Ataque> ataques = ataqueService.listarTodos();
```

**2. Geração Inteligente de Dados**

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

**3. Transações e Lazy Loading**

```java
@Transactional // Mantém sessão Hibernate aberta
public void seed() {
    // Acessa relacionamentos lazy (especie.getTipos())
    // sem LazyInitializationException
}
```

---

## 📝 Detalhes dos Seeders

### **1. TipoSeeder**
- Busca os 18 tipos oficiais da PokeAPI
- Capitaliza nomes (Normal, Fire, Water...)
- Evita duplicações

### **2. EspecieSeeder**
- Busca primeiros 50 Pokémon da PokéAPI
- Extrai sprites da geração 5 (black-white)
- Vincula tipos da geração 6 (ou atual)

### **3. AtaqueSeeder**
- Busca primeiros 50 movimentos da PokéAPI
- Extrai: nome, categoria (Physical/Special/Status), poder
- Vincula ao tipo correto

### **4. TreinadorSeeder**
Cria 4 treinadores icônicos:
- Ash Ketchum
- Misty
- Brock
- Gary Oak

### **5. PokemonSeeder**
- Cria 6 pokémons para cada treinador (24 total)
- Gera apelidos únicos: "Pikachu de Ash #1"
- Adiciona 2-4 ataques compatíveis com os tipos do Pokémon

### **6. TimeSeeder**
- Cria 1 time para cada treinador (4 total)
- Nomes temáticos: "Time Campeão de Ash", "Time Cascata de Misty"...
- Adiciona os 6 pokémons do treinador ao time

### **7. TorneioSeeder**
Cria 2 torneios:
- **Liga Kanto Regional** (Janeiro/2026)
- **Copa dos Mestres** (Fevereiro/2026)
- Todos os 4 times participam de ambos

### **8. BatalhaSeeder**
Cria 6 batalhas (3 por torneio):

**Liga Kanto Regional:**
- Semifinal 1: Ash vs Misty → Ash vence
- Semifinal 2: Brock vs Gary → Gary vence
- Final: Ash vs Gary → **Ash campeão** 🏆

**Copa dos Mestres:**
- Semifinal 1: Ash vs Brock → Brock vence
- Semifinal 2: Misty vs Gary → Misty vence
- Final: Brock vs Misty → **Misty campeã** 🏆

---

## ⚙️ Configurações Avançadas

### **Alterar quantidade de dados:**

Edite os arquivos dos seeders individuais:

```java
// EspecieSeeder.java
especieSeeder.seed(50);  // Altere para 100, 150...

// AtaqueSeeder.java
ataqueSeeder.seed(50);   // Altere para 100, 150...
```

### **Adicionar mais treinadores/times:**

Edite `TreinadorSeeder.java` e adicione na lista `TREINADORES`.

---

## 🔍 Verificar Dados Populados

Após executar os seeders, verifique com Prisma Studio:

```bash
cd backend/db-schema
npx prisma studio
```

Ou consulte diretamente no PostgreSQL:

```bash
psql -h localhost -p 5433 -U dev -d projeto_banquinho
```

---

## 📚 Documentação Adicional

Para mais detalhes técnicos sobre os seeders, consulte:

- [README dos Seeders](../backend/src/main/java/com/ufape/projetobanquinhobd/seeder/README.md)

---

## ⚠️ Observações Importantes

> ⚠️ **Atenção:**  
> - **Delay entre requisições**: 100ms entre chamadas à PokeAPI para evitar rate limiting
> - **Idempotência**: Os seeders podem ser executados múltiplas vezes sem causar duplicações (usam upsert quando possível)

💡 **Dica:**  
Para ambientes de produção, altere `ddl-auto` para `validate` ou `update` para preservar os dados entre reinicializações.

---
---

## 🔗 Links Úteis

- **PokeAPI:** https://pokeapi.co/
- **Documentação da API:** https://pokeapi.co/docs/v2

---

<div align="center">

### 📚 Documentos Relacionados

**[🧠 Esquema BD](esquema-bd.md)** • **[📚 Dicionário de Dados](dicionario-de-dados.md)** • **[📊 Views SQL](views-sql.md)**

---

**[⬆️ Voltar ao topo](#-explicação-de-como-os-dados-do-banco-foram-povoados)** • **[🏠 Voltar ao início](../README.md)**

</div>
