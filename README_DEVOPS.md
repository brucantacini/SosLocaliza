# SOS Localiza — Guia DevOps (Sprint 4)

Este documento é o **manual para reproduzir** a entrega DevOps: pipeline Azure, deploy da API, testes com Postman/curl e persistência no Oracle.


| Documento                                                                                              | Uso                                                               |
| ------------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------- |
| **[README.md](README.md)**                                                                             | API Java — produto, endpoints, autenticação, rodar local          |
| **Este arquivo**                                                                                       | CI/CD, Azure, Docker, testes da entrega                           |
| **[devops/sprint4/DISSERTACAO_PIPELINE_CI_CD.md](../../devops/sprint4/DISSERTACAO_PIPELINE_CI_CD.md)** | Dissertação das etapas da pipeline (20 pts)                       |
| **Diagrama**                                                                                           | `devops/sprint4/CI_CD pipeline and runtime architecture copy.png` |


---

## Índice

1. [Resumo da solução (item 1 da rubrica)](#1-resumo-da-solução-item-1-da-rubrica)
2. [Links da entrega](#2-links-da-entrega)
3. [Arquitetura em produção](#3-arquitetura-em-produção)
4. [Checklist rápido — reproduzir em 15 minutos](#4-checklist-rápido--reproduzir-em-15-minutos)
5. [Pipeline CI/CD (itens 2 e 3)](#5-pipeline-cicd-itens-2-e-3)
6. [Configuração Azure passo a passo](#6-configuração-azure-passo-a-passo)
7. [Rodar localmente (antes do deploy)](#7-rodar-localmente-antes-do-deploy)
8. [Testar a API — Postman e curl (item 5)](#8-testar-a-api--postman-e-curl-item-5)
9. [Scripts JSON de CRUD (item 5)](#9-scripts-json-de-crud-item-5)
10. [Banco de dados na nuvem (item 6)](#10-banco-de-dados-na-nuvem-item-6)
11. [Roteiro do vídeo (item 4)](#11-roteiro-do-vídeo-item-4)
12. [Problemas comuns](#12-problemas-comuns)

---

## 1. Resumo da solução (item 1 da rubrica)

**SOS Localiza** é uma API **Java Spring Boot** para gestão de **emergências climáticas**:

- Cadastro de **eventos de risco** (enchente, deslizamento, etc.).
- **Pedidos de socorro por SMS** (registro no banco; envio simulado).
- Integração com **app mobile/web** (Expo + Firebase Hosting).
- Dados persistidos no **Oracle FIAP** (nuvem acadêmica).

A esteira **CI/CD no Azure DevOps** automatiza: testes → build Maven → imagem Docker → deploy em **App Service** (staging e produção).

---

## 2. Links da entrega

### Aplicações em nuvem


| Ambiente         | Descrição                   | URL                                                                                                                                                  |
| ---------------- | --------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Staging**      | API Java — login web        | [https://soslocaliza-api-staging-560242.azurewebsites.net/login](https://soslocaliza-api-staging-560242.azurewebsites.net/login)                     |
| **Staging**      | API Java — health           | [https://soslocaliza-api-staging-560242.azurewebsites.net/actuator/health](https://soslocaliza-api-staging-560242.azurewebsites.net/actuator/health) |
| **Produção**     | API Java — login web        | [https://soslocaliza-api-prod-560242.azurewebsites.net/login](https://soslocaliza-api-prod-560242.azurewebsites.net/login)                           |
| **Produção**     | API Java — health           | [https://soslocaliza-api-prod-560242.azurewebsites.net/actuator/health](https://soslocaliza-api-prod-560242.azurewebsites.net/actuator/health)       |
| **Produção**     | API Java — base             | [https://soslocaliza-api-prod-560242.azurewebsites.net](https://soslocaliza-api-prod-560242.azurewebsites.net)                                       |
| **Mobile / Web** | App Expo (Firebase Hosting) | [https://sos-localiza.web.app/](https://sos-localiza.web.app/)                                                                                       |


### Repositório e documentação


| Item                       | Link / caminho                                                                             |
| -------------------------- | ------------------------------------------------------------------------------------------ |
| GitHub (código + pipeline) | [https://github.com/brucantacini/SosLocaliza](https://github.com/brucantacini/SosLocaliza) |
| Vídeo YouTube              | *preencher no PDF da equipe*                                                               |
| Pipeline YAML              | `azure-pipelines.yml` (raiz do repo)                                                       |
| Dissertação pipeline       | `devops/sprint4/DISSERTACAO_PIPELINE_CI_CD.md`                                             |
| Diagrama CI/CD             | `devops/sprint4/CI_CD pipeline and runtime architecture copy.png`                          |


**Credenciais de demonstração:** `admin` / `password` ou `citizen` / `password`

---

## 3. Arquitetura em produção

```
[GitHub] ──push──> [Azure DevOps Pipeline]
                         │
                         ├─ CI: mvn test (H2) → mvn package → docker build
                         │
                         ├─ CD Staging → ACR → Web App staging
                         │
                         └─ CD Production → ACR → Web App produção
                                              │
                                              v
                                    [Oracle FIAP - nuvem]
                                    T_SOS_EVENTO ↔ T_SOS_SMS

[App web Firebase] ──HTTPS──> [API Azure App Service]
```

---

## 4. Checklist rápido — reproduzir em 15 minutos

Marque na ordem:

- **1.** Abrir GitHub e confirmar `azure-pipelines.yml`, `Dockerfile`, `pom.xml`, pasta `api-examples/crud/`
- **2.** Azure DevOps → Pipelines → última execução **verde** (branch `main`)
- **3.** Abrir health **staging** ou **produção** → status `UP` (pode demorar ~1 min se API estava parada)  
  - Staging: [https://soslocaliza-api-staging-560242.azurewebsites.net/actuator/health](https://soslocaliza-api-staging-560242.azurewebsites.net/actuator/health)  
  - Produção: [https://soslocaliza-api-prod-560242.azurewebsites.net/actuator/health](https://soslocaliza-api-prod-560242.azurewebsites.net/actuator/health)
- **4.** Postman: GET `/api/eventos/ativos` com Basic Auth `citizen` / `password` → **200**
- **5.** Postman: POST `/api/sms` com JSON (ver [seção 8](#8-testar-a-api--postman-e-curl-item-5)) → **201**
- **6.** Oracle: consultar `T_SOS_SMS` e ver novo registro (vídeo / SQL Developer)
- **7.** Opcional: abrir app web [https://sos-localiza.web.app/](https://sos-localiza.web.app/) e login `citizen` / `password`

---

## 5. Pipeline CI/CD (itens 2 e 3)

### Arquivo

`azure-pipelines.yml` na **raiz** do repositório.

### Gatilho

Push nas branches `main` ou `master`.

### Estágios


| #   | Estágio             | O que faz                                                                            |
| --- | ------------------- | ------------------------------------------------------------------------------------ |
| 1   | **CI — Build**      | `mvn test` (perfil `test`, H2) → `mvn package` → `docker build` → push imagem no ACR |
| 2   | **CD — Staging**    | Deploy imagem `*-staging` no Web App de staging                                      |
| 3   | **CD — Production** | Deploy imagem `*-prod` no Web App de produção                                        |


### Variáveis necessárias no Azure DevOps

Configure em **Pipelines → Library** (ou variáveis do pipeline):


| Variável                 | Exemplo / descrição               |
| ------------------------ | --------------------------------- |
| `ACR_LOGIN_SERVER`       | `soslocalizaacr.azurecr.io`       |
| `ACR_SERVICE_CONNECTION` | Nome da service connection do ACR |
| `AZURE_SUBSCRIPTION`     | Nome da subscription no Azure     |
| `RESOURCE_GROUP`         | `rg-soslocaliza`                  |
| `STAGING_WEBAPP_NAME`    | `soslocaliza-api-staging-560242`  |
| `PRODUCTION_WEBAPP_NAME` | `soslocaliza-api-prod-560242`     |


**Environments:** `staging`, `production` (com aprovação opcional em produção).

### Conectar pipeline ao GitHub

1. Azure DevOps → **Pipelines** → **New pipeline**
2. **GitHub** → repositório `brucantacini/SosLocaliza`
3. **Existing Azure Pipelines YAML file** → `/azure-pipelines.yml`
4. Salvar e executar

### Só CI (sem deploy)

Comente os estágios `DeployStaging` e `DeployProduction` no YAML e rode apenas o estágio **Build**.

---

## 6. Configuração Azure passo a passo

### Recursos criados


| Recurso                  | Nome exemplo                     |
| ------------------------ | -------------------------------- |
| Resource Group           | `rg-soslocaliza`                 |
| Container Registry (ACR) | `soslocalizaacr`                 |
| Web App staging          | `soslocaliza-api-staging-560242` |
| Web App produção         | `soslocaliza-api-prod-560242`    |


Região sugerida: **Brazil South** (`brazilsouth`) ou southafricanorth

Tipo do Web App: **Linux**, publicação **Docker Container** (imagem do ACR).

### App Settings (cada Web App)


| Nome                     | Valor                                   |
| ------------------------ | --------------------------------------- |
| `WEBSITES_PORT`          | `8080`                                  |
| `SPRING_PROFILES_ACTIVE` | `oracle-fiap`                           |
| `SERVER_PORT`            | `8080`                                  |
| `ORACLE_HOST`            | `oracle.fiap.com.br`                    |
| `ORACLE_PORT`            | `1521`                                  |
| `ORACLE_SID`             | `ORCL`                                  |
| `ORACLE_USERNAME`        | RM do aluno (ex.: `rm560242`)           |
| `ORACLE_PASSWORD`        | Senha Oracle (**secret**, não commitar) |


### Permissão ACR → Web App

Em cada Web App: **Identity** → System assigned **On** → **AcrPull** no Container Registry (seção Managed Identity do guia FIAP).

---

## 7. Rodar localmente (antes do deploy)

```powershell
cd API_JAVA\SosLocaliza

# Testes (H2, perfil test)
$env:SPRING_PROFILES_ACTIVE="test"
.\mvnw.cmd test

# Empacotar
.\mvnw.cmd package -DskipTests

# Subir com H2 (sem Oracle)
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw.cmd spring-boot:run
# http://localhost:8082/actuator/health
```

### Docker local (opcional)

```powershell
docker build -t soslocaliza:local .
docker run -p 8080:8080 `
  -e SPRING_PROFILES_ACTIVE=oracle-fiap `
  -e ORACLE_PASSWORD=sua_senha `
  -e ORACLE_USERNAME=rm560242 `
  soslocaliza:local
```

`docker-compose.yml` na raiz do projeto (Oracle via variáveis de ambiente).

---

## 8. Testar a API — Postman e curl (item 5)

**Base URL produção:**  
`https://soslocaliza-api-prod-560242.azurewebsites.net`

### Configurar autenticação no Postman

1. Crie uma **Collection** `SOS Localiza`
2. Aba **Authorization** da collection:
  - Type: **Basic Auth**
  - Username: `citizen`
  - Password: `password`
3. Variável da collection: `baseUrl` = URL acima

> A API **não** tem `POST /api/login`. O login é **HTTP Basic** em todas as rotas `/api/...` protegidas.

### Requests essenciais

#### 1 — Health (sem auth)


| Campo | Valor                         |
| ----- | ----------------------------- |
| GET   | `{{baseUrl}}/actuator/health` |
| Auth  | No Auth                       |


#### 2 — Listar eventos ativos (valida login)


| Campo | Valor                            |
| ----- | -------------------------------- |
| GET   | `{{baseUrl}}/api/eventos/ativos` |
| Auth  | Herda Basic da collection        |


Resposta **200** = usuário/senha OK.

#### 3 — Perfil mobile


| Campo | Valor                       |
| ----- | --------------------------- |
| GET   | `{{baseUrl}}/api/mobile/me` |
| Auth  | Basic                       |


#### 4 — Enviar SMS (persiste no Oracle)


| Campo  | Valor                            |
| ------ | -------------------------------- |
| POST   | `{{baseUrl}}/api/sms`            |
| Auth   | Basic (`citizen` ou `admin`)     |
| Header | `Content-Type: application/json` |
| Body   | raw JSON                         |


```json
{
  "remetente": "Maria Silva",
  "ddd": "11",
  "numeroTelefone": "999998888",
  "mensagem": "Preciso de ajuda - enchente na região.",
  "idEvento": 1
}
```

Use um `idEvento` existente (obtido no GET `/api/eventos/ativos`).

#### 5 — Cadastro (público, sem auth)


| Campo | Valor                             |
| ----- | --------------------------------- |
| POST  | `{{baseUrl}}/api/mobile/cadastro` |
| Auth  | No Auth                           |


```json
{
  "nomeCompleto": "João Teste",
  "cpf": "12345678909",
  "senha": "senha123",
  "telefone": "11999998888",
  "cep": "01310100"
}
```

Depois use Basic Auth com username = CPF e senha cadastrada.

### curl (PowerShell)

```powershell
# Health
curl https://soslocaliza-api-prod-560242.azurewebsites.net/actuator/health

# Eventos (Basic Auth)
curl -u citizen:password https://soslocaliza-api-prod-560242.azurewebsites.net/api/eventos/ativos
```

---

## 9. Scripts JSON de CRUD (item 5)

### Insomnia (collection completa)


| Arquivo                                                | Uso                                                |
| ------------------------------------------------------ | -------------------------------------------------- |
| `**api-examples/insomnia/SOS_Localiza.insomnia.json**` | Import no Insomnia — todas as requests prontas     |
| `**api-examples/insomnia/ENDPOINTS_INSOMNIA.md**`      | Documento com lista de endpoints e ordem de testes |


### JSON avulsos (CRUD)

Pasta: `**api-examples/crud/**`


| Arquivo                   | Endpoint sugerido                      |
| ------------------------- | -------------------------------------- |
| `evento-create.json`      | POST `/api/eventos/add` (ADMIN)        |
| `evento-update.json`      | PUT `/api/eventos/update/{id}`         |
| `sms-create.json`         | POST `/api/sms`                        |
| `sms-update.json`         | PUT `/api/sms/update/{id}`             |
| `localizacao-create.json` | POST `/api/procedures/localizacao`     |
| `localizacao-update.json` | PUT `/api/procedures/localizacao/{id}` |
| `usuario-create.json`     | POST `/api/procedures/usuario`         |
| `usuario-update.json`     | PUT `/api/procedures/usuario/{id}`     |


No Postman: **Import** → selecione o JSON como body da request.

---

## 10. Banco de dados na nuvem (item 6)


| Requisito               | Como atendemos                                       |
| ----------------------- | ---------------------------------------------------- |
| Banco em nuvem          | **Oracle FIAP** (`oracle.fiap.com.br`)               |
| 2+ tabelas relacionadas | `T_SOS_EVENTO` (1) → `T_SOS_SMS` (N) via `ID_EVENTO` |
| Schema versionado       | Flyway em `src/main/resources/db/migration/oracle/`  |


### Consulta para provar persistência (vídeo / SQL Developer)

```sql
SELECT ID_SMS, REMETENTE, NUMERO_TELEFONE, MENSAGEM, ENVIADO_COM_SUCESSO, ID_EVENTO, DATA_ENVIO
FROM T_SOS_SMS
ORDER BY DATA_ENVIO DESC
FETCH FIRST 10 ROWS ONLY;
```

Após POST `/api/sms` no Postman, deve aparecer uma linha nova.

**Dúvidas sobre endpoints ou autenticação?** Consulte **[README.md](README.md)** — seção [Como autenticar](README.md#4-como-autenticar) e [API REST](README.md#8-api-rest--endpoints).