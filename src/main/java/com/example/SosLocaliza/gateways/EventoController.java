package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.domains.Evento;
import com.example.SosLocaliza.gateways.dtos.request.EventoRequestDto;
import com.example.SosLocaliza.gateways.dtos.request.EventoUpdateDto;
import com.example.SosLocaliza.gateways.dtos.response.EventoResponseDto;
import com.example.SosLocaliza.services.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public EventoResponseDto criarEvento(@RequestBody @Valid EventoRequestDto eventoDto) {
        Evento evento = eventoDto.toEvento();
        Evento eventoCriado = eventoService.criarEvento(evento);
        return EventoResponseDto.fromEvento(eventoCriado);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> listarEventos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "true") boolean apenasAtivos
    ) {
        Pageable pageable = Pageable.ofSize(size)
                .withPage(page)
                .withSort(Sort.by(direction, "dataCriacao"));

        Page<Evento> eventos;
        
        if (nome != null && !nome.trim().isEmpty()) {
            eventos = eventoService.buscarEventosPorNomeComPaginacao(nome, pageable);
        } else if (apenasAtivos) {
            eventos = eventoService.listarEventosAtivosComPaginacao(pageable);
        } else {
            eventos = eventoService.listarEventosComPaginacao(pageable);
        }

        Page<EventoResponseDto> eventosResponse = eventos.map(EventoResponseDto::fromEvento);

        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(eventosResponse);
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<EventoResponseDto> buscarEventoPorId(@PathVariable String id) {
        Optional<Evento> eventoOpt = eventoService.buscarEventoPorId(id);
        
        if (eventoOpt.isPresent()) {
            EventoResponseDto response = EventoResponseDto.fromEvento(eventoOpt.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EventoResponseDto> atualizarEvento(
            @PathVariable String id,
            @RequestBody @Valid EventoUpdateDto eventoDto
    ) {
        Optional<Evento> eventoExistenteOpt = eventoService.buscarEventoPorId(id);
        
        if (eventoExistenteOpt.isPresent()) {
            Evento eventoAtualizado = eventoDto.toEvento(id);
            Evento eventoSalvo = eventoService.atualizarEvento(eventoAtualizado);
            EventoResponseDto response = EventoResponseDto.fromEvento(eventoSalvo);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarEvento(@PathVariable String id) {
        eventoService.deletarEvento(id);
    }

    @PatchMapping("/desativar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativarEvento(@PathVariable String id) {
        eventoService.desativarEvento(id);
    }

    @GetMapping("/buscarPorNome")
    public ResponseEntity<List<EventoResponseDto>> buscarEventosPorNome(@RequestParam String nome) {
        List<Evento> eventos = eventoService.buscarEventosPorNome(nome);
        
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<EventoResponseDto> response = eventos.stream()
                    .map(EventoResponseDto::fromEvento)
                    .toList();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/buscarPorDescricao")
    public ResponseEntity<List<EventoResponseDto>> buscarEventosPorDescricao(@RequestParam String descricao) {
        List<Evento> eventos = eventoService.buscarEventosPorDescricao(descricao);
        
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<EventoResponseDto> response = eventos.stream()
                    .map(EventoResponseDto::fromEvento)
                    .toList();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<?> obterEstatisticas() {
        Long totalAtivos = eventoService.contarEventosAtivos();
        return ResponseEntity.ok(new EstatisticasResponse(totalAtivos));
    }

    private record EstatisticasResponse(Long totalEventosAtivos) {}
}