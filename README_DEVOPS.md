# SOS Localiza — DevOps, deploy na nuvem e testes da API

Este documento atende à **Sprint DevOps**: reprodução do **deploy**, **execução da aplicação/API** e **testes** com base no repositório GitHub, incluindo **scripts JSON de CRUD** para Postman/curl.

Para **descrição do projeto**, **stack Java**, **configuração Twilio/Oracle** e **como rodar localmente com Maven**, consulte o **[README.md](README.md)**.

---

## 1. O que deve existir no GitHub (execução e deploy)


| Item            | Descrição                                                                             |
| --------------- | ------------------------------------------------------------------------------------- |
| Código-fonte    | `src/main/java`, `src/main/resources`, `src/test`                                     |
| Build           | `pom.xml`, `mvnw`, `mvnw.cmd`                                                         |
| Banco           | Migrations Flyway em `src/main/resources/db/migration/` (`h2` e `oracle`)             |
| Pipeline        | `azure-pipelines.yml` na **raiz** (mesmo nível do `pom.xml`)                          |
| Docker          | `Dockerfile`, `docker-compose.yml` (se usar container)                                |
| CRUD (API)      | Pasta `**api-examples/crud/`** com JSON listados na seção 6                           |
| Coleção Postman | `postman-collection.json` (opcional; ajuste `base_url` e corpos SMS conforme seção 5) |
| Exemplo de env  | `.env.example` (sem senhas reais)                                                     |


**Clone:**

```bash
git clone https://github.com/brucantacini/SosLocaliza.git
cd SosLocaliza
```

---

## 2. Variáveis de ambiente (Oracle / Azure App Service)

No **Azure Portal** → App Service → **Configuration** (ou `az webapp config appsettings set`), exemplos:


| Variável                 | Exemplo / notas                                             |
| ------------------------ | ----------------------------------------------------------- |
| `SPRING_PROFILES_ACTIVE` | `oracle-fiap`                                               |
| `ORACLE_HOST`            | `oracle.fiap.com.br`                                        |
| `ORACLE_PORT`            | `1521`                                                      |
| `ORACLE_SID`             | `ORCL`                                                      |
| `ORACLE_USERNAME`        | usuário do schema (ex.: `rm560242`)                         |
| `ORACLE_PASSWORD`        | **definir no portal/Key Vault — não versionar**             |
| `TWILIO_ENABLED`         | `false` para simulação de SMS na nuvem                      |
| `SERVER_PORT`            | `8080` (App Service costuma expor na 80/443 para o cliente) |


**Startup (Linux Java):** se a app mostrar página padrão em vez do JAR, configure startup, por exemplo:

`java -jar /home/site/wwwroot/*.jar`

---

## 3. Pipeline CI/CD — Azure DevOps (`azure-pipelines.yml`)

### O que a pipeline faz

- **CI:** agente Ubuntu instala JDK/Maven, executa `mvn clean package` (com fallback Java 17 se 21 não estiver disponível) e publica o JAR `SosLocaliza-0.0.1-SNAPSHOT.jar`.
- **CD:** baixa o artefato e executa `az webapp deploy --type jar` no **Azure App Service** configurado nas variáveis do YAML.

### Variáveis no repositório (ajuste ao seu ambiente)

- `AZURE_SERVICE_CONNECTION` — nome exato da **Service connection** (Azure Resource Manager) no Azure DevOps.
- `AZURE_RESOURCE_GROUP`, `AZURE_WEBAPP_NAME`, `jarName`.

### Pré-requisitos

1. Organização e projeto em [Azure DevOps](https://dev.azure.com).
2. **Service connection** em *Project settings → Service connections → Azure Resource Manager*.
3. Contas novas podem precisar solicitar paralelismo: [aka.ms/azpipelines-parallelism-request](https://aka.ms/azpipelines-parallelism-request).

### Passo a passo

1. **Pipelines → New pipeline**.
2. Conecte o **GitHub** e selecione o repositório e a branch (`main` / `master`).
3. **Existing Azure Pipelines YAML file** → `azure-pipelines.yml`.
4. Confirme o nome da Service connection.
5. **Save and run**.

---

## 4. Deploy manual — Azure Cloud Shell (Bash)

Alternativa quando não se usa Azure DevOps ou para reprodução pontual:

```bash
RG=rg-soslocaliza
LOC=brazilsouth
PLAN=plan-soslocaliza
APP=app-soslocaliza-560242

az group create --name $RG --location $LOC

az appservice plan create --name $PLAN --resource-group $RG --location $LOC --is-linux --sku B1

az webapp create --resource-group $RG --plan $PLAN --name $APP --runtime "JAVA|17-java17"

git clone https://github.com/brucantacini/SosLocaliza.git && cd SosLocaliza
chmod +x mvnw
./mvnw clean package -DskipTests -Dmaven.compiler.release=17 -Djava.version=17

az webapp deploy --resource-group $RG --name $APP --src-path target/SosLocaliza-0.0.1-SNAPSHOT.jar --type jar

az webapp config appsettings set --resource-group $RG --name $APP --settings \
  SPRING_PROFILES_ACTIVE=oracle-fiap \
  ORACLE_HOST=oracle.fiap.com.br ORACLE_PORT=1521 ORACLE_SID=ORCL \
  ORACLE_USERNAME=SEU_USUARIO ORACLE_PASSWORD='SUA_SENHA' \
  TWILIO_ENABLED=false SERVER_PORT=8080

az webapp config set --resource-group $RG --name $APP \
  --startup-file "java -jar /home/site/wwwroot/*.jar"

az webapp restart --resource-group $RG --name $APP
```

**URL pública (exemplo):** `https://app-soslocaliza-560242.azurewebsites.net` — API REST em `**/api/...`** (veja seção 5).

**Logs:**

```bash
az webapp log tail --resource-group $RG --name $APP
```

---

## 5. Postman / Insomnia — URL base e autenticação

**Erro comum:** chamar `https://...azurewebsites.net/eventos/...` → use sempre o prefixo `**/api`**.

- Defina `**base_url**` = `https://<seu-app>.azurewebsites.net/api` (com `**/api` no final**).
- Exemplo: `GET {{base_url}}/eventos/getAll?page=0&size=10&direction=ASC`
→ `https://app-soslocaliza-560242.azurewebsites.net/api/eventos/getAll?...`

**Authorization:** tipo **Basic Auth** — usuário `admin`, senha `password` (padrão das migrations; ajuste se alterou no banco).

- Rotas de **escrita** em eventos e **todas** as rotas de **SMS** exigem `**ROLE_ADMIN`** (`admin`).
- `citizen` / `password`: pedido de socorro e alguns GET de eventos.

**Corpo JSON:** `Content-Type: application/json`. Para SMS use `**numeroTelefone`** (8 ou 9 dígitos) e `**idEvento**` obrigatório — não use apenas o campo `numero`.

Após importar `postman-collection.json`, confira se `base_url` termina em `/api` e se os bodies de SMS estão corretos.

---

## 6. Scripts JSON do CRUD (`api-examples/crud/`)

Use com **Postman** (Body → raw → importar arquivo) ou **curl** `-d @arquivo.json` a partir da pasta `api-examples/crud`.


| Arquivo                   | Método | Endpoint                           |
| ------------------------- | ------ | ---------------------------------- |
| `evento-create.json`      | POST   | `/api/eventos/add`                 |
| `evento-update.json`      | PUT    | `/api/eventos/update/{id}`         |
| `sms-create.json`         | POST   | `/api/sms`                         |
| `sms-update.json`         | PUT    | `/api/sms/update/{id}`             |
| `localizacao-create.json` | POST   | `/api/procedures/localizacao`      |
| `localizacao-update.json` | PUT    | `/api/procedures/localizacao/{id}` |
| `usuario-create.json`     | POST   | `/api/procedures/usuario`          |
| `usuario-update.json`     | PUT    | `/api/procedures/usuario/{id}`     |


**Sem corpo JSON (exemplos):**

- GET `/api/eventos/getById/{id}`, GET `/api/eventos/getAll?page=0&size=10&direction=ASC`, DELETE `/api/eventos/delete/{id}`
- GET `/api/sms/getById/{id}`, GET `/api/sms/getAll?page=0&size=10&direction=DESC`, DELETE `/api/sms/delete/{id}`
- DELETE `/api/procedures/localizacao/{id}`, DELETE `/api/procedures/usuario/{id}`

**Exemplo curl (local Oracle — porta 8081):**

```bash
cd api-examples/crud
curl -u admin:password -X POST "http://localhost:8081/api/eventos/add" \
  -H "Content-Type: application/json" \
  -d @evento-create.json
```

**Exemplo na nuvem:** troque a URL por `https://<app>.azurewebsites.net/api/eventos/add` e mantenha `-u admin:password`.

> Endpoints `/api/procedures/*` exigem **Oracle** configurado (`oracle-fiap`). Com perfil **dev** (H2), use eventos/SMS conforme README principal.

---

## 7. Comandos rápidos após deploy ou em local


| Objetivo          | Comando                                                                                       |
| ----------------- | --------------------------------------------------------------------------------------------- |
| Testes unitários  | `mvn clean verify` ou `mvn test`                                                              |
| JAR rápido        | `mvn clean package -DskipTests`                                                               |
| Subir H2          | `./mvnw spring-boot:run` (perfil padrão `dev`, porta **8080**)                                |
| Subir Oracle FIAP | `./mvnw spring-boot:run -Dspring-boot.run.profiles=oracle-fiap` (porta **8081** + `ORACLE_*`) |
| Health            | `curl http://localhost:8080/actuator/health` ou `8081`                                        |


---

## 8. Testes com curl (referência completa)

**Base:** `http://localhost:8081` (Oracle local) ou URL do Azure. API: prefixo `**/api`**. Use `-u admin:password` quando necessário.

### Health

```bash
curl http://localhost:8081/actuator/health
```

### Eventos

```bash
curl -u admin:password -X POST http://localhost:8081/api/eventos/add \
  -H "Content-Type: application/json" \
  -d '{"nomeEvento":"Enchente na Região Sul","descricaoEvento":"Alagamento severo","causas":"Chuva intensa","alertas":"Rio subindo","acoesAntes":"Evacuar","acoesDurante":"Não atravessar alagamentos","acoesDepois":"Verificar estruturas"}'

curl -u admin:password "http://localhost:8081/api/eventos/getAll?page=0&size=10&direction=ASC"

curl -u admin:password http://localhost:8081/api/eventos/getById/1

curl -u admin:password -X PUT http://localhost:8081/api/eventos/update/1 \
  -H "Content-Type: application/json" \
  -d '{"nomeEvento":"Enchente - ATUALIZADO","descricaoEvento":"Atualizado","causas":"Chuva","alertas":"Estável","acoesAntes":"Evacuar","acoesDurante":"Cuidado","acoesDepois":"Vistoria","ativo":true}'

curl -u admin:password -X DELETE http://localhost:8081/api/eventos/delete/1
```

### SMS

```bash
curl -u admin:password -X POST http://localhost:8081/api/sms \
  -H "Content-Type: application/json" \
  -d '{"remetente":"Defesa Civil","ddd":"11","numeroTelefone":"999999999","mensagem":"ALERTA: teste.","idEvento":1}'

curl -u admin:password "http://localhost:8081/api/sms/getAll?page=0&size=10&direction=DESC"
```

### Procedures Oracle (localização / usuário)

```bash
curl -u admin:password -X POST http://localhost:8081/api/procedures/localizacao \
  -H "Content-Type: application/json" \
  -d '{"nomeLocal":"Praça da Sé","ruaLocal":"Praça da Sé","numeroLocal":100,"cepLocal":"01001000"}'

curl -u admin:password -X PUT http://localhost:8081/api/procedures/localizacao/1 \
  -H "Content-Type: application/json" \
  -d '{"nomeLocal":"Praça da Sé - Atualizado","ruaLocal":"Praça da Sé","numeroLocal":200,"cepLocal":"01001000"}'

curl -u admin:password -X DELETE http://localhost:8081/api/procedures/localizacao/1

curl -u admin:password -X POST http://localhost:8081/api/procedures/usuario \
  -H "Content-Type: application/json" \
  -d '{"nomeUsuario":"João Silva","cpfUsuario":"12345678901","emailUsuario":"joao@example.com","telefoneUsuario":"11999999999"}'

curl -u admin:password -X DELETE http://localhost:8081/api/procedures/usuario/1
```

---

## 9. Docker e VM

### Pré-requisitos

- Docker e Docker Compose (ou plugin)
- Java 21 e Maven para build local do JAR

### Build e Compose (local)

```bash
mvn clean package -DskipTests
docker build -t soslocaliza:latest .
docker compose up -d
docker compose logs -f soslocaliza
docker compose down
```

### VM (Ubuntu) — resumo

Instale Git, OpenJDK 21, Maven, Docker; clone o repositório; `mvn clean package -DskipTests`; `docker build` e `docker compose up -d`.

**Firewall (Azure NSG):** liberar TCP **8081** se acessar pela VM.

**Health na VM:**

```bash
curl http://localhost:8081/actuator/health
curl http://<IP_PUBLICO_VM>:8081/actuator/health
```

Resposta esperada: `{"status":"UP"}`.

### Troubleshooting Docker

```bash
sudo docker compose logs -f soslocaliza
sudo netstat -tulpn | grep 8081
sudo docker compose ps
```

- Banco: credenciais Oracle no `docker-compose.yml` / variáveis de ambiente; teste `telnet oracle.fiap.com.br 1521`.
- Permissão Docker: `sudo usermod -aG docker $USER` ou usar `sudo` nos comandos.

---

## 11. Documentação relacionada

- **[README.md](README.md)** — projeto, tecnologias, execução Maven, Twilio, Oracle/H2, interface web, tabelas de endpoints.
- **[TESTES.md](TESTES.md)** — cenários de teste adicionais.

