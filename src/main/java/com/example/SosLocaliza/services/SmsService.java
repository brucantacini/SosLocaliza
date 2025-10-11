package com.example.SosLocaliza.services;

import com.example.SosLocaliza.domains.Evento;
import com.example.SosLocaliza.domains.SmsMessage;
import com.example.SosLocaliza.gateways.EventoRepository;
import com.example.SosLocaliza.gateways.SmsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SmsService {

    private final SmsRepository smsRepository;
    private final EventoRepository eventoRepository;

    public SmsMessage enviarSms(SmsMessage smsMessage) {
        return smsRepository.save(smsMessage);
    }

    public SmsMessage enviarSmsComEvento(SmsMessage smsMessage, Long idEvento) {
        Optional<Evento> eventoOpt = eventoRepository.findById(idEvento);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            smsMessage = smsMessage.withEvento(evento);
        }
        return smsRepository.save(smsMessage);
    }

    public List<SmsMessage> listarTodosSms() {
        return smsRepository.findAll();
    }

    public List<SmsMessage> listarSmsEnviadosComSucesso() {
        return smsRepository.findByEnviadoComSucessoTrue();
    }

    public List<SmsMessage> listarSmsComErro() {
        return smsRepository.findByEnviadoComSucessoFalse();
    }

    public List<SmsMessage> buscarSmsPorNumero(String numeroTelefone) {
        return smsRepository.findByNumeroTelefone(numeroTelefone);
    }

    public List<SmsMessage> buscarSmsPorDdd(String ddd) {
        return smsRepository.findByDdd(ddd);
    }

    public List<SmsMessage> buscarSmsPorEvento(Long idEvento) {
        return smsRepository.findByEventoIdEvento(idEvento);
    }

    public Page<SmsMessage> listarSmsComPaginacao(Pageable pageable) {
        return smsRepository.findAll(pageable);
    }

    public Page<SmsMessage> listarSmsEnviadosComSucessoComPaginacao(Pageable pageable) {
        return smsRepository.findByEnviadoComSucessoTrue(pageable);
    }

    public List<SmsMessage> buscarSmsPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return smsRepository.findByDataEnvioBetween(dataInicio, dataFim);
    }

    public List<SmsMessage> buscarSmsComErroPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return smsRepository.findSmsComErroPorPeriodo(dataInicio, dataFim);
    }

    public Long contarSmsEnviadosComSucesso() {
        return smsRepository.countSmsEnviadosComSucesso();
    }

    public Long contarSmsComErro() {
        return smsRepository.countSmsComErro();
    }

    public Optional<SmsMessage> buscarUltimoSmsPorNumero(String numero) {
        return smsRepository.findUltimoSmsPorNumero(numero);
    }

    public List<SmsMessage> buscarSmsPorEventoEStatus(Long idEvento, Boolean sucesso) {
        return smsRepository.findByEventoAndStatus(idEvento, sucesso);
    }

    public SmsMessage marcarSmsComoEnviado(Long idSms) {
        Optional<SmsMessage> smsOpt = smsRepository.findById(idSms);
        if (smsOpt.isPresent()) {
            SmsMessage sms = smsOpt.get();
            sms = sms.withEnviadoComSucesso(true).withErro(null);
            return smsRepository.save(sms);
        }
        return null;
    }

    public SmsMessage marcarSmsComoErro(Long idSms, String erro) {
        Optional<SmsMessage> smsOpt = smsRepository.findById(idSms);
        if (smsOpt.isPresent()) {
            SmsMessage sms = smsOpt.get();
            sms = sms.withEnviadoComSucesso(false).withErro(erro);
            return smsRepository.save(sms);
        }
        return null;
    }
}


