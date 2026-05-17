package com.example.SosLocaliza.services;

import com.example.SosLocaliza.domains.AppUser;
import com.example.SosLocaliza.gateways.AppUserRepository;
import com.example.SosLocaliza.gateways.dtos.request.MobileCadastroRequestDto;
import com.example.SosLocaliza.util.CpfUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AppUserRegistrationService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * @return CPF normalizado (11 dígitos), usado como {@code username} no login.
     */
    @Transactional
    public String cadastrar(MobileCadastroRequestDto dto) {
        String cpfDigits = CpfUtil.somenteDigitos(dto.getCpf());
        if (cpfDigits.length() != 11 || !CpfUtil.cpfValido(cpfDigits)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido.");
        }

        if (appUserRepository.findByUsername(cpfDigits).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado.");
        }

        String nome = dto.getNomeCompleto().trim();
        if (nome.length() > 100) {
            nome = nome.substring(0, 100);
        }

        String ddd = null;
        String numero = null;
        if (StringUtils.hasText(dto.getTelefone())) {
            String digits = dto.getTelefone().replaceAll("\\D", "");
            if (digits.length() >= 10) {
                ddd = digits.substring(0, 2);
                numero = digits.substring(2);
                if (numero.length() > 9) {
                    numero = numero.substring(0, 9);
                }
            }
        }

        String local = null;
        if (StringUtils.hasText(dto.getCep())) {
            local = "CEP " + dto.getCep().trim();
        }

        AppUser user = AppUser.builder()
                .username(cpfDigits)
                .password(passwordEncoder.encode(dto.getSenha()))
                .enabled(true)
                .role("ROLE_USER")
                .nomeExibicao(nome)
                .ddd(ddd)
                .numeroLocal(numero)
                .localizacao(local)
                .build();

        appUserRepository.save(user);
        return cpfDigits;
    }
}
