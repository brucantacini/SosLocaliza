# SOS Localiza - Sistema de Emergência Climática

Sistema de emergência para situações climáticas extremas (enchentes, deslizamentos) que permite envio de SMS de socorro via Twilio e gestão de informações sobre eventos de risco.

Este **README** concentra **descrição do produto**, **stack Java/Spring**, **configuração** (Twilio, Oracle, H2) e **como executar** a aplicação em desenvolvimento. Para **deploy na nuvem**, **CI/CD**, **Docker/VM** e **testes da API com JSON de CRUD**, use **[README_DEVOPS.md](README_DEVOPS.md)**.

## 👥 Integrantes do Grupo

### Bruno Cantacini - RM560242

**Responsabilidades**: Desenvolvimento Backend, Arquitetura da Aplicação, Implementação de Endpoints REST, Integração com Banco de Dados Oracle, Procedures e HATEOAS,  Criação de vídeos demonstrativos.

### Amanda Galdino - RM560066

**Responsabilidades**: Integração Twilio para envio de SMS, Configuração de APIs externas, Testes de integração.

### Gustavo Gonçalves - RM556823

**Responsabilidades**: Documentação do projeto técnica.

## 🎥 Vídeo de Apresentação

🔗 **Link do Vídeo**:  [https://youtu.be/9Shjtsz0vjM](https://youtu.be/9Shjtsz0vjM)

### Conteúdo do Vídeo

O vídeo apresenta:

- **Proposta Tecnológica**: Sistema de emergência climática com envio de SMS via Twilio
- **Público-Alvo**: População em áreas de risco, Defesa Civil, Órgãos públicos de gestão de emergências
- **Problemas que a aplicação soluciona**:
  - Comunicação rápida e eficiente em situações de emergência climática
  - Gestão centralizada de eventos de risco (enchentes, deslizamentos)
  - Alertas automáticos via SMS para população em áreas afetadas
  - Histórico e estatísticas de eventos e comunicações
  - Integração com banco de dados Oracle para persistência confiável

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **Spring HATEOAS** (REST Nível 3)
- **H2 Database** (desenvolvimento/testes)
- **Oracle Database** (produção)
- **Twilio SMS API**
- **Lombok**
- **Bean Validation**
- **Maven**
- **Flyway** (versionamento do schema do banco)
- **Spring Security** (form login, HTTP Basic para API, perfis `ROLE_USER` e `ROLE_ADMIN`)
- **Thymeleaf** (camada de visualização web)

## Documentação DevOps (deploy, nuvem e testes da entrega)

Instruções para **reproduzir deploy no Azure**, **pipeline CI/CD**, **scripts JSON de CRUD**, **Postman/curl** e **Docker/VM** estão no arquivo **[README_DEVOPS.md](README_DEVOPS.md)** — use-o como referência principal para a correção da Sprint DevOps.

## Interface web

Após subir a aplicação, acesse no navegador:


| URL                                                                                   | Descrição                                                                                             |
| ------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------- |
| `http://localhost:8082/login` (dev) / `http://localhost:8081/login` (oracle-fiap)     | Login (redireciona usuários não autenticados)                                                         |
| `http://localhost:8082/` (dev) / `http://localhost:8081/` (oracle-fiap)               | Página inicial (após login)                                                                           |
| `http://localhost:8082/socorro` (dev) / `http://localhost:8081/socorro` (oracle-fiap) | Fluxo **Pedido de socorro (SMS)** — escolha do evento e mensagem. Nome/telefone vêm do usuário logado |
| `http://localhost:8082/admin` (dev) / `http://localhost:8081/admin` (oracle-fiap)     | **Painel de mensagens enviadas** — histórico paginado de SMS (somente `ROLE_ADMIN`)     |


**Usuários de demonstração** (senha em ambos: `password`):


| Usuário   | Perfil                                                                 |
| --------- | ---------------------------------------------------------------------- |
| `admin`   | Administrador (`ROLE_ADMIN`) — acesso ao painel e à API completa       |
| `citizen` | Cidadão (`ROLE_USER`) — pedido de socorro e leitura de eventos via API |


Os hashes BCrypt estão nas migrations `V2__seed_users.sql` (pastas `db/migration/h2` e `db/migration/oracle`).

**REST API:** todos os controllers estão sob o prefixo `/api` (ex.: `GET /api/eventos/ativos`). Para testar com **curl** ou Postman, use **HTTP Basic** com `admin`/`password` ou `citizen`/`password`. O CSRF está desligado apenas para rotas `/api/`**; o login web usa CSRF normalmente.

**Variáveis de ambiente (Oracle FIAP):** defina `ORACLE_PASSWORD` (e opcionalmente `ORACLE_USERNAME`). **Não** commite senhas no repositório. A pasta do projeto contém `.env.example`.

**Produção Oracle (alinhado com o app mobile):**

| Item | Valor |
|------|--------|
| Perfil recomendado | `oracle-fiap` (FIAP) ou `prod` (equivale a `oracle-fiap` via grupo de perfis) |
| Alternativa genérica Oracle | `oracle` — mesmo Flyway em `db/migration/oracle`, `ddl-auto: validate` |
| Flyway | `classpath:db/migration/oracle` — cria/atualiza `T_SOS_EVENTO`, `T_SOS_SMS`, `T_SOS_APP_USER` + colunas de perfil (V6/V7) |
| JPA | `validate` — o schema vem das migrations; não use `update`/`create-drop` em produção |
| Porta padrão | **8081** (`oracle-fiap`) vs **8082** (`dev`, alinhado ao Metro Expo do app em **8081**) — no Expo use `EXPO_PUBLIC_API_BASE_URL` com a porta do Spring |
| Variáveis | `ORACLE_HOST`, `ORACLE_PORT`, `ORACLE_SID`, `ORACLE_USERNAME`, `ORACLE_PASSWORD` (obrigatória em FIAP se o default vazio não servir) |
| Dados iniciais | Migrations `V2`/`V5` inserem `admin` e `citizen`; `DataInitializer` roda em `dev` e `oracle-fiap` e cria eventos de exemplo se a tabela estiver vazia |

Exemplo: `mvnw spring-boot:run -Dspring-boot.run.profiles=oracle-fiap` ou `SPRING_PROFILES_ACTIVE=prod`.

## 📋 Pré-requisitos

- Java 21 ou superior
- Maven 3.6+
- Conta Twilio (para envio de SMS)
- Oracle Database (opcional, para produção)

## ⚙️ Configuração

### 1. Configuração do Twilio (SMS Real)

O sistema suporta **dois modos**:

- **Modo Simulação**: Funciona sem configuração (padrão)
- **Modo Twilio Real**: Envia SMS reais via Twilio

#### Modo Simulação (Padrão)

Sem configurar nada, o sistema usa modo simulação para testes.

#### Modo Twilio Real (Opcional)

Para enviar SMS reais, configure as variáveis de ambiente:

```bash
# Windows PowerShell
$env:TWILIO_ACCOUNT_SID="ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
$env:TWILIO_AUTH_TOKEN="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
$env:TWILIO_TRIAL_NUMBER="+15005550006"
$env:TWILIO_ENABLED="true"

# Linux/Mac
export TWILIO_ACCOUNT_SID="ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
export TWILIO_AUTH_TOKEN="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
export TWILIO_TRIAL_NUMBER="+15005550006"
export TWILIO_ENABLED="true"
```

**Como obter credenciais Twilio**:

1. Crie conta gratuita em: [https://www.twilio.com/try-twilio](https://www.twilio.com/try-twilio)
2. Receba $15-20 de crédito grátis
3. Obtenha Account SID e Auth Token no Dashboard

📖 Guia rápido: use as variáveis de ambiente acima para ativar o envio real do Twilio.

### 2. Configuração do Banco de Dados

#### Oracle FIAP

Perfil Spring: `oracle-fiap` (porta **8081**).

- Host: `oracle.fiap.com.br`
- Porta: `1521`
- SID: `ORCL`
- Usuário: variável `ORACLE_USERNAME` (padrão `rm560242` se não informada)
- Senha: variável de ambiente `**ORACLE_PASSWORD`** (obrigatória; não versionar)

**Script SQL da Sprint 3 (DBA):** se você rodar no Oracle o arquivo da disciplina com `T_SOS_EVENTO`, `T_SOS_SMS` e FK entre eles, o modelo JPA deste projeto já está alinhado a essas tabelas. Use **ou** as migrations Flyway em `db/migration/oracle` **ou** o script DBA no mesmo schema; se as tabelas já existirem e o Flyway tentar criá-las de novo, a inicialização pode falhar — nesse caso alinhe com o professor (baseline Flyway ou schema só via script).

Exemplo de `.env` local (na pasta `SosLocaliza`):

```properties
spring.profiles.active=oracle-fiap
SERVER_PORT=8081
ORACLE_USERNAME=rm560242
ORACLE_PASSWORD=*****
ORACLE_HOST=oracle.fiap.com.br
ORACLE_PORT=1521
ORACLE_SID=ORCL
```

#### Desenvolvimento (H2)

O perfil padrão é `**dev**` (H2 em memória, Flyway em `classpath:db/migration/h2`, porta **8082**).

```bash
java -jar target/SosLocaliza-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Console H2 (perfil `dev`):

```
http://localhost:8082/h2-console
```

## 🏃‍♂️ Como Rodar a Aplicação

### Opção 1: Execução Local (Desenvolvimento)

#### Pré-requisitos

- Java 21 instalado
- Maven 3.6+ instalado
- Acesso ao Oracle FIAP (ou use perfil H2 para desenvolvimento)

#### Passo a Passo

1. **Clone o repositório:**

```bash
git clone <URL_DO_REPOSITORIO>
cd SosLocaliza
```

1. **Compile o projeto:**

```bash
mvn clean install
```

1. **Execute a aplicação:**

```bash
./mvnw spring-boot:run
```

1. **Acesse a aplicação:**

```
http://localhost:8082/actuator/health (perfil dev)
```

Para Oracle FIAP, rode com perfil Oracle ativo:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=oracle-fiap
```

e acesse:

```
http://localhost:8081/actuator/health
```

### Opção 2: Execução com JAR (Produção)

1. **Gere o JAR:**

```bash
mvn clean package
```

1. **Execute o JAR:**

```bash
java -jar target/SosLocaliza-0.0.1-SNAPSHOT.jar
```

### Opção 3: Docker / nuvem / pipeline

Para **Docker Compose**, **VM**, **Azure App Service** e **Azure Pipelines**, consulte **[README_DEVOPS.md](README_DEVOPS.md)**.

### Opção 4: Execução com H2 (Desenvolvimento sem Oracle)

Para desenvolvimento sem acesso ao Oracle, use o perfil H2:

```bash
java -jar target/SosLocaliza-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Acesse o console H2 em: `http://localhost:8082/h2-console` (perfil `dev`; no `oracle-fiap` o H2 costuma não estar habilitado).

Exemplos de **curl**, **Postman**, **Docker**, **deploy em VM/Azure** e **scripts JSON de CRUD** para reprodução completa: **[README_DEVOPS.md](README_DEVOPS.md)**.

## 🔗 HATEOAS (Hypermedia as the Engine of Application State)

A API implementa **HATEOAS nível 3** do modelo de maturidade REST proposto por Leonard Richardson. Isso significa que todas as respostas incluem links navegáveis que permitem descobrir e acessar recursos relacionados sem precisar conhecer as URLs de antemão.

### O que é HATEOAS?

HATEOAS é um padrão REST que adiciona links nas respostas da API, permitindo que o cliente navegue pela API seguindo links, similar a navegar por um site web. Isso torna a API mais descoberta e evolutiva.

### Nível de Maturidade REST

- ✅ **Nível 1**: Recursos (URLs diferentes para cada recurso)
- ✅ **Nível 2**: Verbos HTTP (GET, POST, PUT, DELETE)
- ✅ **Nível 3**: HATEOAS (Links explícitos nas respostas) ← **Implementado nesta Sprint**

### Estrutura de Resposta com HATEOAS

Todas as respostas de recursos individuais (Evento e SMS) incluem um objeto `_links` com os links disponíveis:

```json
{
  "idEvento": 1,
  "nomeEvento": "Enchente na Região Sul",
  "descricaoEvento": "Alagamento severo em várias ruas",
  "ativo": true,
  "_links": {
    "self": {
      "href": "http://localhost:8082/api/eventos/getById/1"
    },
    "collection": {
      "href": "http://localhost:8082/api/eventos/getAll"
    },
    "update": {
      "href": "http://localhost:8082/api/eventos/update/1"
    },
    "delete": {
      "href": "http://localhost:8082/api/eventos/delete/1"
    },
    "desativar": {
      "href": "http://localhost:8082/api/eventos/desativar/1"
    },
    "sms": {
      "href": "http://localhost:8082/api/sms/buscarPorEvento/1"
    }
  }
}
```

### Links Disponíveis por Recurso

#### Eventos (`EventoResponseDto`)

- `self` - Link para o próprio evento
- `collection` - Link para listar todos os eventos
- `update` - Link para atualizar o evento (PUT)
- `delete` - Link para deletar o evento (DELETE)
- `desativar` - Link para desativar o evento (PATCH)
- `sms` - Link para ver SMSs relacionados ao evento

#### SMS (`SmsResponseDto`)

- `self` - Link para o próprio SMS
- `collection` - Link para listar todos os SMS
- `evento` - Link para o evento relacionado (se houver)

### Exemplo de Uso

1. **Criar um evento**:
  ```bash
   POST /api/eventos/add
  ```
   A resposta incluirá links para navegar pelo evento criado.
2. **Seguir o link `collection`**:
  O cliente pode usar o link `collection` retornado para listar todos os eventos.
3. **Seguir o link `sms`**:
  O cliente pode usar o link `sms` para ver todos os SMSs relacionados ao evento.

## 📡 Documentação Completa da API - Endpoints

### Base URL

Base HTTP: `http://localhost:8081` — rotas REST usam o prefixo `/api/...`. Autenticação: HTTP Basic (`admin`/`citizen`, senha `password`).

### Eventos


| Método   | Endpoint                                                | Descrição                                                 |
| -------- | ------------------------------------------------------- | --------------------------------------------------------- |
| `POST`   | `/api/eventos/add`                                      | Criar novo evento                                         |
| `GET`    | `/api/eventos/getAll`                                   | Listar todos os eventos (com paginação)                   |
| `GET`    | `/api/eventos/ativos`                                   | Listar apenas eventos ativos (ex.: formulário de socorro) |
| `GET`    | `/api/eventos/getById/{id}`                             | Buscar evento por ID                                      |
| `PUT`    | `/api/eventos/update/{id}`                              | Atualizar evento existente                                |
| `DELETE` | `/api/eventos/delete/{id}`                              | Deletar evento                                            |
| `PATCH`  | `/api/eventos/desativar/{id}`                           | Desativar evento (soft delete)                            |
| `GET`    | `/api/eventos/buscarPorNome?nome={nome}`                | Buscar eventos por nome                                   |
| `GET`    | `/api/eventos/buscarPorDescricao?descricao={descricao}` | Buscar eventos por descrição                              |
| `GET`    | `/api/eventos/estatisticas`                             | Obter estatísticas dos eventos                            |


**Parâmetros de Paginação (para getAll):**

- `page`: Número da página (padrão: 0)
- `size`: Tamanho da página (padrão: 10)
- `direction`: Direção da ordenação - ASC ou DESC (padrão: ASC)

### SMS


| Método  | Endpoint                                                              | Descrição                                      |
| ------- | --------------------------------------------------------------------- | ---------------------------------------------- |
| `POST`  | `/api/sms`                                                            | Enviar SMS                                     |
| `POST`  | `/api/sms/emergencia/{idEvento}`                                      | Enviar SMS de emergência vinculado a um evento |
| `GET`   | `/api/sms/getById/{id}`                                               | Buscar SMS por ID                              |
| `PUT`   | `/api/sms/update/{id}`                                                | Atualizar SMS (inclui vínculo com evento)      |
| `DELETE`| `/api/sms/delete/{id}`                                                | Excluir SMS                                    |
| `GET`   | `/api/sms/getAll`                                                     | Listar todos os SMS (com paginação)            |
| `GET`   | `/api/sms/buscarPorNumero?numeroTelefone={numero}`                    | Buscar SMS por número de telefone              |
| `GET`   | `/api/sms/buscarPorDdd?ddd={ddd}`                                     | Buscar SMS por DDD                             |
| `GET`   | `/api/sms/buscarPorEvento/{idEvento}`                                 | Buscar SMS por evento                          |
| `GET`   | `/api/sms/buscarPorPeriodo?dataInicio={dataInicio}&dataFim={dataFim}` | Buscar SMS por período                         |
| `GET`   | `/api/sms/ultimoSms/{numero}`                                         | Buscar último SMS enviado para um número       |
| `GET`   | `/api/sms/estatisticas`                                               | Obter estatísticas dos SMS                     |
| `PATCH` | `/api/sms/marcarSucesso/{id}`                                         | Marcar SMS como enviado com sucesso            |
| `PATCH` | `/api/sms/marcarErro/{id}?erro={mensagemErro}`                        | Marcar SMS como erro                           |


**Parâmetros de Paginação (para getAll):**

- `page`: Número da página (padrão: 0)
- `size`: Tamanho da página (padrão: 10)
- `direction`: Direção da ordenação - ASC ou DESC (padrão: DESC)
- `sucesso`: true/false (opcional, filtra por status de envio)

### Procedures Oracle (Sprint 2)


| Método   | Endpoint                           | Descrição                                  |
| -------- | ---------------------------------- | ------------------------------------------ |
| `POST`   | `/api/procedures/localizacao`      | Criar localização via procedure Oracle     |
| `PUT`    | `/api/procedures/localizacao/{id}` | Atualizar localização via procedure Oracle |
| `DELETE` | `/api/procedures/localizacao/{id}` | Deletar localização via procedure Oracle   |
| `POST`   | `/api/procedures/usuario`          | Criar usuário via procedure Oracle         |
| `PUT`    | `/api/procedures/usuario/{id}`     | Atualizar usuário via procedure Oracle     |
| `DELETE` | `/api/procedures/usuario/{id}`     | Deletar usuário via procedure Oracle       |


### Actuator (Health Check)


| Método | Endpoint           | Descrição                    |
| ------ | ------------------ | ---------------------------- |
| `GET`  | `/actuator/health` | Verificar saúde da aplicação |
| `GET`  | `/actuator/info`   | Informações da aplicação     |


---

**Total de Endpoints**: 25 endpoints disponíveis

Exemplos de corpo JSON, Postman e pasta `api-examples/crud/`: **[README_DEVOPS.md](README_DEVOPS.md)**.

#### Relacionamentos e Constraints

**T_SOS_EVENTO ↔ T_SOS_SMS**

- **Tipo**: Um-para-Muitos (1:N)
- **Relacionamento**: Um evento pode ter múltiplos SMS associados
- **Constraint**: `T_SOS_SMS.ID_EVENTO` é Foreign Key para `T_SOS_EVENTO.ID_EVENTO`
- **Comportamento**: cada SMS deve estar vinculado a um evento (`ID_EVENTO` obrigatório no Oracle em produção).

**T_SOS_LOCALIZACAO ↔ T_SOS_USUARIO**

- **Tipo**: Um-para-Muitos (1:N)
- **Relacionamento**: Uma localização pode ter múltiplos usuários
- **Constraint**: `T_SOS_USUARIO.ID_LOCAL` é Foreign Key para `T_SOS_LOCALIZACAO.ID_LOCAL`

#### Constraints Principais

- **Primary Keys**: As tabelas usam chaves numéricas (`NUMBER(19)` no Oracle e `BIGINT` no H2).
- **Not Null**: Campos obrigatórios incluem NOME_EVENTO, REMETENTE, NUMERO_TELEFONE, MENSAGEM
- **Unique**: CPF do usuário deve ser único
- **Check**: Validações de formato (CEP, telefone, email)
- **Timestamps**: DATA_CRIACAO e DATA_ATUALIZACAO são gerenciados automaticamente

## 📊 Estrutura do Banco de Dados

### T_SOS_EVENTO

- ID_EVENTO (NUMBER/BIGINT)
- NOME_EVENTO (VARCHAR 100)
- DESCRICAO_EVENTO (VARCHAR 500)
- CAUSAS (VARCHAR 300)
- ALERTAS (VARCHAR 300)
- ACOES_ANTES (VARCHAR 500)
- ACOES_DURANTE (VARCHAR 500)
- ACOES_DEPOIS (VARCHAR 500)
- DATA_CRIACAO (TIMESTAMP)
- DATA_ATUALIZACAO (TIMESTAMP)
- ATIVO (BOOLEAN)

### T_SOS_SMS

- ID_SMS (NUMBER/BIGINT)
- REMETENTE (VARCHAR 100)
- NUMERO_TELEFONE (VARCHAR 20)
- DDD (VARCHAR 3)
- NUMERO (VARCHAR 10)
- MENSAGEM (VARCHAR 1000)
- DATA_ENVIO (TIMESTAMP)
- ENVIADO_COM_SUCESSO (BOOLEAN)
- ERRO (VARCHAR 500)
- ID_EVENTO (NUMBER/BIGINT, FK)

### T_SOS_APP_USER

- ID_USER (NUMBER/BIGINT)
- USERNAME (VARCHAR 100)
- PASSWORD (VARCHAR 100)
- ENABLED (NUMBER(1))
- ROLE (VARCHAR 50)
- NOME_EXIBICAO (VARCHAR 100)
- DDD (VARCHAR 2)
- NUMERO_LOCAL (VARCHAR 9)
- LOCALIZACAO (VARCHAR 200)

## 🔧 Configurações Avançadas

### Logs

O projeto está configurado para logs detalhados em desenvolvimento:

- SQL queries
- Parâmetros de binding
- Requests HTTP
- Logs do Twilio

### Paginação

Todos os endpoints de listagem suportam paginação:

- `page`: Número da página (padrão: 0)
- `size`: Tamanho da página (padrão: 10)
- `direction`: Direção da ordenação (ASC/DESC)

### Validações

- Números de telefone brasileiros (DDD + 8-9 dígitos)
- Campos obrigatórios
- Tamanhos máximos de campos
- Formato de datas

## 📚 Documentação adicional

- **[README_DEVOPS.md](README_DEVOPS.md)**: deploy (Azure, Docker, VM), pipeline CI/CD, testes com curl/Postman e **scripts JSON de CRUD** (entrega Sprint DevOps).
- **[TESTES.md](TESTES.md)**: cenários de teste da API e procedures.

