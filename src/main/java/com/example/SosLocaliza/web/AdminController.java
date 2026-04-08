package com.example.SosLocaliza.web;

import com.example.SosLocaliza.domains.SmsMessage;
import com.example.SosLocaliza.gateways.dtos.response.SmsResponseDto;
import com.example.SosLocaliza.services.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SmsService smsService;

    @GetMapping
    @Transactional(readOnly = true)
    public String painel(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataEnvio"));
        Page<SmsMessage> smsPage = smsService.listarSmsComPaginacao(pageable);
        List<SmsResponseDto> linhas = smsPage.getContent().stream()
                .map(SmsResponseDto::fromSmsMessage)
                .toList();

        model.addAttribute("smsPage", smsPage);
        model.addAttribute("smsLinhas", linhas);
        model.addAttribute("totalSucesso", smsService.contarSmsEnviadosComSucesso());
        model.addAttribute("totalErro", smsService.contarSmsComErro());
        return "admin";
    }
}
