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

@RestController
@RequestMapping("/api/estatisticas")
@CrossOrigin(origins = "*")
public class EstatisticasController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
