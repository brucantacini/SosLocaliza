package com.example.SosLocaliza.services;

import com.example.SosLocaliza.domains.Evento;
import com.example.SosLocaliza.domains.SmsMessage;
import com.example.SosLocaliza.exceptions.EventoNotFoundException;
import com.example.SosLocaliza.exceptions.SmsNotFoundException;
import com.example.SosLocaliza.gateways.dtos.request.SmsRequestDto;
import com.example.SosLocaliza.gateways.dtos.request.SmsUpdateDto;
import com.example.SosLocaliza.gateways.EventoRepository;
import com.example.SosLocaliza.gateways.SmsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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

    /**
     * Valida o pedido, simula o envio do SMS (sem provedor externo) e persiste no Oracle.
     */
    public SmsMessage registrarEnvioComEvento(SmsRequestDto smsRequestDto, Long idEvento) {
        SmsMessage smsMessage = smsRequestDto.toSmsMessage();

        if (!isValidPhoneNumber(smsMessage.getNumeroTelefone())) {
            smsMessage = smsMessage.withEnviadoComSucesso(false)
                    .withErro("Número de telefone inválido: " + smsMessage.getNumeroTelefone());
            log.warn("Número de telefone inválido: {}", smsMessage.getNumeroTelefone());
            return enviarSmsComEvento(smsMessage, idEvento);
        }

        try {
            log.info("[SMS] Envio simulado registrado para {}", smsMessage.getNumeroTelefone());
            smsMessage = smsMessage.withEnviadoComSucesso(true).withErro(null);
        } catch (Exception e) {
            smsMessage = smsMessage.withEnviadoComSucesso(false).withErro("Erro ao registrar envio: " + e.getMessage());
            log.error("Erro ao registrar envio de SMS: {}", e.getMessage());
        }

        return enviarSmsComEvento(smsMessage, idEvento);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        if (phoneNumber.startsWith("+55") && phoneNumber.length() >= 13) {
            return true;
        }
        return phoneNumber.startsWith("+") && phoneNumber.length() >= 10;
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
        return smsRepository.findAllWithEvento(pageable);
    }

    public Page<SmsMessage> listarSmsEnviadosComSucessoComPaginacao(Pageable pageable) {
        return smsRepository.findByEnviadoComSucessoTrue(pageable);
    }

    public List<SmsMessage> buscarSmsPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return smsRepository.findByDataEnvioBetween(dataInicio, dataFim);
    }

    public Long contarSmsEnviadosComSucesso() {
        return smsRepository.countSmsEnviadosComSucesso();
    }

    public Long contarSmsComErro() {
        return smsRepository.countSmsComErro();
    }

    public Optional<SmsMessage> buscarUltimoSmsPorNumero(String numeroTelefone) {
        List<SmsMessage> smsList = smsRepository.findByNumeroTelefone(numeroTelefone);
        if (smsList.isEmpty()) {
            return Optional.empty();
        }
        return smsList.stream()
                .sorted((a, b) -> b.getDataEnvio().compareTo(a.getDataEnvio()))
                .findFirst();
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

    public Optional<SmsMessage> buscarSmsPorId(Long idSms) {
        return smsRepository.findById(idSms);
    }

    public SmsMessage atualizarSms(Long idSms, SmsUpdateDto dto) {
        SmsMessage existing = smsRepository.findById(idSms)
                .orElseThrow(() -> new SmsNotFoundException("SMS não encontrado com ID: " + idSms));
        Evento evento = eventoRepository.findById(dto.getIdEvento())
                .orElseThrow(() -> new EventoNotFoundException("Evento não encontrado com ID: " + dto.getIdEvento()));
        String numeroCompleto = "+55" + dto.getDdd() + dto.getNumeroTelefone();
        boolean enviado = dto.getEnviadoComSucesso() != null
                ? dto.getEnviadoComSucesso()
                : Boolean.TRUE.equals(existing.getEnviadoComSucesso());
        SmsMessage merged = existing
                .withRemetente(dto.getRemetente())
                .withDdd(dto.getDdd())
                .withNumeroTelefone(numeroCompleto)
                .withMensagem(dto.getMensagem())
                .withEvento(evento)
                .withEnviadoComSucesso(enviado)
                .withErro(dto.getErro());
        return smsRepository.save(merged);
    }

    public void deletarSms(Long idSms) {
        if (!smsRepository.existsById(idSms)) {
            throw new SmsNotFoundException("SMS não encontrado com ID: " + idSms);
        }
        smsRepository.deleteById(idSms);
    }
}
