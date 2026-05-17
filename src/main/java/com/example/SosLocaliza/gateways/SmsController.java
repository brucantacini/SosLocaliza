package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.domains.SmsMessage;
import com.example.SosLocaliza.gateways.dtos.request.SmsRequestDto;
import com.example.SosLocaliza.gateways.dtos.request.SmsUpdateDto;
import com.example.SosLocaliza.gateways.dtos.response.SmsResponseDto;
import com.example.SosLocaliza.services.SmsService;
import com.example.SosLocaliza.services.TwilioSmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
@Slf4j
public class SmsController {

    private final SmsService smsService;
    private final TwilioSmsService twilioSmsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SmsResponseDto enviarSms(@RequestBody @Valid SmsRequestDto smsRequestDto) {
        SmsMessage smsEnviado = twilioSmsService.enviarSmsComEvento(smsRequestDto, smsRequestDto.getIdEvento());
        SmsResponseDto response = SmsResponseDto.fromSmsMessage(smsEnviado);
        HateoasLinkBuilder.adicionarLinksSms(response);
        return response;
    }

    @PostMapping("/emergencia/{idEvento}")
    @ResponseStatus(HttpStatus.CREATED)
    public SmsResponseDto enviarSmsEmergencia(
            @PathVariable Long idEvento,
            @RequestBody @Valid SmsRequestDto smsRequestDto
    ) {
        SmsMessage smsEnviado = twilioSmsService.enviarSmsComEvento(smsRequestDto, idEvento);
        SmsResponseDto response = SmsResponseDto.fromSmsMessage(smsEnviado);
        HateoasLinkBuilder.adicionarLinksSms(response);
        return response;
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<SmsResponseDto> buscarPorId(@PathVariable Long id) {
        return smsService.buscarSmsPorId(id)
                .map(sms -> {
                    SmsResponseDto dto = SmsResponseDto.fromSmsMessage(sms);
                    HateoasLinkBuilder.adicionarLinksSms(dto);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SmsResponseDto> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid SmsUpdateDto dto
    ) {
        SmsMessage atualizado = smsService.atualizarSms(id, dto);
        SmsResponseDto response = SmsResponseDto.fromSmsMessage(atualizado);
        HateoasLinkBuilder.adicionarLinksSms(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        smsService.deletarSms(id);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> listarSms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean sucesso
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "dataEnvio"));

        Page<SmsMessage> smsPage;
        
        if (sucesso != null && sucesso) {
            smsPage = smsService.listarSmsEnviadosComSucessoComPaginacao(pageable);
        } else {
            smsPage = smsService.listarSmsComPaginacao(pageable);
        }

        Page<SmsResponseDto> smsResponse = smsPage.map(sms -> {
            SmsResponseDto dto = SmsResponseDto.fromSmsMessage(sms);
            HateoasLinkBuilder.adicionarLinksSms(dto);
            return dto;
        });

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
                    .map(sms -> {
                        SmsResponseDto dto = SmsResponseDto.fromSmsMessage(sms);
                        HateoasLinkBuilder.adicionarLinksSms(dto);
                        return dto;
                    })
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
                    .map(sms -> {
                        SmsResponseDto dto = SmsResponseDto.fromSmsMessage(sms);
                        HateoasLinkBuilder.adicionarLinksSms(dto);
                        return dto;
                    })
                    .toList();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/buscarPorEvento/{idEvento}")
    public ResponseEntity<List<SmsResponseDto>> buscarSmsPorEvento(@PathVariable Long idEvento) {
        List<SmsMessage> smsList = smsService.buscarSmsPorEvento(idEvento);
        
        if (smsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<SmsResponseDto> response = smsList.stream()
                    .map(sms -> {
                        SmsResponseDto dto = SmsResponseDto.fromSmsMessage(sms);
                        HateoasLinkBuilder.adicionarLinksSms(dto);
                        return dto;
                    })
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
                    .map(sms -> {
                        SmsResponseDto dto = SmsResponseDto.fromSmsMessage(sms);
                        HateoasLinkBuilder.adicionarLinksSms(dto);
                        return dto;
                    })
                    .toList();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/ultimoSms/{numero:.+}")
    public ResponseEntity<SmsResponseDto> buscarUltimoSmsPorNumero(@PathVariable String numero) {
        try {
            log.info("Buscando último SMS para número: {}", numero);
            
            String numeroTratado = numero;
            if (numero.contains("%")) {
                numeroTratado = java.net.URLDecoder.decode(numero, java.nio.charset.StandardCharsets.UTF_8);
            }
            
            numeroTratado = numeroTratado.replace(" ", "+");
            
            if (!numeroTratado.startsWith("+")) {
                numeroTratado = "+" + numeroTratado.trim();
            }
            
            log.info("Número tratado: {}", numeroTratado);
            
            Optional<SmsMessage> smsOpt = smsService.buscarUltimoSmsPorNumero(numeroTratado);
            
            if (smsOpt.isPresent()) {
                SmsResponseDto response = SmsResponseDto.fromSmsMessage(smsOpt.get());
                HateoasLinkBuilder.adicionarLinksSms(response);
                return ResponseEntity.ok(response);
            } else {
                log.warn("SMS não encontrado para número: {}", numeroTratado);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Erro ao buscar último SMS: {}", e.getMessage(), e);
            throw e;
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
    public void marcarSmsComoSucesso(@PathVariable Long id) {
        smsService.marcarSmsComoEnviado(id);
    }

    @PatchMapping("/marcarErro/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarSmsComoErro(@PathVariable Long id, @RequestParam String erro) {
        smsService.marcarSmsComoErro(id, erro);
    }

    private record SmsEstatisticasResponse(Long totalSucesso, Long totalErro) {}
}
