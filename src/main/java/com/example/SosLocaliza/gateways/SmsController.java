package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.domains.SmsMessage;
import com.example.SosLocaliza.gateways.dtos.request.SmsRequestDto;
import com.example.SosLocaliza.gateways.dtos.response.SmsResponseDto;
import com.example.SosLocaliza.services.SmsService;
import com.example.SosLocaliza.services.TwilioSmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;
    private final TwilioSmsService twilioSmsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SmsResponseDto enviarSms(@RequestBody @Valid SmsRequestDto smsRequestDto) {
        SmsMessage smsEnviado = twilioSmsService.enviarSmsViaTwilio(smsRequestDto);
        return SmsResponseDto.fromSmsMessage(smsEnviado);
    }

    @PostMapping("/emergencia/{idEvento}")
    @ResponseStatus(HttpStatus.CREATED)
    public SmsResponseDto enviarSmsEmergencia(
            @PathVariable String idEvento,
            @RequestBody @Valid SmsRequestDto smsRequestDto
    ) {
        SmsMessage smsEnviado = twilioSmsService.enviarSmsComEvento(smsRequestDto, idEvento);
        return SmsResponseDto.fromSmsMessage(smsEnviado);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> listarSms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean sucesso
    ) {
        Pageable pageable = Pageable.ofSize(size)
                .withPage(page)
                .withSort(Sort.by(direction, "dataEnvio"));

        Page<SmsMessage> smsPage;
        
        if (sucesso != null && sucesso) {
            smsPage = smsService.listarSmsEnviadosComSucessoComPaginacao(pageable);
        } else {
            smsPage = smsService.listarSmsComPaginacao(pageable);
        }

        Page<SmsResponseDto> smsResponse = smsPage.map(SmsResponseDto::fromSmsMessage);

        if (smsPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(smsResponse);
        }
    }

    @GetMapping("/buscarPorNumero")
    public ResponseEntity<List<SmsResponseDto>> buscarSmsPorNumero(@RequestParam String numeroTelefone) {
        List<SmsMessage> smsList = smsService.buscarSmsPorNumero(numeroTelefone);
        
        if (smsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<SmsResponseDto> response = smsList.stream()
                    .map(SmsResponseDto::fromSmsMessage)
                    .toList();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/buscarPorDdd")
    public ResponseEntity<List<SmsResponseDto>> buscarSmsPorDdd(@RequestParam String ddd) {
        List<SmsMessage> smsList = smsService.buscarSmsPorDdd(ddd);
        
        if (smsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<SmsResponseDto> response = smsList.stream()
                    .map(SmsResponseDto::fromSmsMessage)
                    .toList();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/buscarPorEvento/{idEvento}")
    public ResponseEntity<List<SmsResponseDto>> buscarSmsPorEvento(@PathVariable String idEvento) {
        List<SmsMessage> smsList = smsService.buscarSmsPorEvento(idEvento);
        
        if (smsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<SmsResponseDto> response = smsList.stream()
                    .map(SmsResponseDto::fromSmsMessage)
                    .toList();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/buscarPorPeriodo")
    public ResponseEntity<List<SmsResponseDto>> buscarSmsPorPeriodo(
            @RequestParam String dataInicio,
            @RequestParam String dataFim
    ) {
        LocalDateTime inicio = LocalDateTime.parse(dataInicio);
        LocalDateTime fim = LocalDateTime.parse(dataFim);
        
        List<SmsMessage> smsList = smsService.buscarSmsPorPeriodo(inicio, fim);
        
        if (smsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<SmsResponseDto> response = smsList.stream()
                    .map(SmsResponseDto::fromSmsMessage)
                    .toList();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/ultimoSms/{numero}")
    public ResponseEntity<SmsResponseDto> buscarUltimoSmsPorNumero(@PathVariable String numero) {
        Optional<SmsMessage> smsOpt = smsService.buscarUltimoSmsPorNumero(numero);
        
        if (smsOpt.isPresent()) {
            SmsResponseDto response = SmsResponseDto.fromSmsMessage(smsOpt.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<?> obterEstatisticas() {
        Long totalSucesso = smsService.contarSmsEnviadosComSucesso();
        Long totalErro = smsService.contarSmsComErro();
        
        return ResponseEntity.ok(new SmsEstatisticasResponse(totalSucesso, totalErro));
    }

    @PatchMapping("/marcarSucesso/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarSmsComoSucesso(@PathVariable String id) {
        smsService.marcarSmsComoEnviado(id);
    }

    @PatchMapping("/marcarErro/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarSmsComoErro(@PathVariable String id, @RequestParam String erro) {
        smsService.marcarSmsComoErro(id, erro);
    }

    private record SmsEstatisticasResponse(Long totalSucesso, Long totalErro) {}
}
