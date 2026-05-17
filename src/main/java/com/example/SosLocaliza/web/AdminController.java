package com.example.SosLocaliza.web;

import com.example.SosLocaliza.domains.AppUser;
import com.example.SosLocaliza.domains.SmsMessage;
import com.example.SosLocaliza.gateways.AppUserRepository;
import com.example.SosLocaliza.services.SmsService;
import com.example.SosLocaliza.web.dto.AdminSmsLinhaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Pattern PAT_LAT_LNG = Pattern.compile(
            "Lat:\\s*([-\\d.]+)\\s*\\R.*?Lng:\\s*([-\\d.]+)", Pattern.DOTALL);

    private final SmsService smsService;
    private final AppUserRepository appUserRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public String painel(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataEnvio"));
        Page<SmsMessage> smsPage = smsService.listarSmsComPaginacao(pageable);
        Map<String, String> localPorRemetente = montarMapaLocalizacaoPorRemetente();
        List<AdminSmsLinhaDto> linhas = smsPage.getContent().stream()
                .map(sms -> paraLinha(sms, localPorRemetente))
                .toList();

        model.addAttribute("smsPage", smsPage);
        model.addAttribute("smsLinhas", linhas);
        return "admin";
    }

    private Map<String, String> montarMapaLocalizacaoPorRemetente() {
        Map<String, String> map = new HashMap<>();
        for (AppUser u : appUserRepository.findAll()) {
            String loc = StringUtils.hasText(u.getLocalizacao()) ? u.getLocalizacao().trim() : "—";
            if (StringUtils.hasText(u.getNomeExibicao())) {
                map.put(u.getNomeExibicao().trim(), loc);
            }
            map.put(u.getUsername(), loc);
        }
        return map;
    }

    /** Texto gravado no SMS pelo app: "Endereço (envio): rua, nº, bairro, cidade, UF". */
    private static String extrairEnderecoEnvio(String mensagem) {
        if (!StringUtils.hasText(mensagem)) {
            return null;
        }
        for (String raw : mensagem.split("\\R")) {
            String line = raw.trim();
            if (line.startsWith("Endereço (envio):")) {
                String v = line.substring("Endereço (envio):".length()).trim();
                return StringUtils.hasText(v) ? v : null;
            }
        }
        return null;
    }

    private static String extrairResumoGps(String mensagem) {
        if (!StringUtils.hasText(mensagem)) {
            return null;
        }
        Matcher m = PAT_LAT_LNG.matcher(mensagem);
        if (m.find()) {
            return "GPS " + m.group(1) + ", " + m.group(2);
        }
        return null;
    }

    private static String localizacaoParaExibicao(SmsMessage sms, Map<String, String> localPorRemetente) {
        String msg = sms.getMensagem();
        String end = extrairEnderecoEnvio(msg);
        if (StringUtils.hasText(end)) {
            return end;
        }
        String gps = extrairResumoGps(msg);
        if (StringUtils.hasText(gps)) {
            return gps;
        }
        String rem = sms.getRemetente() != null ? sms.getRemetente().trim() : "";
        return localPorRemetente.getOrDefault(rem, "—");
    }

    private static AdminSmsLinhaDto paraLinha(SmsMessage sms, Map<String, String> localPorRemetente) {
        String loc = localizacaoParaExibicao(sms, localPorRemetente);
        String nomeEvento = sms.getEvento() != null && StringUtils.hasText(sms.getEvento().getNomeEvento())
                ? sms.getEvento().getNomeEvento()
                : (sms.getEvento() != null ? "Evento #" + sms.getEvento().getIdEvento() : "—");
        return new AdminSmsLinhaDto(
                sms.getDataEnvio(),
                sms.getRemetente(),
                sms.getNumeroTelefone(),
                loc,
                nomeEvento,
                sms.getMensagem()
        );
    }
}
