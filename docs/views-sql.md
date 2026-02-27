# 📊 Views SQL

<div align="center">

**[🏠 Início](README.md)** • **[🧠 Esquema do BD](esquema-bd.md)** • **[📚 Dicionário de Dados](dicionario-de-dados.md)** • **[🌱 Povoamento](povoamento-seeders.md)**

</div>

---

## Índice

- [Introdução](#introdução)
- [View 1 — v_resumo_batalhas_torneio](#view-1--v_resumo_batalhas_torneio)
- [View 2 — v_time_pokemons_detalhado](#view-2--v_time_pokemons_detalhado)
- [View 3 — v_treinador_desempenho_torneio](#view-3--v_treinador_desempenho_torneio)
- [Benefícios das Views](#benefícios-das-views)

---

## Introdução

As views foram criadas via migration Prisma e são aplicadas automaticamente ao subir o projeto com `docker compose up`.

**Elas fornecem consultas otimizadas e pré-processadas para:**
- Resumos de batalhas e torneios
- Composição detalhada de times
- Desempenho de treinadores

> **Nota:** Todas as views são materializadas em tempo de consulta, garantindo dados sempre atualizados.

---

## View 1 — `v_resumo_batalhas_torneio`

### **Finalidade**
Apresenta um resumo completo de cada batalha disputada no sistema.

### **Colunas Retornadas**

| Coluna               | Descrição                                    |
| -------------------- | -------------------------------------------- |
| `torneio_id`         | Identificador do torneio                     |
| `torneio_nome`       | Nome do torneio                              |
| `batalha_id`         | ID da batalha                                |
| `rodada`             | Fase da competição (1 = oitavas, 2 = quartas…) |
| `horario_inicio`     | Horário de início da batalha                 |
| `horario_fim`        | Horário de término da batalha                |
| `duracao_minutos`    | Tempo de duração calculado automaticamente   |
| `time_vencedor_id`   | Identificador do time vencedor               |
| `time_vencedor_nome` | Nome do time vencedor do confronto           |

### **Exemplos de Consulta**

```sql
-- Todas as batalhas da Liga Kanto Regional
SELECT * FROM v_resumo_batalhas_torneio
WHERE torneio_nome = 'Liga Kanto Regional';

-- Ranking de batalhas por duração
SELECT torneio_nome, rodada, time_vencedor_nome, duracao_minutos
FROM v_resumo_batalhas_torneio
ORDER BY duracao_minutos DESC;
```

---

## View 2 — `v_time_pokemons_detalhado`

### **Finalidade**
Exibe a composição completa de cada time com todos os Pokémons, suas espécies, imagens e tipos.

### **Colunas Retornadas**

| Coluna                | Descrição                          |
| --------------------- | ---------------------------------- |
| `time_id`             | Identificador do time              |
| `time_nome`           | Nome do time                       |
| `treinador_id`        | Identificador do treinador         |
| `treinador_nome`      | Nome do dono do time               |
| `pokemon_id`          | Identificador do Pokémon           |
| `pokemon_apelido`     | Apelido dado ao Pokémon            |
| `especie_nome`        | Nome da espécie do Pokémon         |
| `especie_imagem_url`  | URL do sprite/imagem da espécie    |
| `tipos`               | Tipos elementais do Pokémon        |

### **Exemplos de Consulta**

```sql
-- Todos os Pokémons do time de Ash
SELECT pokemon_apelido, especie_nome, tipos
FROM v_time_pokemons_detalhado
WHERE treinador_nome = 'Ash Ketchum';

-- Times que possuem Pokémons do tipo Fire
SELECT DISTINCT time_nome, treinador_nome
FROM v_time_pokemons_detalhado
WHERE tipos LIKE '%Fire%';
```

---

## View 3 — `v_treinador_desempenho_torneio`

### **Finalidade**
Apresenta o desempenho de cada treinador por torneio (total de batalhas, vitórias, derrotas e percentual de aproveitamento).

### **Colunas Retornadas**

| Coluna                 | Descrição                             |
| ---------------------- | ------------------------------------- |
| `treinador_id`         | Identificador do treinador            |
| `treinador_nome`       | Nome do treinador                     |
| `torneio_id`           | Identificador do torneio              |
| `torneio_nome`         | Nome do torneio                       |
| `time_id`              | Identificador do time usado           |
| `time_nome`            | Nome do time usado no torneio         |
| `total_batalhas`       | Número de batalhas disputadas         |
| `total_vitorias`       | Número de vitórias obtidas            |
| `total_derrotas`       | Número de derrotas sofridas           |
| `percentual_vitorias`  | Taxa de vitórias em % (arredondado)   |

### **Regra de cálculo**

- `total_batalhas` considera apenas batalhas do time dentro do torneio da linha.
- `total_derrotas` conta somente batalhas finalizadas com vencedor diferente do time.
- `percentual_vitorias` usa `vitorias / batalhas finalizadas`, com proteção para divisão por zero.

### **Exemplos de Consulta**

```sql
-- Ranking geral por percentual de vitórias
SELECT treinador_nome, torneio_nome, total_batalhas,
       total_vitorias, percentual_vitorias
FROM v_treinador_desempenho_torneio
ORDER BY percentual_vitorias DESC;

-- Melhor treinador de cada torneio
SELECT DISTINCT ON (torneio_nome)
    torneio_nome, treinador_nome, percentual_vitorias
FROM v_treinador_desempenho_torneio
ORDER BY torneio_nome, percentual_vitorias DESC;

-- Aproveitamento geral de um treinador em todos os torneios
SELECT treinador_nome,
       SUM(total_batalhas) AS batalhas_totais,
       SUM(total_vitorias) AS vitorias_totais,
       ROUND(SUM(total_vitorias)::NUMERIC / NULLIF(SUM(total_batalhas), 0) * 100, 2) AS aproveitamento_geral
FROM v_treinador_desempenho_torneio
WHERE treinador_nome = 'Ash Ketchum'
GROUP BY treinador_nome;
```

---

## Benefícios das Views

✅ **Performance:** Consultas complexas pré-processadas  
✅ **Simplicidade:** Interface simplificada para queries frequentes  
✅ **Manutenibilidade:** Lógica centralizada e reutilizável  
✅ **Segurança:** Abstração da estrutura interna das tabelas  
✅ **Dados Atualizados:** Views não materializadas refletem mudanças em tempo real

---

<div align="center">

### 📚 Documentos Relacionados

**[🧠 Esquema BD](esquema-bd.md)** • **[📚 Dicionário de Dados](dicionario-de-dados.md)** • **[🌱 Povoamento](povoamento-seeders.md)**

---

**[⬆️ Voltar ao topo](#-views-sql)** • **[🏠 Voltar ao início](README.md)**

</div>
