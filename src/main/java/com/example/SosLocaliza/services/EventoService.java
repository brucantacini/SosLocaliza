package com.example.SosLocaliza.services;

import com.example.SosLocaliza.domains.Evento;
import com.example.SosLocaliza.exceptions.EventoNotFoundException;
import com.example.SosLocaliza.gateways.EventoRepository;
import com.example.SosLocaliza.gateways.SmsRepository;
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
    private final SmsRepository smsRepository;

    public Evento criarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento atualizarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Optional<Evento> buscarEventoPorId(Long id) {
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

    public void deletarEvento(Long id) {
        smsRepository.deleteAll(smsRepository.findByEventoIdEvento(id));
        eventoRepository.deleteById(id);
    }

    public void desativarEvento(Long id) {
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isEmpty()) {
            throw new EventoNotFoundException("Evento não encontrado com ID: " + id);
        }
        
        Evento evento = eventoOpt.get();
        evento = evento.withAtivo(false);
        eventoRepository.save(evento);
    }

    public Long contarEventosAtivos() {
        return eventoRepository.countEventosAtivos();
    }

    public List<Evento> buscarEventosPorDescricao(String descricao) {
        return eventoRepository.findByDescricaoContainingAndAtivoTrue(descricao);
    }
}
