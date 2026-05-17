package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.domains.AppUser;
import com.example.SosLocaliza.gateways.dtos.request.MobileCadastroRequestDto;
import com.example.SosLocaliza.gateways.dtos.response.MobilePerfilResponseDto;
import com.example.SosLocaliza.gateways.dtos.response.RiscoAreaMobileDto;
import com.example.SosLocaliza.services.AppUserRegistrationService;
import com.example.SosLocaliza.services.AppUserService;
import com.example.SosLocaliza.services.NominatimReverseGeocodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile")
@RequiredArgsConstructor
public class MobileApiController {

    private final AppUserRegistrationService appUserRegistrationService;
    private final AppUserService appUserService;
    private final NominatimReverseGeocodeService nominatimReverseGeocodeService;

    @PostMapping("/cadastro")
    public ResponseEntity<Map<String, String>> cadastro(@RequestBody @Valid MobileCadastroRequestDto dto) {
        String username = appUserRegistrationService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Cadastro realizado com sucesso.",
                "username", username
        ));
    }

    /**
     * Pontos de exemplo (Grande SP) para o mapa do app mobile — mesmo contrato que o Oracle APEX usava no protótipo.
     */
    @GetMapping("/areas-risco")
    public List<RiscoAreaMobileDto> areasRisco() {
        return List.of(
                new RiscoAreaMobileDto(-23.55052, -46.63331, "alto"),
                new RiscoAreaMobileDto(-23.56168, -46.65614, "medio"),
                new RiscoAreaMobileDto(-23.50456, -46.63803, "baixo"),
                new RiscoAreaMobileDto(-23.53010, -46.61020, "medio"),
                new RiscoAreaMobileDto(-23.58710, -46.65590, "alto")
        );
    }

    @GetMapping("/me")
    public MobilePerfilResponseDto perfil(Principal principal) {
        AppUser u = appUserService.buscarObrigatorioPorUsername(principal.getName());
        return new MobilePerfilResponseDto(
                u.getUsername(),
                u.getNomeExibicao(),
                u.getLocalizacao(),
                u.getDdd(),
                u.getNumeroLocal()
        );
    }

    /**
     * Endereço legível a partir de coordenadas (Nominatim/OSM), para exibir no SMS e no painel admin.
     */
    @GetMapping("/reverse-geocode")
    public ResponseEntity<Map<String, String>> reverseGeocode(
            @RequestParam double lat,
            @RequestParam double lon) {
        return nominatimReverseGeocodeService.reverseGeocodeLinhaCurta(lat, lon)
                .map(line -> ResponseEntity.ok(Map.of("enderecoLinha", line)))
                .orElseGet(() -> ResponseEntity.ok(Map.of("enderecoLinha", "")));
    }
}
