package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.domains.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, String> {

     // Busca eventos por nome 
    List<Evento> findByNomeEventoContainingIgnoreCase(String nomeEvento);

    // Busca eventos ativos
    List<Evento> findByAtivoTrue();

    // Busca eventos inativos
    List<Evento> findByAtivoFalse();

    // Busca eventos por nome com paginação
    Page<Evento> findByNomeEventoContainingIgnoreCase(String nomeEvento, Pageable pageable);

    // Busca eventos ativos com paginação
    Page<Evento> findByAtivoTrue(Pageable pageable);

    // Busca evento por ID e nome
    @Query(value = "SELECT * FROM T_SOS_EVENTO e WHERE e.ID_EVENTO = :id AND e.NOME_EVENTO = :nome", nativeQuery = true)
    Optional<Evento> findByIdAndNomeEvento(@Param("id") String id, @Param("nome") String nomeEvento);

    // Busca eventos por descrição
    @Query("SELECT e FROM Evento e WHERE e.descricaoEvento LIKE %:descricao% AND e.ativo = true")
    List<Evento> findByDescricaoContainingAndAtivoTrue(@Param("descricao") String descricao);

    // Conta eventos ativos
    @Query("SELECT COUNT(e) FROM Evento e WHERE e.ativo = true")
    Long countEventosAtivos();

    // Busca eventos criados em um período
    @Query("SELECT e FROM Evento e WHERE e.dataCriacao BETWEEN :dataInicio AND :dataFim ORDER BY e.dataCriacao DESC")
    List<Evento> findByDataCriacaoBetween(@Param("dataInicio") java.time.LocalDateTime dataInicio, 
                                         @Param("dataFim") java.time.LocalDateTime dataFim);
}
