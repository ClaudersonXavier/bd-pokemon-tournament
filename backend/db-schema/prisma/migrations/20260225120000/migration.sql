CREATE VIEW v_resumo_batalhas_torneio AS
SELECT
    t.id                                                              AS torneio_id,
    t.nome                                                            AS torneio_nome,
    b.id                                                              AS batalha_id,
    b.rodada,
    b.horario_inicio,
    b.horario_fim,
    ROUND(
        EXTRACT(EPOCH FROM (b.horario_fim - b.horario_inicio)) / 60.0,
        2
    )                                                                 AS duracao_minutos,
    tv.id                                                             AS time_vencedor_id,
    tv.nome                                                           AS time_vencedor_nome
FROM torneio t
JOIN batalha b
    ON b.torneio_id = t.id
LEFT JOIN time tv
    ON tv.id = b.time_vencedor_id
GROUP BY
    t.id, t.nome,
    b.id, b.rodada, b.horario_inicio, b.horario_fim,
    tv.id, tv.nome;

CREATE VIEW v_time_pokemons_detalhado AS
SELECT
    tm.id                                                             AS time_id,
    tm.nome                                                           AS time_nome,
    tr.id                                                             AS treinador_id,
    tr.nome                                                           AS treinador_nome,
    p.id                                                              AS pokemon_id,
    p.apelido                                                         AS pokemon_apelido,
    e.nome                                                            AS especie_nome,
    e.imagem_url                                                      AS especie_imagem_url,
    STRING_AGG(DISTINCT et.tipos_nome, ', ' ORDER BY et.tipos_nome)   AS tipos
FROM time tm
JOIN treinador tr
    ON tr.id = tm.treinador_id
JOIN time_pokemons tp
    ON tp.time_id = tm.id
JOIN pokemon p
    ON p.id = tp.pokemons_id
LEFT JOIN especie e
    ON e.nome = p.especie_nome
LEFT JOIN especie_tipos et
    ON et.especie_nome = e.nome
GROUP BY
    tm.id, tm.nome,
    tr.id, tr.nome,
    p.id, p.apelido,
    e.nome, e.imagem_url;e;

CREATE VIEW v_treinador_desempenho_torneio AS
SELECT
    tr.id                                                             AS treinador_id,
    tr.nome                                                           AS treinador_nome,
    tn.id                                                             AS torneio_id,
    tn.nome                                                           AS torneio_nome,
    tm.id                                                             AS time_id,
    tm.nome                                                           AS time_nome,
    COUNT(DISTINCT btp.batalha_id)                                    AS total_batalhas,
    COUNT(DISTINCT CASE
        WHEN b.time_vencedor_id = tm.id THEN b.id
    END)                                                              AS total_vitorias,
    COUNT(DISTINCT btp.batalha_id)
        - COUNT(DISTINCT CASE
            WHEN b.time_vencedor_id = tm.id THEN b.id
          END)                                                         AS total_derrotas,
    ROUND(
        COUNT(DISTINCT CASE
            WHEN b.time_vencedor_id = tm.id THEN b.id
        END)::NUMERIC
        / NULLIF(COUNT(DISTINCT btp.batalha_id), 0) * 100,
        2
    )                                                                 AS percentual_vitorias
FROM treinador tr
JOIN time tm
    ON tm.treinador_id = tr.id
JOIN torneio_times tt
    ON tt.times_id = tm.id
JOIN torneio tn
    ON tn.id = tt.torneios_id
LEFT JOIN batalha_times_participantes btp
    ON btp.times_participantes_id = tm.id
LEFT JOIN batalha b
    ON b.id = btp.batalha_id
   AND b.torneio_id = tn.id
GROUP BY
    tr.id, tr.nome,
    tn.id, tn.nome,
    tm.id, tm.nome;