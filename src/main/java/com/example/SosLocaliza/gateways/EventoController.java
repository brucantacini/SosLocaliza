package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.domains.Evento;
import com.example.SosLocaliza.gateways.dtos.request.EventoRequestDto;
import com.example.SosLocaliza.gateways.dtos.request.EventoUpdateDto;
import com.example.SosLocaliza.gateways.dtos.response.EventoResponseDto;
import com.example.SosLocaliza.services.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.Optional;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "API para gerenciamento de eventos climáticos de emergência")
public class EventoController {

    private final EventoService eventoService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar novo evento", description = "Cria um novo evento climático de emergência")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento criado com sucesso",
                    content = @Content(schema = @Schema(implementation = EventoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public EventoResponseDto criarEvento(
            @Parameter(description = "Dados do evento a ser criado")
            @RequestBody @Valid EventoRequestDto eventoDto) {
        Evento evento = eventoDto.toEvento();
        Evento eventoCriado = eventoService.criarEvento(evento);
        return EventoResponseDto.fromEvento(eventoCriado);
    }

    @GetMapping("/getAll")
    @Operation(summary = "Listar eventos", description = "Lista todos os eventos com paginação e filtros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum evento encontrado")
    })
    public ResponseEntity<?> listarEventos(
            @Parameter(description = "Número da página (padrão: 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Direção da ordenação (ASC/DESC)")
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @Parameter(description = "Tamanho da página (padrão: 10)")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filtro por nome do evento")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Mostrar apenas eventos ativos")
            @RequestParam(defaultValue = "true") boolean apenasAtivos
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "dataCriacao"));

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
    public ResponseEntity<EventoResponseDto> buscarEventoPorId(@PathVariable Long id) {
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
            @PathVariable Long id,
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
    public void deletarEvento(@PathVariable Long id) {
        eventoService.deletarEvento(id);
    }

    @PatchMapping("/desativar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativarEvento(@PathVariable Long id) {
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