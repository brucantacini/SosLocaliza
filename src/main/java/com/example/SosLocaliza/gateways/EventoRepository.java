package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.domains.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByNomeEventoContainingIgnoreCase(String nomeEvento);

    List<Evento> findByAtivoTrue();

    Page<Evento> findByNomeEventoContainingIgnoreCase(String nomeEvento, Pageable pageable);

    @Query("SELECT e FROM Evento e WHERE e.descricaoEvento LIKE %:descricao% AND e.ativo = true")
    List<Evento> findByDescricaoContainingAndAtivoTrue(@Param("descricao") String descricao);

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.ativo = true")
    Long countEventosAtivos();

}
