# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

Moradores de áreas sujeitas a riscos climáticos que usam principalmente o celular para consultar condições meteorológicas próximas e registrar uma ocorrência simulada.

## Product Purpose

Demonstrar como uma experiência localizada pode ajudar moradores a consultar estimativas meteorológicas do entorno e percorrer o registro de uma situação como alagamento, enchente ou risco de desmoronamento. O sucesso é a pessoa entender a localização e os limites da estimativa e conseguir concluir o fluxo simulado.

## Positioning

O protótipo reúne uma previsão meteorológica centrada na localização e um fluxo demonstrativo de registro de ocorrência no mesmo aplicativo. A estimativa não prevê enchentes ou deslizamentos e não substitui avisos oficiais.

## Operating Context

- Experiência web responsiva, projetada prioritariamente para uso em celular.
- Na abertura, a localização de demonstração em São Paulo pode ser exibida sem solicitar a posição do dispositivo. A localização real só é consultada após ação explícita e permissão do navegador.
- A pessoa pode consultar amostras meteorológicas, selecionar pontos no mapa, ver fatores e horário da consulta e percorrer um registro simulado.

## Capabilities and Constraints

- React, TypeScript e Vite compõem a aplicação web; Leaflet exibe o mapa com tiles do OpenStreetMap.
- A previsão vem da API Open-Meteo. Coordenadas do ponto consultado são transmitidas ao serviço; dados do perfil não são enviados. Os tiles do mapa são solicitados ao OpenStreetMap.
- Os níveis de atenção são estimativas heurísticas baseadas em precipitação prevista e probabilidade de chuva. A API integrada não fornece avisos oficiais.
- Perfil e histórico de ocorrências simuladas são guardados no `localStorage` do navegador atual. Não há backend nem transmissão de alertas para autoridades ou serviços de emergência.
- O ponto de demonstração é identificado como tal; indisponibilidade de dados não deve ser substituída por condições ou níveis inventados.

- Após o cadastro, Orientações de Segurança oferece conteúdo local para as cinco ocorrências, com fontes oficiais. Após a confirmação, o botão de orientações abre o conteúdo do tipo registrado. Consultar instruções não altera alertas nem substitui autoridades locais.

## Brand Commitments

- O nome do produto é SOS LOCALIZA.
- A identidade deve ser própria do produto e não reproduzir marcas ou logotipos de órgãos públicos.
- A interface usa linguagem de registro local, sem avisos acadêmicos repetitivos. Junto à confirmação, informa: “A equipe da Defesa Civil não foi notificada por este aplicativo.” Nunca sugere envio, recebimento ou atendimento oficial.

## Evidence on Hand

- O projeto contém uma aplicação funcional, uma integração de previsão Open-Meteo, mapa Leaflet/OpenStreetMap e fluxos locais de perfil e registro simulado.
- Não há validação de um modelo oficial de risco, integração com alertas da Defesa Civil ou envio real de ocorrências.
- Dados de perfil usados em demonstrações devem ser fictícios.

## Product Principles

- Identificar claramente se a localização é real ou demonstrativa.
- Separar estimativas meteorológicas de alertas oficiais e de previsões de desastres.
- Explicar a origem dos dados e tornar indisponibilidade explícita.
- Tratar dados de perfil localmente e deixar claro quando coordenadas são consultadas por serviços externos.
- Tornar a ação de registro local compreensível e informar a ausência de notificação à Defesa Civil junto à confirmação.

## Accessibility & Inclusion

- Priorizar celular, leitura rápida, texto legível, rótulos além de cor e áreas de toque confortáveis.
