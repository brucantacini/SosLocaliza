package com.example.SosLocaliza.services;

import com.example.SosLocaliza.domains.AppUser;
import com.example.SosLocaliza.gateways.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public Optional<AppUser> buscarPorUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public AppUser buscarObrigatorioPorUsername(String username) {
        return buscarPorUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));
    }
}
