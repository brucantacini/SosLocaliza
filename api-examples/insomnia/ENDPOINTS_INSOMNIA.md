# Endpoints — ordem sugerida de testes (Insomnia / Postman)

**Variável:** `baseUrl` = `https://soslocaliza-api-prod-560242.azurewebsites.net`  
**Auth da pasta/collection:** Basic Auth `citizen` / `password` (admin para criar evento)

## 1. Sem autenticação

| # | GET | `{{baseUrl}}/actuator/health` |

## 2. Eventos (tabela `T_SOS_EVENTO`)

| # | Método | URL | Body |
|---|--------|-----|------|
| 1 | GET | `/api/eventos/ativos` | — |
| 2 | POST | `/api/eventos/add` | `api-examples/crud/evento-create.json` (user **admin**) |
| 3 | GET | `/api/eventos/getById/1` | — |
| 4 | PUT | `/api/eventos/update/1` | `api-examples/crud/evento-update.json` |

## 3. SMS (tabela `T_SOS_SMS`, FK `ID_EVENTO`)

| # | Método | URL | Body |
|---|--------|-----|------|
| 5 | POST | `/api/sms` | `api-examples/crud/sms-create.json` |
| 6 | GET | `/api/sms/getAll?page=0&size=10` | — |
| 7 | PUT | `/api/sms/update/1` | `api-examples/crud/sms-update.json` |

## 4. Procedures (opcional)

| # | Método | URL | Body |
|---|--------|-----|------|
| 8 | POST | `/api/procedures/localizacao` | `localizacao-create.json` |
| 9 | PUT | `/api/procedures/localizacao/1` | `localizacao-update.json` |
| 10 | POST | `/api/procedures/usuario` | `usuario-create.json` |

**Import rápido:** use `postman-collection.json` e `postman-procedures-collection.json` na raiz do repositório.
