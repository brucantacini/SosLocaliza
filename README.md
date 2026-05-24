# SOS Localiza — API Java (Spring Boot)

API backend do projeto **SOS Localiza**: emergências climáticas (enchentes, deslizamentos), cadastro de cidadãos, eventos de risco e **registro de pedidos de SMS** (envio simulado, persistido no Oracle).


| Documento                                | Conteúdo                                                                    |
| ---------------------------------------- | --------------------------------------------------------------------------- |
| **Este README**                          | Produto, arquitetura, autenticação, endpoints, banco, como rodar localmente |
| **[README_DEVOPS.md](README_DEVOPS.md)** | Pipeline Azure, deploy, Docker, testes Postman/curl, JSON de CRUD           |
| **[TESTES.md](TESTES.md)**               | Cenários de teste detalhados                                                |


---

## Índice

1. [Visão geral](#1-visão-geral)
2. [Integrantes e vídeo](#2-integrantes-e-vídeo)
3. [URLs de acesso](#3-urls-de-acesso)
4. [Como autenticar](#4-como-autenticar)
5. [Executar localmente](#5-executar-localmente)
6. [Perfis Spring e banco](#6-perfis-spring-e-banco)
7. [Interface web (Thymeleaf)](#7-interface-web-thymeleaf)
8. [API REST — endpoints](#8-api-rest--endpoints)
9. [Exemplos Postman / curl](#9-exemplos-postman--curl)
10. [Modelo de dados](#10-modelo-de-dados)
11. [HATEOAS](#11-hateoas)
12. [Estrutura do projeto](#12-estrutura-do-projeto)

---

## 1. Visão geral

### O que o sistema faz

- Cadastra **eventos de risco** (nome, descrição, alertas, ações).
- Permite **pedido de socorro por SMS** (validação de telefone; grava em `T_SOS_SMS`).
- Oferece **API REST** para o app mobile/web e **telas web** para admin e cidadão.
- Persiste dados no **Oracle FIAP** (produção) ou **H2** (testes locais).

### Stack


| Camada       | Tecnologia                                            |
| ------------ | ----------------------------------------------------- |
| Linguagem    | Java 21                                               |
| Framework    | Spring Boot 3.5.6                                     |
| Persistência | Spring Data JPA + Flyway                              |
| Banco        | Oracle (FIAP) / H2 (dev e testes)                     |
| Segurança    | Spring Security — form login (web) + HTTP Basic (API) |
| API          | REST + links HATEOAS                                  |
| View         | Thymeleaf + Bootstrap                                 |


---

## 2. Integrantes e vídeo


| Integrante        | RM     | Papel                                    |
| ----------------- | ------ | ---------------------------------------- |
| Bruno Cantacini   | 560242 | Backend, API, Oracle, procedures, vídeos |
| Amanda Galdino    | 560066 | Integração mobile                        |
| Gustavo Gonçalves | 556823 | Documentação                             |


**Vídeo (apresentação Java):** [https://youtu.be/naIMPbVnLuY](https://youtu.be/naIMPbVnLuY)  
**Vídeo (apresentação Devops):** [https://youtu.be/EjIRXvjaivI](https://youtu.be/EjIRXvjaivI)  
**Vídeo (apresentação DataBase): [https://youtu.be/vd5-xOuBDjA](https://youtu.be/vd5-xOuBDjA)**

---

## 3. URLs de acesso

### Ambientes publicados


| Ambiente         | Recurso                     | URL                                                                                                                                                  |
| ---------------- | --------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Staging**      | API Java — login web        | [https://soslocaliza-api-staging-560242.azurewebsites.net/login](https://soslocaliza-api-staging-560242.azurewebsites.net/login)                     |
| **Staging**      | API Java — health           | [https://soslocaliza-api-staging-560242.azurewebsites.net/actuator/health](https://soslocaliza-api-staging-560242.azurewebsites.net/actuator/health) |
| **Produção**     | API Java — login web        | [https://soslocaliza-api-prod-560242.azurewebsites.net/login](https://soslocaliza-api-prod-560242.azurewebsites.net/login)                           |
| **Produção**     | API Java — health           | [https://soslocaliza-api-prod-560242.azurewebsites.net/actuator/health](https://soslocaliza-api-prod-560242.azurewebsites.net/actuator/health)       |
| **Produção**     | API Java — base REST        | [https://soslocaliza-api-prod-560242.azurewebsites.net](https://soslocaliza-api-prod-560242.azurewebsites.net)                                       |
| **Mobile / Web** | App Expo (Firebase Hosting) | [https://sos-localiza.web.app/](https://sos-localiza.web.app/)                                                                                       |


> O app em Firebase consome a API de **produção** (`EXPO_PUBLIC_API_BASE_URL` no build web).

### Local


| Perfil        | Porta | Base URL                                       |
| ------------- | ----- | ---------------------------------------------- |
| `dev` (H2)    | 8082  | [http://localhost:8082](http://localhost:8082) |
| `oracle-fiap` | 8081  | [http://localhost:8081](http://localhost:8081) |


> O app Expo usa porta **8081** no Metro; a API em `dev` usa **8082** para não conflitar. Configure `EXPO_PUBLIC_API_BASE_URL` no mobile conforme a porta da API.

---

## 4. Como autenticar

 Há dois modos:

### A) API REST (`/api/...`) — HTTP Basic Auth

Envie em **toda** requisição protegida:

```http
Authorization: Basic base64(usuario:senha)
```

**Usuários de demonstração** (senha: `password`):


| Username  | Perfil | Uso típico                                  |
| --------- | ------ | ------------------------------------------- |
| `admin`   | ADMIN  | CRUD completo, painel admin                 |
| `citizen` | USER   | App cidadão, enviar SMS, ver eventos ativos |


**Usuário cadastrado pelo app:** username = **CPF** (11 dígitos) + senha escolhida no cadastro.

**“Login” no mobile:** o app valida credenciais com `GET /api/eventos/ativos` + Basic Auth e depois chama `GET /api/mobile/me`.

### B) Interface web — formulário + sessão


| Ação         | Método | URL                                            |
| ------------ | ------ | ---------------------------------------------- |
| Abrir tela   | GET    | `/login`                                       |
| Enviar login | POST   | `/login` (campos `username`, `password`, CSRF) |


Após sucesso, o navegador usa **cookie de sessão** (não Basic).

---

## 5. Executar localmente

### Pré-requisitos

- Java 21+
- Maven (ou use `mvnw` incluso)

### Testes automatizados (recomendado antes de subir)

```powershell
cd API_JAVA\SosLocaliza
$env:SPRING_PROFILES_ACTIVE="test"
.\mvnw.cmd test
```

### Perfil H2 — desenvolvimento rápido (sem Oracle)

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw.cmd spring-boot:run
```

- Health: [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health)  
- H2 Console: [http://localhost:8082/h2-console](http://localhost:8082/h2-console) (JDBC: `jdbc:h2:mem:testdb`, user `sa`, senha `password`)

### Perfil Oracle FIAP

Crie `.env` na pasta do projeto (veja `.env.example`). Mínimo:

```properties
SPRING_PROFILES_ACTIVE=oracle-fiap
ORACLE_USERNAME=rm560242
ORACLE_PASSWORD=sua_senha
```

```powershell
$env:SPRING_PROFILES_ACTIVE="oracle-fiap"
$env:ORACLE_PASSWORD="sua_senha"
.\mvnw.cmd spring-boot:run
```

- Health: [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)

### Gerar JAR

```powershell
.\mvnw.cmd package -DskipTests
java -jar target\SosLocaliza-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Deploy, Docker e pipeline: **[README_DEVOPS.md](README_DEVOPS.md)**.

---

## 6. Perfis Spring e banco


| Perfil        | Banco                         | Porta     | Quando usar           |
| ------------- | ----------------------------- | --------- | --------------------- |
| `test`        | H2 (memória)                  | aleatória | `mvn test`            |
| `dev`         | H2 + Flyway `h2`              | 8082      | Desenvolvimento local |
| `oracle-fiap` | Oracle FIAP + Flyway `oracle` | 8081      | Produção / entrega    |
| `oracle`      | Oracle genérico               | 8081      | Outro servidor Oracle |


**Tabelas principais (2+ com relacionamento — requisito DevOps):**

- `T_SOS_EVENTO` ← 1:N → `T_SOS_SMS` (FK `ID_EVENTO`)
- `T_SOS_APP_USER` (login da API e do app)

Migrations: `src/main/resources/db/migration/oracle/` e `.../h2/`.

**SMS:** envio **simulado**; cada pedido é salvo em `T_SOS_SMS` com status de sucesso/erro.

---

## 7. Interface web (Thymeleaf)

Após login web (`/login`):


| URL (dev :8082 / oracle-fiap :8081) | Quem acessa | Função                    |
| ----------------------------------- | ----------- | ------------------------- |
| `/`                                 | USER, ADMIN | Página inicial            |
| `/socorro`                          | USER, ADMIN | Pedido de socorro (SMS)   |
| `/admin`                            | ADMIN       | Histórico de SMS enviados |


---

## 8. API REST — endpoints

**Prefixo:** `/api`  
**Base produção:** `https://soslocaliza-api-prod-560242.azurewebsites.net`

Legenda de acesso: **Público** | **USER** (citizen ou CPF) | **ADMIN**

### Mobile — `/api/mobile`


| Método | Endpoint                                | Acesso      |
| ------ | --------------------------------------- | ----------- |
| POST   | `/api/mobile/cadastro`                  | Público     |
| GET    | `/api/mobile/areas-risco`               | Público     |
| GET    | `/api/mobile/me`                        | USER, ADMIN |
| GET    | `/api/mobile/reverse-geocode?lat=&lon=` | USER, ADMIN |


### Eventos — `/api/eventos`


| Método | Endpoint                                     | Acesso      |
| ------ | -------------------------------------------- | ----------- |
| GET    | `/api/eventos/ativos`                        | USER, ADMIN |
| GET    | `/api/eventos/getAll?page=&size=&direction=` | USER, ADMIN |
| GET    | `/api/eventos/getById/{id}`                  | USER, ADMIN |
| GET    | `/api/eventos/buscarPorNome?nome=`           | USER, ADMIN |
| GET    | `/api/eventos/buscarPorDescricao?descricao=` | USER, ADMIN |
| GET    | `/api/eventos/estatisticas`                  | USER, ADMIN |
| POST   | `/api/eventos/add`                           | ADMIN       |
| PUT    | `/api/eventos/update/{id}`                   | ADMIN       |
| DELETE | `/api/eventos/delete/{id}`                   | ADMIN       |
| PATCH  | `/api/eventos/desativar/{id}`                | ADMIN       |


### SMS — `/api/sms`


| Método | Endpoint                                          | Acesso      |
| ------ | ------------------------------------------------- | ----------- |
| POST   | `/api/sms`                                        | USER, ADMIN |
| POST   | `/api/sms/emergencia/{idEvento}`                  | USER, ADMIN |
| GET    | `/api/sms/getAll?page=&size=&direction=&sucesso=` | ADMIN       |
| GET    | `/api/sms/getById/{id}`                           | ADMIN       |
| GET    | `/api/sms/buscarPorNumero?numeroTelefone=`        | ADMIN       |
| GET    | `/api/sms/buscarPorDdd?ddd=`                      | ADMIN       |
| GET    | `/api/sms/buscarPorEvento/{idEvento}`             | ADMIN       |
| GET    | `/api/sms/buscarPorPeriodo?dataInicio=&dataFim=`  | ADMIN       |
| GET    | `/api/sms/ultimoSms/{numero}`                     | ADMIN       |
| GET    | `/api/sms/estatisticas`                           | ADMIN       |
| PUT    | `/api/sms/update/{id}`                            | ADMIN       |
| DELETE | `/api/sms/delete/{id}`                            | ADMIN       |
| PATCH  | `/api/sms/marcarSucesso/{id}`                     | ADMIN       |
| PATCH  | `/api/sms/marcarErro/{id}?erro=`                  | ADMIN       |


### Procedures Oracle — `/api/procedures` (ADMIN)


| Método | Endpoint                           |
| ------ | ---------------------------------- |
| POST   | `/api/procedures/localizacao`      |
| PUT    | `/api/procedures/localizacao/{id}` |
| DELETE | `/api/procedures/localizacao/{id}` |
| POST   | `/api/procedures/usuario`          |
| PUT    | `/api/procedures/usuario/{id}`     |
| DELETE | `/api/procedures/usuario/{id}`     |


### Monitoramento


| Método | Endpoint           | Acesso  |
| ------ | ------------------ | ------- |
| GET    | `/actuator/health` | Público |
| GET    | `/actuator/info`   | Público |


Corpos JSON de exemplo: `api-examples/crud/`. **Insomnia:** `api-examples/insomnia/SOS_Localiza.insomnia.json` + guia `ENDPOINTS_INSOMNIA.md`.

---

## 9. Exemplos Postman / curl

### Validar login (Basic Auth)

```bash
curl -u citizen:password ^
  "https://soslocaliza-api-prod-560242.azurewebsites.net/api/eventos/ativos"
```

**Postman:** Authorization → **Basic Auth** → Username `citizen`, Password `password` → GET na URL acima.

### Enviar SMS

```bash
curl -u citizen:password -X POST ^
  -H "Content-Type: application/json" ^
  -d "{\"remetente\":\"Maria\",\"ddd\":\"11\",\"numeroTelefone\":\"999998888\",\"mensagem\":\"Preciso de ajuda\",\"idEvento\":1}" ^
  "https://soslocaliza-api-prod-560242.azurewebsites.net/api/sms"
```

### Cadastro (sem auth)

```bash
curl -X POST -H "Content-Type: application/json" ^
  -d "{\"nomeCompleto\":\"Joao\",\"cpf\":\"12345678909\",\"senha\":\"senha123\",\"telefone\":\"11999998888\",\"cep\":\"01310100\"}" ^
  "https://soslocaliza-api-prod-560242.azurewebsites.net/api/mobile/cadastro"
```

Mais exemplos e collection: **[README_DEVOPS.md](README_DEVOPS.md)**.

---

## 10. Modelo de dados

```
T_SOS_EVENTO (1) ──────< T_SOS_SMS (N)
     ID_EVENTO              ID_EVENTO (FK)

T_SOS_APP_USER — autenticação (USERNAME, PASSWORD BCrypt, ROLE, DDD, telefone)
```


| Tabela           | Campos principais                                             |
| ---------------- | ------------------------------------------------------------- |
| `T_SOS_EVENTO`   | nome, descrição, causas, alertas, ações, ativo                |
| `T_SOS_SMS`      | remetente, telefone (+55), mensagem, sucesso, erro, id_evento |
| `T_SOS_APP_USER` | username, password, role, nome_exibicao, ddd, numero_local    |


---

## 11. HATEOAS

Respostas de **Evento** e **SMS** incluem `_links` (`self`, `collection`, etc.) para navegação REST nível 3. Exemplo:

```json
{
  "idEvento": 1,
  "nomeEvento": "Enchente na Região Sul",
  "_links": {
    "self": { "href": "/api/eventos/getById/1" },
    "collection": { "href": "/api/eventos/getAll?page=0&size=10" }
  }
}
```

Implementação: `HateoasLinkBuilder.java`.

---

## 12. Estrutura do projeto

```
SosLocaliza/
├── src/main/java/com/example/SosLocaliza/
│   ├── configurations/     # Security, CORS
│   ├── domains/            # Entidades JPA
│   ├── gateways/           # Controllers REST
│   ├── services/           # Regras de negócio
│   ├── web/                # Controllers Thymeleaf
│   └── security/
├── src/main/resources/
│   ├── application.yml
│   ├── application-*.yml   # Perfis
│   ├── db/migration/       # Flyway
│   └── templates/          # login, socorro, admin
├── api-examples/
│   ├── crud/               # JSON create/update
│   └── insomnia/           # Collection Insomnia + documentação
├── azure-pipelines.yml     # CI/CD (ver README_DEVOPS)
├── Dockerfile
├── pom.xml
├── README.md               # Este arquivo
└── README_DEVOPS.md        # Pipeline e entrega Sprint DevOps
```

---

**Dúvidas sobre deploy ou reprodução da entrega?** Comece pelo **[README_DEVOPS.md](README_DEVOPS.md)**.