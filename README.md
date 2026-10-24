# SOS LOCALIZA

Protótipo acadêmico responsivo para demonstrar como poderia funcionar o registro de uma situação de risco e a visualização de condições meteorológicas próximas. Nenhum alerta é enviado a órgãos públicos ou serviços de emergência.

## Tecnologias

- React e TypeScript para telas, fluxos e envio de alertas à API.
- Vite para desenvolvimento e geração da versão de apresentação.
- Leaflet e tiles do OpenStreetMap para o mapa interativo.
- Open-Meteo Forecast API para previsão horária de precipitação e probabilidade de chuva.
- `localStorage` para cadastro local e cópias dos alertas confirmados pela API, diferenciadas de registros legados sem envio.

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

Com o Vite em `http://127.0.0.1:5173/`, execute `node scripts/check-ui.mjs` para conferir os fluxos e capturar telas em 390, 768 e 1440 px. O roteiro usa o Chrome/Edge instalado e o cliente CDP local em `scripts/lib/cdp.mjs`; não adiciona dependências. Os cenários meteorológicos e de permissão são controlados somente nesse navegador de teste. Uma captura separada consulta a API real. Resultados ficam em `.impeccable/review`; nenhum dado de teste é incluído na aplicação distribuída.

## Envio de alertas à Central

1. Cadastre informações claramente fictícias, como “Alex Silva”, “000.000.000-00”, “(11) 90000-0000” e “Rua Exemplo, 10, Bairro Jardim, São Paulo”.
2. Na tela inicial, toque em **COMUNICAR ESTADO DE ALERTA** e escolha uma ocorrência.
3. Revise os dados e selecione **Confirmar registro**. O app faz POST na API e só confirma quando ela devolve o alerta gravado no Oracle, com identificador e horário do servidor. A confirmação também informa que a Defesa Civil não foi notificada.
4. Abra o histórico para consultar o registro e seus detalhes. O perfil e o histórico continuam no mesmo navegador após recarregar a página.

## Dados, privacidade e limites

O perfil fica no armazenamento local do navegador. Cada novo alerta transmite uma cópia do perfil (nome, CPF, telefone e endereço), ocorrência, observações e localização à API SOS Localiza, que grava no Oracle. As coordenadas do alerta vêm apenas da permissão GPS do próprio fluxo; endereços manuais/cadastrados enviam latitude e longitude null. O ponto demonstrativo do mapa meteorológico nunca é usado como localização do alerta. O histórico local diferencia confirmações da API de registros legados sem envio e não acompanha atualizações de status na Central. Dados pessoais não são enviados à API meteorológica. Ao escolher a localização atual, somente as coordenadas dos nove pontos de consulta são enviadas à Open-Meteo. O navegador também carrega os tiles necessários à área visível do OpenStreetMap. A escolha do ponto demonstrativo consulta as coordenadas fixas de demonstração em São Paulo.

As cores são estimativas heurísticas baseadas em precipitação prevista e probabilidade de chuva. Não representam perímetros de inundação ou deslizamento e não substituem avisos oficiais. A API de previsão escolhida não oferece alertas oficiais integrados; consulte os avisos do [INMET](https://avisos.inmet.gov.br/) em situações reais. Os níveis são ocultados se a consulta local tiver mais de 30 minutos e a pessoa ainda não tiver atualizado.

## Orientações de Segurança

Após o cadastro, use **Orientações** na navegação para consultar as cinco situações sem registrar alertas. Após **Confirmar registro**, **Ver orientações de segurança** abre a orientação do tipo salvo. A consulta não altera o histórico nem notifica órgãos oficiais.

Cada situação tem prevenção, ações durante a ocorrência, cuidados posteriores e busca de ajuda. Prevenção e cuidados posteriores são expansíveis. As recomendações e fontes oficiais são incluídas no aplicativo; os links abrem em nova aba. Não há garantia de abertura offline: a aplicação não tem service worker.

Para validar: inicie `npm run dev`, execute `node scripts/check-safety.mjs` e `node scripts/check-ui.mjs`. Para outra porta, defina a variável `SOS_URL` com a URL completa. Os roteiros usam apenas perfis fictícios no navegador de teste.

## Sair do perfil

Em **Meus dados / Perfil**, toque em **Sair**. A identificação é local, sem senha, token ou autenticação em servidor. A saída remove apenas `sos-localiza-profile` e retorna ao cadastro vazio. Histórico e preferência de tema são preservados; os registros continuam contendo os dados associados no momento do cadastro da ocorrência.

A saída é sincronizada entre abas e conferida ao restaurar uma página pelo histórico. Se o navegador impedir a remoção do cadastro, o perfil mostra um erro para nova tentativa, sem anunciar saída concluída.

Para testar automaticamente, inicie a aplicação e execute `node scripts/check-logout.mjs`. Use `SOS_URL` se a aplicação estiver em outra porta.

## API, configuração e verificação

### Produção na Vercel

O app publicado em `https://soslocalizanext.vercel.app` usa `/api/v1` na mesma origem. O `vercel.json` encaminha POST de alertas e health para a API HTTPS da Central, evitando chamadas ao localhost do visitante e preflight entre domínios. A Central continua sendo o único backend que acessa o Oracle.

No projeto Vercel do app, deixe `VITE_API_BASE_URL=/api/v1` ou remova a variável. Configurações antigas de localhost e a URL padrão da Central são normalizadas para esse proxy no build de produção; configurações explícitas de outra API continuam sendo respeitadas e exigem CORS correspondente. Se alterar o domínio da Central, atualize também o destino dos rewrites. Em desenvolvimento local, o padrão continua sendo `http://localhost:3001/api/v1`.

Depois do deploy, `/api/v1/health` no domínio do app deve retornar o estado do Oracle. Envie um alerta fictício, aguarde o UUID e confira o mesmo protocolo na Central. Nenhuma credencial é configurada neste frontend.

O contrato único está em [API.md](../sos-localiza-central/docs/API.md), e a operação do backend em [RUNBOOK.md](../sos-localiza-central/docs/RUNBOOK.md). Copie `.env.example` para `.env` e ajuste `VITE_API_BASE_URL` se necessário (padrão `http://localhost:3001/api/v1`). Reinicie o Vite após mudar a configuração. O backend precisa permitir a origem exata do app em `CORS_ORIGINS`. Inicie este app com `npm run dev -- --host localhost --port 5173`.

Nenhuma credencial Oracle ou chave de operador é necessária no app. Elas pertencem exclusivamente ao backend/operador da Central; nunca use variáveis VITE para segredos. O POST público deste protótipo não autentica cidadãos; utilize somente dados fictícios até implementar autenticação e controles de produção.

Falhas HTTP, de rede e timeout ficam visíveis na revisão. A tentativa preserva os dados e o mesmo `requestId`; **Tentar novamente** recupera a confirmação sem duplicar um alerta possivelmente gravado. Enquanto o resultado for incerto, a edição fica bloqueada; uma recusa de validação (400/413, sem gravação) permite corrigir dados mantendo o identificador. A tentativa pendente dura esta sessão da página: mantenha a página aberta até obter confirmação. O logout continua removendo apenas o cadastro local; não apaga alertas já gravados no Oracle.

Execute `npm run build`, `node scripts/check-api.mjs` e, com Vite ativo, `node scripts/check-alert-api.mjs`. Os dois novos testes usam respostas de teste identificadas e não comprovam persistência Oracle. Os roteiros antigos que esperavam confirmação somente local precisam de uma API de teste ou backend real para a etapa de envio. Para validar a integração real, configure o backend conforme o runbook, envie um alerta fictício pelo app e confira o mesmo UUID no painel e no Oracle. Sem conexão Oracle válida, a API deve retornar 503 e o app deve continuar na revisão, sem adicionar confirmação ao histórico.
