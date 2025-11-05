package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.domains.SmsMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SmsRepository extends JpaRepository<SmsMessage, Long> {

    List<SmsMessage> findByNumeroTelefone(String numeroTelefone);

    List<SmsMessage> findByRemetente(String remetente);

    List<SmsMessage> findByEnviadoComSucessoTrue();

    List<SmsMessage> findByEnviadoComSucessoFalse();

    List<SmsMessage> findByEventoIdEvento(Long idEvento);

    List<SmsMessage> findByDdd(String ddd);

    Page<SmsMessage> findAll(Pageable pageable);

    Page<SmsMessage> findByEnviadoComSucessoTrue(Pageable pageable);

    @Query("SELECT s FROM SmsMessage s WHERE s.dataEnvio BETWEEN :dataInicio AND :dataFim ORDER BY s.dataEnvio DESC")
    List<SmsMessage> findByDataEnvioBetween(@Param("dataInicio") LocalDateTime dataInicio, 
                                          @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT s FROM SmsMessage s WHERE s.enviadoComSucesso = false AND s.dataEnvio BETWEEN :dataInicio AND :dataFim")
    List<SmsMessage> findSmsComErroPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio, 
                                              @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COUNT(s) FROM SmsMessage s WHERE s.enviadoComSucesso = true")
    Long countSmsEnviadosComSucesso();

    @Query("SELECT COUNT(s) FROM SmsMessage s WHERE s.enviadoComSucesso = false")
    Long countSmsComErro();

    @Query("SELECT s FROM SmsMessage s WHERE s.evento.idEvento = :idEvento AND s.enviadoComSucesso = :sucesso")
    List<SmsMessage> findByEventoAndStatus(@Param("idEvento") Long idEvento, @Param("sucesso") Boolean sucesso);

}
