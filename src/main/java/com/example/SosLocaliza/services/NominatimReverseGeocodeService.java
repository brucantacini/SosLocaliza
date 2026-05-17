package com.example.SosLocaliza.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

/**
 * Geocodificação reversa via Nominatim (OSM). Uso moderado; User-Agent obrigatório pela política do serviço.
 */
@Service
@Slf4j
public class NominatimReverseGeocodeService {

    private static final String USER_AGENT = "SOSLocaliza/1.0 (projeto acadêmico; pt-BR)";
    private static final int MAX_LINHA = 220;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    public Optional<String> reverseGeocodeLinhaCurta(double lat, double lon) {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            return Optional.empty();
        }
        String url = String.format(Locale.US,
                "https://nominatim.openstreetmap.org/reverse?lat=%f&lon=%f&format=json&accept-language=pt-BR,pt,en",
                lat, lon);
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(12))
                    .header("User-Agent", USER_AGENT)
                    .GET()
                    .build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                return Optional.empty();
            }
            JsonNode root = objectMapper.readTree(res.body());
            JsonNode addr = root.get("address");
            if (addr == null || !addr.isObject()) {
                return Optional.empty();
            }
            String line = montarLinhaEndereco(addr);
            if (line == null || line.isBlank()) {
                return Optional.empty();
            }
            if (line.length() > MAX_LINHA) {
                line = line.substring(0, MAX_LINHA - 1) + "…";
            }
            return Optional.of(line);
        } catch (Exception e) {
            log.warn("Nominatim indisponível: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static String montarLinhaEndereco(JsonNode addr) {
        String road = primeiroTexto(addr, "road", "pedestrian", "residential", "path", "footway");
        String num = texto(addr, "house_number");
        String bairro = primeiroTexto(addr, "suburb", "neighbourhood", "quarter", "city_district");
        String cidade = primeiroTexto(addr, "city", "town", "village", "municipality");
        String uf = texto(addr, "state_code");
        if (uf == null || uf.isBlank()) {
            String estado = texto(addr, "state");
            if (estado != null && estado.length() <= 3) {
                uf = estado;
            }
        }

        StringBuilder sb = new StringBuilder();
        if (road != null && !road.isBlank()) {
            sb.append(road.trim());
            if (num != null && !num.isBlank()) {
                sb.append(", ").append(num.trim());
            }
        } else if (num != null && !num.isBlank()) {
            sb.append(num.trim());
        }
        if (bairro != null && !bairro.isBlank()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(bairro.trim());
        }
        if (cidade != null && !cidade.isBlank()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(cidade.trim());
        }
        if (uf != null && !uf.isBlank()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(uf.trim().toUpperCase(Locale.ROOT));
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private static String texto(JsonNode parent, String field) {
        JsonNode n = parent.get(field);
        return n != null && n.isTextual() ? n.asText().trim() : "";
    }

    private static String primeiroTexto(JsonNode parent, String... fields) {
        for (String f : fields) {
            String t = texto(parent, f);
            if (!t.isBlank()) {
                return t;
            }
        }
        return "";
    }
}
