package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.domains.SmsMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

    // Repositório para operações com a entidade SmsMessage
    // Seguindo padrões do Spring Data JPA

public interface SmsRepository extends JpaRepository<SmsMessage, Long> {

    // Busca SMS por número de telefone
    List<SmsMessage> findByNumeroTelefone(String numeroTelefone);

    // Busca SMS por remetente
    List<SmsMessage> findByRemetente(String remetente);

    // Busca SMS enviados com sucesso
    List<SmsMessage> findByEnviadoComSucessoTrue();

    // Busca SMS com erro
    List<SmsMessage> findByEnviadoComSucessoFalse();

// Busca SMS por evento relacionado
    List<SmsMessage> findByEventoIdEvento(Long idEvento);

    // Busca SMS por DDD
    List<SmsMessage> findByDdd(String ddd);

    // Busca SMS com paginação
    Page<SmsMessage> findAll(Pageable pageable);

    // Busca SMS enviados com sucesso com paginação
    Page<SmsMessage> findByEnviadoComSucessoTrue(Pageable pageable);

    // Busca SMS por período de envio
    @Query("SELECT s FROM SmsMessage s WHERE s.dataEnvio BETWEEN :dataInicio AND :dataFim ORDER BY s.dataEnvio DESC")
    List<SmsMessage> findByDataEnvioBetween(@Param("dataInicio") LocalDateTime dataInicio, 
                                          @Param("dataFim") LocalDateTime dataFim);

    // Busca SMS com erro por período
    @Query("SELECT s FROM SmsMessage s WHERE s.enviadoComSucesso = false AND s.dataEnvio BETWEEN :dataInicio AND :dataFim")
    List<SmsMessage> findSmsComErroPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio, 
                                              @Param("dataFim") LocalDateTime dataFim);

    // Conta SMS enviados com sucesso
    @Query("SELECT COUNT(s) FROM SmsMessage s WHERE s.enviadoComSucesso = true")
    Long countSmsEnviadosComSucesso();

    // Conta SMS com erro
    @Query("SELECT COUNT(s) FROM SmsMessage s WHERE s.enviadoComSucesso = false")
    Long countSmsComErro();

    // Busca SMS por evento e status
    @Query("SELECT s FROM SmsMessage s WHERE s.evento.idEvento = :idEvento AND s.enviadoComSucesso = :sucesso")
    List<SmsMessage> findByEventoAndStatus(@Param("idEvento") Long idEvento, @Param("sucesso") Boolean sucesso);

    // Busca último SMS enviado para um número
    @Query("SELECT s FROM SmsMessage s WHERE s.numeroTelefone = :numero ORDER BY s.dataEnvio DESC")
    Optional<SmsMessage> findUltimoSmsPorNumero(@Param("numero") String numero);
}
