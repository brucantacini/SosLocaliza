# Scripts JSON — CRUD da API (Sprint 4 DevOps)

Bodies prontos para importar no **Postman** ou **Insomnia** (aba Body → raw → JSON).

**Base URL (produção):** `https://soslocaliza-api-prod-560242.azurewebsites.net`  
**Autenticação:** HTTP Basic — `admin`/`password` (eventos POST) ou `citizen`/`password` (SMS)

| Arquivo | Método | Endpoint |
|---------|--------|----------|
| `evento-create.json` | POST | `/api/eventos/add` |
| `evento-update.json` | PUT | `/api/eventos/update/{id}` |
| `sms-create.json` | POST | `/api/sms` |
| `sms-update.json` | PUT | `/api/sms/update/{id}` |
| `localizacao-create.json` | POST | `/api/procedures/localizacao` |
| `localizacao-update.json` | PUT | `/api/procedures/localizacao/{id}` |
| `usuario-create.json` | POST | `/api/procedures/usuario` |
| `usuario-update.json` | PUT | `/api/procedures/usuario/{id}` |

**Coleções Postman completas (raiz do repo):**

- `postman-collection.json` — eventos e SMS
- `postman-procedures-collection.json` — procedures localização/usuário

Substitua `{id}` pelo ID retornado no CREATE. Em `sms-create.json`, use um `idEvento` existente (GET `/api/eventos/ativos`).
