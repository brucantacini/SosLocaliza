package com.example.SosLocaliza.configurations;

import com.example.SosLocaliza.domains.Evento;
import com.example.SosLocaliza.services.EventoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "oracle-fiap"})
public class DataInitializer implements CommandLineRunner {

    private final EventoService eventoService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Verificando eventos de emergência ativos (app mobile / SMS)...");

        Long totalAtivos = eventoService.contarEventosAtivos();
        if (totalAtivos != null && totalAtivos > 0) {
            log.info("{} evento(s) ativo(s); nada a fazer.", totalAtivos);
            return;
        }

        /* Linhas antigas (ex.: Oracle) com ATIVO NULL/0: existem em T_SOS_EVENTO mas /ativos vinha vazio. */
        List<Evento> todos = eventoService.listarTodosEventos();
        for (Evento e : todos) {
            if (!Boolean.TRUE.equals(e.getAtivo())) {
                log.info("Reativando evento id={} ({})", e.getIdEvento(), e.getNomeEvento());
                eventoService.atualizarEvento(e.withAtivo(true));
            }
        }

        if (eventoService.contarEventosAtivos() != null && eventoService.contarEventosAtivos() > 0) {
            log.info("Eventos existentes reativados.");
            return;
        }

        criarEventosExemplo();
        log.info("Dados de exemplo inicializados com sucesso!");
    }

    private void criarEventosExemplo() {
        Evento enchente = Evento.builder()
                .nomeEvento("Enchente")
                .descricaoEvento("Inundação causada por chuvas intensas que podem causar danos materiais e riscos à vida")
                .causas("Chuvas intensas, impermeabilização do solo, falta de drenagem adequada")
                .alertas("Nível dos rios elevado, áreas de risco identificadas, previsão de chuvas")
                .acoesAntes("Verificar se a casa está em área de risco, ter plano de evacuação, manter documentos importantes em local seguro")
                .acoesDurante("Evitar áreas alagadas, não dirigir em ruas inundadas, seguir orientações das autoridades")
                .acoesDepois("Verificar danos estruturais, limpar e desinfetar áreas afetadas, contatar seguro")
                .ativo(true)
                .build();

        Evento deslizamento = Evento.builder()
                .nomeEvento("Deslizamento de Terra")
                .descricaoEvento("Movimento de massa de solo e rochas em encostas, causado por chuvas ou instabilidade do terreno")
                .causas("Chuvas intensas, desmatamento, construção inadequada em encostas")
                .alertas("Trincas no solo, inclinação de postes, árvores inclinadas")
                .acoesAntes("Evitar construção em encostas íngremes, manter vegetação nativa, instalar sistemas de drenagem")
                .acoesDurante("Evacuar imediatamente a área, não tentar salvar pertences, seguir rotas de fuga")
                .acoesDepois("Aguardar liberação das autoridades, verificar estabilidade do terreno, reconstruir com técnicas adequadas")
                .ativo(true)
                .build();

        Evento seca = Evento.builder()
                .nomeEvento("Seca")
                .descricaoEvento("Período prolongado de ausência de chuvas que afeta recursos hídricos e agricultura")
                .causas("Ausência prolongada de chuvas, mudanças climáticas, uso inadequado da água")
                .alertas("Nível baixo de reservatórios, vegetação seca, animais migrando")
                .acoesAntes("Armazenar água potável, economizar água, plantar espécies resistentes à seca")
                .acoesDurante("Usar água com moderação, evitar desperdício, seguir racionamento")
                .acoesDepois("Replantar vegetação, melhorar sistemas de captação de água, implementar práticas sustentáveis")
                .ativo(true)
                .build();

        Evento tempestade = Evento.builder()
                .nomeEvento("Tempestade")
                .descricaoEvento("Fenômeno meteorológico com ventos fortes, chuvas intensas e possíveis granizos")
                .causas("Instabilidade atmosférica, diferenças de temperatura, sistemas frontais")
                .alertas("Ventos acima de 60 km/h, formação de nuvens escuras, queda de temperatura")
                .acoesAntes("Fixar objetos soltos, podar árvores próximas, ter lanternas e pilhas")
                .acoesDurante("Ficar em local seguro, evitar áreas abertas, não usar aparelhos elétricos")
                .acoesDepois("Verificar danos, limpar detritos, reportar problemas à concessionária")
                .ativo(true)
                .build();

        Evento incendioFlorestal = Evento.builder()
                .nomeEvento("Incêndio Florestal")
                .descricaoEvento("Queimada descontrolada em áreas de vegetação que pode se espalhar rapidamente")
                .causas("Secas prolongadas, atividades humanas, raios, queimadas criminosas")
                .alertas("Fumaça no ar, cheiro de queimado, animais fugindo da área")
                .acoesAntes("Manter área limpa ao redor da propriedade, ter equipamentos de combate ao fogo")
                .acoesDurante("Evacuar imediatamente, seguir orientações dos bombeiros, não tentar combater sozinho")
                .acoesDepois("Aguardar liberação das autoridades, verificar danos, replantar vegetação")
                .ativo(true)
                .build();

        eventoService.criarEvento(enchente);
        eventoService.criarEvento(deslizamento);
        eventoService.criarEvento(seca);
        eventoService.criarEvento(tempestade);
        eventoService.criarEvento(incendioFlorestal);

        log.info("5 eventos de exemplo criados com sucesso!");
    }
}

