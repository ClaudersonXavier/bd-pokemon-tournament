-- ============================================================================
-- TRIGGERS PARA ATUALIZAÇÃO AUTOMÁTICA DO STATUS DOS TORNEIOS
-- ============================================================================

-- ────────────────────────────────────────────────────────────────────────────
-- 1. TRIGGER: Atualizar status quando atingir capacidade máxima
-- ────────────────────────────────────────────────────────────────────────────

-- Função que verifica se o torneio atingiu capacidade máxima
CREATE OR REPLACE FUNCTION verificar_capacidade_torneio()
RETURNS TRIGGER AS $$
DECLARE
    v_total_times INTEGER;
    v_max_participantes INTEGER;
    v_status status_torneio_enum;
BEGIN
    -- Buscar informações do torneio
    SELECT max_participantes, status
    INTO v_max_participantes, v_status
    FROM torneio
    WHERE id = NEW.torneios_id;
    
    -- Contar quantos times estão inscritos no torneio
    SELECT COUNT(*)
    INTO v_total_times
    FROM torneio_times
    WHERE torneios_id = NEW.torneios_id;
    
    -- Se atingiu capacidade máxima e status ainda é ABERTO, atualiza para EM_ANDAMENTO
    IF v_total_times >= v_max_participantes AND v_status = 'ABERTO' THEN
        UPDATE torneio
        SET status = 'EM_ANDAMENTO'
        WHERE id = NEW.torneios_id;
        
        RAISE NOTICE 'Torneio % atingiu capacidade máxima (%). Status atualizado para EM_ANDAMENTO.', 
                     NEW.torneios_id, v_max_participantes;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Criar o trigger na tabela torneio_times
DROP TRIGGER IF EXISTS trg_verificar_capacidade ON torneio_times;

CREATE TRIGGER trg_verificar_capacidade
AFTER INSERT ON torneio_times
FOR EACH ROW
EXECUTE FUNCTION verificar_capacidade_torneio();

-- ────────────────────────────────────────────────────────────────────────────
-- 2. TRIGGER: Encerrar torneio quando houver um campeão
-- ────────────────────────────────────────────────────────────────────────────

-- Função que verifica se há um campeão (todos times exceto um perderam)
CREATE OR REPLACE FUNCTION verificar_campeao_torneio()
RETURNS TRIGGER AS $$
DECLARE
    v_torneio_id BIGINT;
    v_total_times INTEGER;
    v_times_com_vitoria INTEGER;
    v_times_sem_derrota INTEGER;
    v_status status_torneio_enum;
BEGIN
    -- Buscar o ID do torneio e seu status
    SELECT b.torneio_id, t.status
    INTO v_torneio_id, v_status
    FROM batalha b
    INNER JOIN torneio t ON b.torneio_id = t.id
    WHERE b.id = NEW.id;
    
    -- Só processa se o torneio está EM_ANDAMENTO e a batalha tem um vencedor
    IF v_status != 'EM_ANDAMENTO' OR NEW.time_vencedor_id IS NULL THEN
        RETURN NEW;
    END IF;
    
    -- Contar total de times no torneio
    SELECT COUNT(DISTINCT times_id)
    INTO v_total_times
    FROM torneio_times
    WHERE torneios_id = v_torneio_id;
    
    -- Contar times que têm pelo menos uma vitória
    SELECT COUNT(DISTINCT time_vencedor_id)
    INTO v_times_com_vitoria
    FROM batalha
    WHERE torneio_id = v_torneio_id
      AND time_vencedor_id IS NOT NULL;
    
    -- Verificar se existe apenas 1 time invicto (campeão)
    -- Um time é campeão quando:
    -- 1. Tem pelo menos uma vitória
    -- 2. Todos os outros times do torneio perderam pelo menos uma vez
    SELECT COUNT(DISTINCT tt.times_id)
    INTO v_times_sem_derrota
    FROM torneio_times tt
    WHERE tt.torneios_id = v_torneio_id
      AND NOT EXISTS (
          -- Verifica se o time perdeu alguma batalha
          SELECT 1
          FROM batalha b
          INNER JOIN batalha_times_participantes bt ON b.id = bt.batalha_id
          WHERE b.torneio_id = v_torneio_id
            AND bt.times_participantes_id = tt.times_id
            AND b.time_vencedor_id IS NOT NULL
            AND b.time_vencedor_id != tt.times_id
      );
    
    -- Se há exatamente 1 time sem derrotas e ele tem vitórias, temos um campeão!
    IF v_times_sem_derrota = 1 THEN
        -- Verificar se este time invicto tem pelo menos uma vitória
        IF EXISTS (
            SELECT 1
            FROM batalha b
            INNER JOIN torneio_times tt ON b.time_vencedor_id = tt.times_id
            WHERE b.torneio_id = v_torneio_id
              AND b.time_vencedor_id IS NOT NULL
              AND NOT EXISTS (
                  SELECT 1
                  FROM batalha b2
                  INNER JOIN batalha_times_participantes bt ON b2.id = bt.batalha_id
                  WHERE b2.torneio_id = v_torneio_id
                    AND bt.times_participantes_id = tt.times_id
                    AND b2.time_vencedor_id IS NOT NULL
                    AND b2.time_vencedor_id != tt.times_id
              )
        ) THEN
            UPDATE torneio
            SET status = 'ENCERRADO'
            WHERE id = v_torneio_id;
            
            RAISE NOTICE 'Torneio % encerrado! Campeão definido.', v_torneio_id;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Criar o trigger na tabela batalha
DROP TRIGGER IF EXISTS trg_verificar_campeao ON batalha;

CREATE TRIGGER trg_verificar_campeao
AFTER UPDATE ON batalha
FOR EACH ROW
WHEN (OLD.time_vencedor_id IS DISTINCT FROM NEW.time_vencedor_id AND NEW.time_vencedor_id IS NOT NULL)
EXECUTE FUNCTION verificar_campeao_torneio();

-- ────────────────────────────────────────────────────────────────────────────
-- 3. FUNÇÃO: Atualizar status baseado nas datas (executar manualmente ou agendar)
-- ────────────────────────────────────────────────────────────────────────────

-- Função para atualizar status baseado nas datas
-- Esta função pode ser chamada manualmente ou agendada com pg_cron
CREATE OR REPLACE FUNCTION atualizar_status_por_datas()
RETURNS void AS $$
DECLARE
    v_hoje DATE;
    v_torneios_atualizados INTEGER := 0;
BEGIN
    v_hoje := CURRENT_DATE;
    
    -- Atualizar torneios que devem estar EM_ANDAMENTO
    -- (data de início já passou e status ainda é ABERTO)
    UPDATE torneio
    SET status = 'EM_ANDAMENTO'
    WHERE status = 'ABERTO'
      AND data_inicio <= v_hoje
      AND data_fim >= v_hoje;
    
    GET DIAGNOSTICS v_torneios_atualizados = ROW_COUNT;
    
    IF v_torneios_atualizados > 0 THEN
        RAISE NOTICE '% torneio(s) atualizado(s) para EM_ANDAMENTO baseado na data de início', 
                     v_torneios_atualizados;
    END IF;
    
    -- Atualizar torneios que devem estar ENCERRADOS
    -- (data de fim já passou e status não é ENCERRADO)
    UPDATE torneio
    SET status = 'ENCERRADO'
    WHERE status != 'ENCERRADO'
      AND data_fim < v_hoje;
    
    GET DIAGNOSTICS v_torneios_atualizados = ROW_COUNT;
    
    IF v_torneios_atualizados > 0 THEN
        RAISE NOTICE '% torneio(s) atualizado(s) para ENCERRADO baseado na data de fim', 
                     v_torneios_atualizados;
    END IF;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION verificar_capacidade_torneio() IS 
'Trigger function: Atualiza status do torneio para EM_ANDAMENTO quando atingir capacidade máxima de participantes';

COMMENT ON FUNCTION verificar_campeao_torneio() IS 
'Trigger function: Atualiza status do torneio para ENCERRADO quando houver um campeão (apenas um time sem derrotas)';

COMMENT ON FUNCTION atualizar_status_por_datas() IS 
'Função manual/agendada: Atualiza status dos torneios baseado nas datas de início e fim. Execute manualmente ou agende com pg_cron';
