# ⚙️ Triggers SQL

<div align="center">

**[🏠 Início](../README.md)** • **[🧠 Esquema do BD](esquema-bd.md)** • **[📚 Dicionário de Dados](dicionario-de-dados.md)** • **[📊 Views SQL](views-sql.md)**

</div>

---

## Índice

- [Regras de negócio automatizadas](#regras-de-negócio-automatizadas)
- [Lista de triggers e funções](#lista-de-triggers-e-funções)
- [Roteiro SQL de testes](#roteiro-sql-de-testes)
- [Resultados esperados](#resultados-esperados)
---

## Regras de negócio automatizadas

Os triggers e funções de status do torneio aplicam três regras principais:

- Quando um torneio lota (`max_participantes`), o status muda de `ABERTO` para `EM_ANDAMENTO`.
- Quando existe campeão definido (apenas um time sem derrotas), o status muda para `ENCERRADO`.
- Quando datas já passaram, a função por data corrige status de torneios que ficaram desatualizados.

---

## Lista de triggers e funções

| Nome | Tipo | Execução | Efeito |
| --- | --- | --- | --- |
| `verificar_capacidade_torneio()` | Função de trigger | Após inserir em `torneio_times` | Atualiza `torneio.status` para `EM_ANDAMENTO` ao atingir capacidade |
| `trg_verificar_capacidade` | Trigger | `AFTER INSERT` em `torneio_times` | Chama `verificar_capacidade_torneio()` |
| `verificar_campeao_torneio()` | Função de trigger | Após update de vencedor em `batalha` | Atualiza `torneio.status` para `ENCERRADO` quando há campeão |
| `trg_verificar_campeao` | Trigger | `AFTER UPDATE` em `batalha` (quando `time_vencedor_id` muda) | Chama `verificar_campeao_torneio()` |
| `atualizar_status_por_datas()` | Função manual/agendada | Chamada explícita (`SELECT atualizar_status_por_datas();`) | Ajusta status para `EM_ANDAMENTO`/`ENCERRADO` com base em `data_inicio` e `data_fim` |

---

## Roteiro SQL de testes

Execute no PostgreSQL (`docker compose exec db psql -U dev -d projeto_banquinho`).

### 1) Preparação de dados de teste

```sql
-- Torneio de teste com 2 vagas
INSERT INTO torneio (
  nome,
  max_participantes,
  data_abertura_inscricoes,
  data_encerramento_inscricoes,
  data_inicio,
  data_fim,
  status
)
VALUES (
  'Torneio Trigger Teste',
  2,
  NOW() - INTERVAL '2 day',
  NOW() + INTERVAL '2 day',
  NOW() + INTERVAL '1 day',
  NOW() + INTERVAL '7 day',
  'ABERTO'
)
RETURNING id;

-- Escolha 2 times existentes para o teste
SELECT id, nome FROM time ORDER BY id LIMIT 2;
```

### 2) Teste do trigger de capacidade (`trg_verificar_capacidade`)

```sql
-- Troque <TORNEIO_ID>, <TIME_1> e <TIME_2> pelos IDs retornados
INSERT INTO torneio_times (times_id, torneios_id)
VALUES (<TIME_1>, <TORNEIO_ID>);

SELECT id, nome, status, max_participantes
FROM torneio
WHERE id = <TORNEIO_ID>;

INSERT INTO torneio_times (times_id, torneios_id)
VALUES (<TIME_2>, <TORNEIO_ID>);

SELECT id, nome, status, max_participantes
FROM torneio
WHERE id = <TORNEIO_ID>;
```

### 3) Teste do trigger de campeão (`trg_verificar_campeao`)

```sql
-- Criar batalha sem vencedor
INSERT INTO batalha (
  rodada,
  horario_inicio,
  horario_fim,
  torneio_id,
  time_vencedor_id
)
VALUES (
  1,
  NOW(),
  NOW() + INTERVAL '30 minute',
  <TORNEIO_ID>,
  NULL
)
RETURNING id;

-- Associar os 2 times participantes na batalha
INSERT INTO batalha_times_participantes (batalha_id, times_participantes_id)
VALUES
  (<BATALHA_ID>, <TIME_1>),
  (<BATALHA_ID>, <TIME_2>);

-- Garante pré-condição para o trigger de campeão
UPDATE torneio
SET status = 'EM_ANDAMENTO'
WHERE id = <TORNEIO_ID>;

-- Definir vencedor da batalha (dispara trg_verificar_campeao)
UPDATE batalha
SET time_vencedor_id = <TIME_1>
WHERE id = <BATALHA_ID>;

SELECT id, nome, status
FROM torneio
WHERE id = <TORNEIO_ID>;
```

### 4) Teste da função por data (`atualizar_status_por_datas`)

```sql
-- Cenário A: deve virar EM_ANDAMENTO
UPDATE torneio
SET status = 'ABERTO',
    data_inicio = NOW() - INTERVAL '1 day',
    data_fim = NOW() + INTERVAL '1 day'
WHERE id = <TORNEIO_ID>;

SELECT atualizar_status_por_datas();

SELECT id, nome, status
FROM torneio
WHERE id = <TORNEIO_ID>;

-- Cenário B: deve virar ENCERRADO
UPDATE torneio
SET status = 'EM_ANDAMENTO',
    data_fim = NOW() - INTERVAL '1 day'
WHERE id = <TORNEIO_ID>;

SELECT atualizar_status_por_datas();

SELECT id, nome, status
FROM torneio
WHERE id = <TORNEIO_ID>;
```

---

## Resultados esperados

- Após o 1o `INSERT` em `torneio_times`, o torneio permanece `ABERTO`.
- Após o 2o `INSERT` em `torneio_times` (lotação), o torneio muda para `EM_ANDAMENTO`.
- Após definir `time_vencedor_id` na batalha, o torneio muda para `ENCERRADO`.
- No cenário A da função por data, o torneio fica `EM_ANDAMENTO`.
- No cenário B da função por data, o torneio fica `ENCERRADO`.

---

<div align="center">

**[⬆️ Voltar ao topo](#️-triggers-sql)** • **[🏠 Voltar ao início](../README.md)**

</div>
