package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.domains.Evento;
import com.example.SosLocaliza.exceptions.EventoNotFoundException;
import com.example.SosLocaliza.gateways.dtos.request.EventoRequestDto;
import com.example.SosLocaliza.gateways.dtos.request.EventoUpdateDto;
import com.example.SosLocaliza.gateways.dtos.response.EventoResponseDto;
import com.example.SosLocaliza.services.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public EventoResponseDto adicionar(@RequestBody @Valid EventoRequestDto dto) {
        Evento criado = eventoService.criarEvento(dto.toEvento());
        EventoResponseDto response = EventoResponseDto.fromEvento(criado);
        HateoasLinkBuilder.adicionarLinksEvento(response);
        return response;
    }

    @GetMapping("/getAll")
    public ResponseEntity<Page<EventoResponseDto>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeEvento"));
        Page<Evento> eventos = eventoService.listarEventosComPaginacao(pageable);
        Page<EventoResponseDto> dtoPage = eventos.map(e -> {
            EventoResponseDto r = EventoResponseDto.fromEvento(e);
            HateoasLinkBuilder.adicionarLinksEvento(r);
            return r;
        });
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<EventoResponseDto>> listarAtivos() {
        List<EventoResponseDto> list = eventoService.listarEventosAtivos().stream()
                .map(e -> {
                    EventoResponseDto r = EventoResponseDto.fromEvento(e);
                    HateoasLinkBuilder.adicionarLinksEvento(r);
                    return r;
                })
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<EventoResponseDto> buscarPorId(@PathVariable Long id) {
        return eventoService.buscarEventoPorId(id)
                .map(e -> {
                    EventoResponseDto r = EventoResponseDto.fromEvento(e);
                    HateoasLinkBuilder.adicionarLinksEvento(r);
                    return ResponseEntity.ok(r);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EventoResponseDto> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EventoUpdateDto dto
    ) {
        Evento existing = eventoService.buscarEventoPorId(id)
                .orElseThrow(() -> new EventoNotFoundException("Evento não encontrado com ID: " + id));
        Evento merged = existing
                .withNomeEvento(dto.getNomeEvento())
                .withDescricaoEvento(dto.getDescricaoEvento())
                .withCausas(dto.getCausas())
                .withAlertas(dto.getAlertas())
                .withAcoesAntes(dto.getAcoesAntes())
                .withAcoesDurante(dto.getAcoesDurante())
                .withAcoesDepois(dto.getAcoesDepois())
                .withAtivo(dto.getAtivo() != null ? dto.getAtivo() : existing.getAtivo());
        Evento atualizado = eventoService.atualizarEvento(merged);
        EventoResponseDto r = EventoResponseDto.fromEvento(atualizado);
        HateoasLinkBuilder.adicionarLinksEvento(r);
        return ResponseEntity.ok(r);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        eventoService.deletarEvento(id);
    }

    @PatchMapping("/desativar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        eventoService.desativarEvento(id);
    }

    @GetMapping("/buscarPorNome")
    public ResponseEntity<List<EventoResponseDto>> buscarPorNome(@RequestParam String nome) {
        List<EventoResponseDto> list = eventoService.buscarEventosPorNome(nome).stream()
                .map(e -> {
                    EventoResponseDto r = EventoResponseDto.fromEvento(e);
                    HateoasLinkBuilder.adicionarLinksEvento(r);
                    return r;
                })
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/buscarPorDescricao")
    public ResponseEntity<List<EventoResponseDto>> buscarPorDescricao(@RequestParam String descricao) {
        List<EventoResponseDto> list = eventoService.buscarEventosPorDescricao(descricao).stream()
                .map(e -> {
                    EventoResponseDto r = EventoResponseDto.fromEvento(e);
                    HateoasLinkBuilder.adicionarLinksEvento(r);
                    return r;
                })
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Long>> estatisticas() {
        return ResponseEntity.ok(Map.of("totalAtivos", eventoService.contarEventosAtivos()));
    }
}
