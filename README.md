# SOS LOCALIZA

Protótipo acadêmico responsivo para demonstrar como poderia funcionar o registro de uma situação de risco e a visualização de condições meteorológicas próximas. Nenhum alerta é enviado a órgãos públicos ou serviços de emergência.

## Tecnologias

- React e TypeScript para telas, fluxos e classificação demonstrativa.
- Vite para desenvolvimento e geração da versão de apresentação.
- Leaflet e tiles do OpenStreetMap para o mapa interativo.
- Open-Meteo Forecast API para previsão horária de precipitação e probabilidade de chuva.
- `localStorage` para manter perfil e histórico de alertas no navegador atual.

## Como executar

Instale Node.js (versão LTS) e, na pasta do projeto, rode:

```bash
npm install
npm run dev
```

Abra o endereço local exibido pelo Vite. Para gerar e visualizar a versão otimizada:

```bash
npm run build
npm run preview
```

O uso acadêmico não comercial da Open-Meteo não exige chave de API. A API gratuita tem limites de uso e requer atribuição CC BY 4.0. Tiles do OpenStreetMap são carregados pela rede e exigem atribuição visível; consulte a política do provedor antes de publicar para tráfego elevado.

## Demonstração do mapa

1. A tela inicial abre o mapa no ponto de demonstração de São Paulo, identificado como tal, e consulta a previsão para esse ponto.
2. Para consultar sua posição, escolha **Usar minha localização** e permita o acesso do navegador. Se negar, o ponto demonstrativo continua disponível.
3. Aguarde a previsão de nove pontos aproximados. Toque nos marcadores para consultar a estimativa, a chuva horária, o acumulado em 24 horas, a probabilidade disponível e a hora da consulta.
4. Use os controles de zoom ou o botão de centralizar. Atualize manualmente para consultar os dados de novo.

O navegador precisa de contexto seguro para disponibilizar a localização. Se a permissão for negada ou a posição não estiver disponível, o ponto de demonstração permanece identificado como tal. Uma falha da API não bloqueia os outros recursos do aplicativo e não gera níveis de risco fictícios.

## Interface e revisão visual

A interface usa fundo cinza quente, texto petróleo, ações verde-lima e o botão de comunicação vermelho. A Montserrat variável é servida pelo próprio projeto em `public/fonts`, com licença SIL OFL e alternativa Arial. A aplicação não consulta serviços externos de fontes. Os tokens e padrões responsivos estão em `DESIGN.md`.

No Windows, se o PowerShell bloquear `npm.ps1`, use `npm.cmd run dev` e `npm.cmd run build`.

### Tema claro e escuro

Use **Claro** ou **Escuro** no cabeçalho, disponível em todas as telas. A opção ativa fica destacada e é informada a leitores de tela. Na primeira visita o sistema segue o tema do dispositivo; após uma escolha manual, ela prevalece e fica salva em `sos-localiza-theme` no `localStorage`, inclusive entre abas. Se o armazenamento estiver bloqueado, a escolha funciona durante a sessão. Para voltar a seguir o dispositivo, remova somente essa chave pelo painel Application/Storage do navegador e recarregue a página.

A preferência é aplicada no HTML antes do React, inclusive ao fundo e à cor da barra do navegador. O mapa mantém os tiles originais; controles, legenda e mensagens acompanham o tema sem alterar cores de risco ou recriar o mapa.

Para validar persistência, preferência do dispositivo, teclado, sincronização e armazenamento bloqueado, execute `node scripts/check-theme.mjs` com o Vite iniciado. `node scripts/check-ui.mjs` captura cada tela nos dois temas em 390, 768 e 1440 px e verifica que a alternância mantém os campos e o mapa. Os resultados desses roteiros ficam em `.impeccable/review/themes`.

Com o Vite em `http://127.0.0.1:5173/`, execute `node scripts/check-ui.mjs` para conferir os fluxos e capturar telas em 390, 768 e 1440 px. O roteiro usa o Chrome/Edge instalado e o cliente CDP da skill local; não adiciona dependências. Os cenários meteorológicos e de permissão são controlados somente nesse navegador de teste. Uma captura separada consulta a API real. Resultados ficam em `.impeccable/review`; nenhum dado de teste é incluído na aplicação distribuída.

## Demonstração de alertas

1. Cadastre informações claramente fictícias, como “Alex Silva”, “000.000.000-00”, “(11) 90000-0000” e “Rua Exemplo, 10, Bairro Jardim, São Paulo”.
2. Na tela inicial, toque em **COMUNICAR ESTADO DE ALERTA** e escolha uma ocorrência.
3. Revise os dados e confirme o registro simulado.
4. Abra o histórico para consultar o registro e seus detalhes. O perfil e o histórico continuam no mesmo navegador após recarregar a página.

## Dados, privacidade e limites

O perfil e os registros simulados ficam no armazenamento local do navegador e não são enviados à API meteorológica. Ao escolher a localização atual, somente as coordenadas dos nove pontos de consulta são enviadas à Open-Meteo. O navegador também carrega os tiles necessários à área visível do OpenStreetMap. A escolha do ponto demonstrativo consulta as coordenadas fixas de demonstração em São Paulo.

As cores são estimativas heurísticas baseadas em precipitação prevista e probabilidade de chuva. Não representam perímetros de inundação ou deslizamento e não substituem avisos oficiais. A API de previsão escolhida não oferece alertas oficiais integrados; consulte os avisos do [INMET](https://avisos.inmet.gov.br/) em situações reais. Os níveis são ocultados se a consulta local tiver mais de 30 minutos e a pessoa ainda não tiver atualizado.
