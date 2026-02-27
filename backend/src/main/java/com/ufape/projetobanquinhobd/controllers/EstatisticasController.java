package com.ufape.projetobanquinhobd.controllers;

import com.ufape.projetobanquinhobd.dto.ResumoBatalhaTorneioDTO;
import com.ufape.projetobanquinhobd.dto.TreinadorDesempenhoDTO;
import com.ufape.projetobanquinhobd.dto.TimePokemonDetalhadoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estatisticas")
@CrossOrigin(origins = "*")
public class EstatisticasController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // GET /api/estatisticas/geral
    // Retorna contagens gerais do sistema (treinadores, torneios, batalhas)
    @GetMapping("/geral")
    public ResponseEntity<Map<String, Object>> obterEstatisticasGerais() {
        Long totalTreinadores = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM treinador", Long.class);
        Long totalTorneios = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM torneio", Long.class);
        Long totalBatalhas = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM batalha", Long.class);

        java.util.Map<String, Object> stats = new java.util.LinkedHashMap<>();
        stats.put("totalTreinadores", totalTreinadores != null ? totalTreinadores : 0);
        stats.put("totalTorneios", totalTorneios != null ? totalTorneios : 0);
        stats.put("totalBatalhas", totalBatalhas != null ? totalBatalhas : 0);

        return ResponseEntity.ok(stats);
    }

    // GET /api/estatisticas/top-treinadores
    // Retorna o ranking geral de treinadores agregado por todos os torneios
    @GetMapping("/top-treinadores")
    public ResponseEntity<?> obterTopTreinadores() {
        try {
            List<java.util.Map<String, Object>> top = jdbcTemplate.queryForList(
                "SELECT treinador_id, treinador_nome, " +
                "SUM(total_batalhas) AS total_batalhas, " +
                "SUM(total_vitorias) AS total_vitorias, " +
                "SUM(total_derrotas) AS total_derrotas, " +
                "CASE WHEN SUM(total_batalhas) > 0 " +
                "     THEN ROUND(100.0 * SUM(total_vitorias) / SUM(total_batalhas), 2) " +
                "     ELSE 0 END AS percentual_vitorias " +
                "FROM v_treinador_desempenho_torneio " +
                "GROUP BY treinador_id, treinador_nome " +
                "ORDER BY total_vitorias DESC, percentual_vitorias DESC " +
                "LIMIT 10"
            );
            return ResponseEntity.ok(top);
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }

    // GET /api/estatisticas/torneios-resumo
    // Retorna resumo de torneios com contagem de participantes e batalhas
    @GetMapping("/torneios-resumo")
    public ResponseEntity<?> obterResumoTorneios() {
        try {
            List<java.util.Map<String, Object>> resumo = jdbcTemplate.queryForList(
                "SELECT t.id AS torneio_id, t.nome AS torneio_nome, " +
                "t.data_inicio, t.data_fim, " +
                "COUNT(DISTINCT tt.time_id) AS total_participantes, " +
                "COUNT(DISTINCT b.id) AS total_batalhas " +
                "FROM torneio t " +
                "LEFT JOIN torneio_times tt ON t.id = tt.torneio_id " +
                "LEFT JOIN batalha b ON t.id = b.torneio_id " +
                "GROUP BY t.id, t.nome, t.data_inicio, t.data_fim " +
                "ORDER BY total_batalhas DESC, total_participantes DESC " +
                "LIMIT 10"
            );
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }

    // GET /api/estatisticas/torneio/{torneioId}/desempenho
    // Consulta a view v_treinador_desempenho_torneio
    @GetMapping("/torneio/{torneioId}/desempenho")
    public ResponseEntity<List<TreinadorDesempenhoDTO>> obterDesempenhoTreinadores(@PathVariable Long torneioId) {
        List<TreinadorDesempenhoDTO> desempenho = jdbcTemplate.query(
            "SELECT * FROM v_treinador_desempenho_torneio WHERE torneio_id = ? ORDER BY total_vitorias DESC",
            (rs, rowNum) -> new TreinadorDesempenhoDTO(
                rs.getLong("treinador_id"),
                rs.getString("treinador_nome"),
                rs.getLong("torneio_id"),
                rs.getString("torneio_nome"),
                rs.getLong("time_id"),
                rs.getString("time_nome"),
                rs.getInt("total_batalhas"),
                rs.getInt("total_vitorias"),
                rs.getInt("total_derrotas"),
                rs.getDouble("percentual_vitorias")
            ), torneioId
        );
        return ResponseEntity.ok(desempenho);
    }

    // GET /api/estatisticas/time/{timeId}/pokemons
    // Consulta a view v_time_pokemons_detalhado
    @GetMapping("/time/{timeId}/pokemons")
    public ResponseEntity<List<TimePokemonDetalhadoDTO>> obterPokemonsDoTime(@PathVariable Long timeId) {
        List<TimePokemonDetalhadoDTO> pokemons = jdbcTemplate.query(
            "SELECT * FROM v_time_pokemons_detalhado WHERE time_id = ?",
            (rs, rowNum) -> new TimePokemonDetalhadoDTO(
                rs.getLong("time_id"),
                rs.getString("time_nome"),
                rs.getLong("treinador_id"),
                rs.getString("treinador_nome"),
                rs.getLong("pokemon_id"),
                rs.getString("pokemon_apelido"),
                rs.getString("especie_nome"),
                rs.getString("especie_imagem_url"),
                rs.getString("tipos")
            ), timeId
        );
        return ResponseEntity.ok(pokemons);
    }

    // GET /api/estatisticas/treinador/{treinadorId}/geral
    // Consulta a view v_treinador_desempenho_torneio
    @GetMapping("/treinador/{treinadorId}/geral")
    public ResponseEntity<List<TreinadorDesempenhoDTO>> obterDesempenhoGeralTreinador(@PathVariable Long treinadorId) {
        List<TreinadorDesempenhoDTO> desempenho = jdbcTemplate.query(
            "SELECT * FROM v_treinador_desempenho_torneio WHERE treinador_id = ? ORDER BY torneio_id DESC",
            (rs, rowNum) -> new TreinadorDesempenhoDTO(
                rs.getLong("treinador_id"),
                rs.getString("treinador_nome"),
                rs.getLong("torneio_id"),
                rs.getString("torneio_nome"),
                rs.getLong("time_id"),
                rs.getString("time_nome"),
                rs.getInt("total_batalhas"),
                rs.getInt("total_vitorias"),
                rs.getInt("total_derrotas"),
                rs.getDouble("percentual_vitorias")
            ), treinadorId
        );
        return ResponseEntity.ok(desempenho);
    }

    // GET /api/estatisticas/torneio/{torneioId}/resumo-batalhas
    // Consulta a view v_resumo_batalhas_torneio
    @GetMapping("/torneio/{torneioId}/resumo-batalhas")
    public ResponseEntity<List<ResumoBatalhaTorneioDTO>> obterResumoBatalhasTorneio(@PathVariable Long torneioId) {
        List<ResumoBatalhaTorneioDTO> resumo = jdbcTemplate.query(
            "SELECT * FROM v_resumo_batalhas_torneio WHERE torneio_id = ? ORDER BY rodada DESC, horario_inicio DESC",
            (rs, rowNum) -> {
                Timestamp inicio = rs.getTimestamp("horario_inicio");
                Timestamp fim = rs.getTimestamp("horario_fim");
                return new ResumoBatalhaTorneioDTO(
                    rs.getLong("torneio_id"),
                    rs.getString("torneio_nome"),
                    rs.getLong("batalha_id"),
                    rs.getInt("rodada"),
                    inicio != null ? inicio.toLocalDateTime() : null,
                    fim != null ? fim.toLocalDateTime() : null,
                    rs.getDouble("duracao_minutos"),
                    rs.getObject("time_vencedor_id") != null ? rs.getLong("time_vencedor_id") : null,
                    rs.getString("time_vencedor_nome")
                );
            }, torneioId
        );
        return ResponseEntity.ok(resumo);
    }
}
