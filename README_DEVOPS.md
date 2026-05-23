# SOS Localiza — DevOps (API Java / Spring Boot)

Este repositório é a **API Java** usada na Sprint 4 DevOps. Siga o guia geral em  
`devops/cp5_devops/GUIA_CICD_AZURE.md` (Azure DevOps + ACR + App Service).  
Aqui a stack é **Maven + Docker + Oracle FIAP**, não Node.

## O que fazer primeiro (ordem)

### 1. Validar localmente

```powershell
cd API_JAVA\SosLocaliza
$env:SPRING_PROFILES_ACTIVE="test"
.\mvnw.cmd test
.\mvnw.cmd package -DskipTests
```

### 2. GitHub

Envie **esta pasta** (`SosLocaliza`) como raiz do repositório no GitHub, com:

- `azure-pipelines.yml`
- `Dockerfile`
- `mvnw`, `pom.xml`, `src/`

### 3. Azure DevOps

Crie o projeto → **Pipelines** → **New pipeline** → GitHub → repositório →  
**Existing Azure Pipelines YAML file** → `/azure-pipelines.yml`

### 4. Azure (portal) — seção 3 do guia

| Recurso | Exemplo de nome |
|---------|-----------------|
| Resource Group | `rg-soslocaliza` |
| ACR | `soslocalizaacr` → anote `soslocalizaacr.azurecr.io` |
| Web App staging | `soslocaliza-api-staging` (Linux, **Docker Container**) |
| Web App produção | `soslocaliza-api-prod` |

Região sugerida: **brazilsouth** (se `eastus` estiver bloqueada).

### 5. App Settings (cada Web App)

| Nome | Valor |
|------|--------|
| `WEBSITES_PORT` | `8080` |
| `SPRING_PROFILES_ACTIVE` | `oracle-fiap` |
| `SERVER_PORT` | `8080` |
| `ORACLE_HOST` | `oracle.fiap.com.br` |
| `ORACLE_PORT` | `1521` |
| `ORACLE_SID` | `ORCL` |
| `ORACLE_USERNAME` | seu RM |
| `ORACLE_PASSWORD` | *** (secret no DevOps ou App Settings) |
| `TWILIO_ENABLED` | `false` |

### 6. Service connections + variáveis (seções 5–7 do guia)

Variáveis da pipeline:

- `ACR_LOGIN_SERVER`
- `ACR_SERVICE_CONNECTION`
- `AZURE_SUBSCRIPTION`
- `RESOURCE_GROUP`
- `STAGING_WEBAPP_NAME`
- `PRODUCTION_WEBAPP_NAME`

Environments: `staging`, `production`.

### 7. Managed Identity + AcrPull

Siga a **seção 10.4** do `GUIA_CICD_AZURE.md` em **cada** Web App.

### 8. Testar

- Pipeline verde no Azure DevOps  
- `https://<seu-app>.azurewebsites.net/actuator/health`  
- `https://<seu-app>.azurewebsites.net/login`  
- Pedido de socorro → dado persistido no **Oracle** (vídeo da entrega)

## Pipeline (resumo)

| Estágio | Ação |
|---------|------|
| **CI Build** | `mvn test` (H2) → `mvn package` → build Docker |
| **CD Staging** | push imagem `*-staging` → Web App staging |
| **CD Production** | push imagem `*-prod` → Web App produção |

## Banco (2+ tabelas relacionadas)

Oracle FIAP — migrations em `src/main/resources/db/migration/oracle/`  
(ex.: eventos, SMS, localização com relacionamentos).

## Só CI (sem ACR ainda)

Comente os estágios `DeployStaging` e `DeployProduction` no `azure-pipelines.yml` e rode só o estágio **Build**.

## Links da entrega

- GitHub: _preencher_
- Vídeo YouTube: _preencher_
