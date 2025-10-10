package com.example.SosLocaliza.services;

import com.example.SosLocaliza.domains.Evento;
import com.example.SosLocaliza.gateways.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventoService {

    private final EventoRepository eventoRepository;

    public Evento criarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento atualizarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Optional<Evento> buscarEventoPorId(String id) {
        return eventoRepository.findById(id);
    }

    public List<Evento> listarTodosEventos() {
        return eventoRepository.findAll();
    }

    public List<Evento> listarEventosAtivos() {
        return eventoRepository.findByAtivoTrue();
    }

    public List<Evento> buscarEventosPorNome(String nomeEvento) {
        return eventoRepository.findByNomeEventoContainingIgnoreCase(nomeEvento);
    }

    public Page<Evento> listarEventosComPaginacao(Pageable pageable) {
        return eventoRepository.findAll(pageable);
    }

    public Page<Evento> listarEventosAtivosComPaginacao(Pageable pageable) {
        return eventoRepository.findByAtivoTrue(pageable);
    }

    public Page<Evento> buscarEventosPorNomeComPaginacao(String nomeEvento, Pageable pageable) {
        return eventoRepository.findByNomeEventoContainingIgnoreCase(nomeEvento, pageable);
    }

    public void deletarEvento(String id) {
        eventoRepository.deleteById(id);
    }

    public void desativarEvento(String id) {
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            evento = evento.withAtivo(false);
            eventoRepository.save(evento);
        }
    }

    public Long contarEventosAtivos() {
        return eventoRepository.countEventosAtivos();
    }

    public List<Evento> buscarEventosPorDescricao(String descricao) {
        return eventoRepository.findByDescricaoContainingAndAtivoTrue(descricao);
    }
}
