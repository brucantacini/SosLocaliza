package com.example.SosLocaliza.web;

import com.example.SosLocaliza.domains.AppUser;
import com.example.SosLocaliza.gateways.dtos.request.SmsRequestDto;
import com.example.SosLocaliza.gateways.dtos.request.SocorroForm;
import com.example.SosLocaliza.services.AppUserService;
import com.example.SosLocaliza.services.EventoService;
import com.example.SosLocaliza.services.TwilioSmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/socorro")
@RequiredArgsConstructor
public class SocorroController {

    private final TwilioSmsService twilioSmsService;
    private final EventoService eventoService;
    private final AppUserService appUserService;

    @GetMapping
    public String form(Model model, Principal principal) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new SocorroForm());
        }
        model.addAttribute("eventos", eventoService.listarEventosAtivos());
        enriquecerModelComUsuario(model, principal);
        return "socorro";
    }

    @PostMapping
    public String enviar(
            @Valid @ModelAttribute("form") SocorroForm form,
            BindingResult bindingResult,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        AppUser usuario = appUserService.buscarObrigatorioPorUsername(principal.getName());

        if (!StringUtils.hasText(usuario.getDdd()) || !StringUtils.hasText(usuario.getNumeroLocal())) {
            bindingResult.reject("perfil", "Seu cadastro não possui telefone. Solicite ao administrador a atualização do perfil.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("eventos", eventoService.listarEventosAtivos());
            enriquecerModelComUsuario(model, principal);
            return "socorro";
        }

        SmsRequestDto dto = new SmsRequestDto();
        dto.setRemetente(remetenteParaSms(usuario));
        dto.setDdd(usuario.getDdd().trim());
        dto.setNumeroTelefone(usuario.getNumeroLocal().trim());
        dto.setMensagem(form.getMensagem());
        twilioSmsService.enviarSmsComEvento(dto, form.getIdEvento());
        redirectAttributes.addFlashAttribute("successMessage", "Solicitação de socorro registrada. Acompanhe o status no painel administrativo.");
        return "redirect:/socorro";
    }

    private void enriquecerModelComUsuario(Model model, Principal principal) {
        if (principal == null) {
            return;
        }
        appUserService.buscarPorUsername(principal.getName()).ifPresent(usuario -> {
            model.addAttribute("nomeContato", remetenteParaSms(usuario));
            model.addAttribute("telefoneResumo", formatarTelefoneResumo(usuario.getDdd(), usuario.getNumeroLocal()));
            model.addAttribute("localizacaoResumo", formatarLocalizacaoResumo(usuario.getLocalizacao()));
        });
    }

    private static String remetenteParaSms(AppUser usuario) {
        if (StringUtils.hasText(usuario.getNomeExibicao())) {
            return usuario.getNomeExibicao().trim();
        }
        return usuario.getUsername();
    }

    private static String formatarTelefoneResumo(String ddd, String numero) {
        if (!StringUtils.hasText(ddd) || !StringUtils.hasText(numero)) {
            return "—";
        }
        String n = numero.trim();
        if (n.length() == 9) {
            return ddd + " " + n.substring(0, 5) + "-" + n.substring(5);
        }
        if (n.length() == 8) {
            return ddd + " " + n.substring(0, 4) + "-" + n.substring(4);
        }
        return ddd + " " + n;
    }

    private static String formatarLocalizacaoResumo(String localizacao) {
        if (!StringUtils.hasText(localizacao)) {
            return "não informada";
        }
        return localizacao.trim();
    }
}
