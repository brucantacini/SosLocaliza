# SOS Localiza - Sistema de Emergência Climática

Sistema de emergência para situações climáticas extremas (enchentes, deslizamentos) que permite envio de SMS de socorro via Twilio e gestão de informações sobre eventos de risco.

## 👥 Integrantes do Grupo

- **Bruno Cantacini** - RM560242 - Desenvolvimento Backend, Arquitetura da Aplicação
- **Amanda Galdino** - RM560066 - Integração Twilio
- **Gustavo Gonçalves** - RM556823 - Documentos, vídeo e diagramas

### Cronograma tarefas:
- https://trello.com/invite/b/68d7339fb360c5bde1caf0dc/ATTI0af812df390890d5b80e5048c67d862a00864906/sos-localiza

## 🎥 Vídeo de Apresentação

🔗 https://www.youtube.com/watch?v=rsRSfEShnGk

*O vídeo apresenta a proposta tecnológica, público-alvo da aplicação e os problemas que a aplicação se propõe a solucionar.*

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
1. Crie conta gratuita em: https://www.twilio.com/try-twilio
2. Receba $15-20 de crédito grátis
3. Obtenha Account SID e Auth Token no Dashboard

📖 **Guia completo**: Consulte [GUIA_TWILIO.md](GUIA_TWILIO.md)

### 2. Configuração do Banco de Dados

#### Oracle FIAP (Padrão)

O projeto está configurado para usar o Oracle FIAP por padrão:

- Host: `oracle.fiap.com.br`
- Porta: `1521`
- SID: `ORCL`
- Usuário: `rm560242`
- Senha: `271005`

#### Desenvolvimento (H2)

Para usar H2 em memória durante desenvolvimento, ative o perfil dev:

```bash
java -jar target/SosLocaliza-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Acesse o console H2 em:

```
http://localhost:8080/api/h2-console
```

## 🏃‍♂️ Executando o Projeto

### Desenvolvimento

```bash
mvn spring-boot:run
```

### Produção

```bash
mvn clean package
java -jar target/SosLocaliza-0.0.1-SNAPSHOT.jar
```

## 🐳 Executando com Docker

### Pré-requisitos
- Docker instalado
- Docker Compose instalado

### Build e Execução

#### 1. Build do JAR
Primeiro, é necessário compilar o projeto e gerar o JAR:

```bash
mvn clean package
```

#### 2. Build da Imagem Docker
```bash
docker build -t soslocaliza:latest .
```

#### 3. Executar com Docker Compose (Recomendado)
```bash
# Executar em background
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar a aplicação
docker-compose down
```

#### 4. Executar com Docker (Alternativo)
```bash
# Executar em background
docker run -d \
  --name soslocaliza-app \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=oracle-fiap \
  soslocaliza:latest

# Ver logs
docker logs -f soslocaliza-app

# Parar a aplicação
docker stop soslocaliza-app
docker rm soslocaliza-app
```

### Configurações Docker

O `docker-compose.yml` está configurado com:
- ✅ **Imagem Alpine Linux** (leve e performática)
- ✅ **Usuário não-root** (segurança)
- ✅ **Diretório padrão** (`/app`)
- ✅ **Execução em background** (via `-d`)

### Variáveis de Ambiente

Você pode sobrescrever variáveis de ambiente no `docker-compose.yml` ou via arquivo `.env`:

```bash
# Criar arquivo .env (opcional)
SPRING_PROFILES_ACTIVE=oracle-fiap
TWILIO_ENABLED=false
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
SPRING_DATASOURCE_USERNAME=seu_usuario
SPRING_DATASOURCE_PASSWORD=sua_senha
```

### Verificar Status

```bash
# Verificar containers em execução
docker ps

# Verificar logs da aplicação
docker-compose logs soslocaliza

# Verificar saúde da aplicação
curl http://localhost:8081/api/actuator/health
```

### Troubleshooting Docker

#### Erro: "Cannot connect to the Docker daemon"
```bash
# Iniciar Docker service (Linux)
sudo systemctl start docker

# Verificar se Docker está rodando
docker info
```

#### Erro: "Port already in use"
```bash
# Verificar qual processo está usando a porta 8081
# Linux/Mac
lsof -i :8081

# Windows
netstat -ano | findstr :8081

# Parar container existente
docker stop soslocaliza-app
docker rm soslocaliza-app
```

#### Rebuild após mudanças no código
```bash
# Recompilar o JAR
mvn clean package

# Rebuild da imagem Docker
docker-compose build --no-cache

# Reiniciar containers
docker-compose up -d
```

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
      "href": "http://localhost:8080/api/eventos/getById/1"
    },
    "collection": {
      "href": "http://localhost:8080/api/eventos/getAll"
    },
    "update": {
      "href": "http://localhost:8080/api/eventos/update/1"
    },
    "delete": {
      "href": "http://localhost:8080/api/eventos/delete/1"
    },
    "desativar": {
      "href": "http://localhost:8080/api/eventos/desativar/1"
    },
    "sms": {
      "href": "http://localhost:8080/api/sms/buscarPorEvento/1"
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

### Benefícios

- ✅ **Descoberta automática**: Cliente descobre endpoints disponíveis
- ✅ **Menos hardcoding**: Não precisa decorar URLs
- ✅ **Evolução fácil**: Mudanças de URL são menos impactantes
- ✅ **Navegação natural**: Segue links como em um site web

## 📡 Endpoints da API

### Eventos

- `POST /api/eventos/add` - Criar evento
- `GET /api/eventos/getAll` - Listar eventos (com paginação)
- `GET /api/eventos/getById/{id}` - Buscar evento por ID
- `PUT /api/eventos/update/{id}` - Atualizar evento
- `DELETE /api/eventos/delete/{id}` - Deletar evento
- `PATCH /api/eventos/desativar/{id}` - Desativar evento
- `GET /api/eventos/buscarPorNome?nome=X` - Buscar por nome
- `GET /api/eventos/buscarPorDescricao?descricao=X` - Buscar por descrição
- `GET /api/eventos/estatisticas` - Estatísticas

### SMS

- `POST /api/sms` - Enviar SMS
- `POST /api/sms/emergencia/{idEvento}` - Enviar SMS de emergência
- `GET /api/sms/getAll` - Listar SMS (com paginação)
- `GET /api/sms/buscarPorNumero?numeroTelefone=X` - Buscar por número
- `GET /api/sms/buscarPorDdd?ddd=XX` - Buscar por DDD
- `GET /api/sms/buscarPorEvento/{idEvento}` - Buscar por evento
- `GET /api/sms/buscarPorPeriodo?dataInicio=X&dataFim=Y` - Buscar por período
- `GET /api/sms/ultimoSms/{numero}` - Último SMS por número
- `GET /api/sms/estatisticas` - Estatísticas
- `PATCH /api/sms/marcarSucesso/{id}` - Marcar como sucesso
- `PATCH /api/sms/marcarErro/{id}?erro=X` - Marcar como erro

## 🧪 Testando com Postman/Insomnia

### Exemplo de Criação de Evento

```json
POST /api/eventos/add
Content-Type: application/json

{
  "nomeEvento": "Enchente na Região Sul",
  "descricaoEvento": "Alagamento severo em várias ruas",
  "causas": "Chuva intensa e sistema de drenagem inadequado",
  "alertas": "Nível do rio subindo rapidamente",
  "acoesAntes": "Evacuar áreas de risco",
  "acoesDurante": "Não atravessar ruas alagadas",
  "acoesDepois": "Verificar danos estruturais"
}
```

### Exemplo de Envio de SMS

```json
POST /api/sms
Content-Type: application/json

{
  "remetente": "Defesa Civil",
  "ddd": "11",
  "numero": "999999999",
  "mensagem": "ALERTA: Enchente na região. Evacue imediatamente!"
}
```

### Exemplo de SMS de Emergência

```json
POST /api/sms/emergencia/{idEvento}
Content-Type: application/json

{
  "remetente": "Sistema SOS Localiza",
  "ddd": "11",
  "numero": "999999999",
  "mensagem": "Situação crítica detectada!"
}
```

## 📊 Diagramas da Aplicação

### Diagrama de Classes

![Diagrama de Classes](Docs/DiagramaClasseJAava.png)

### Diagrama de Entidade e Relacionamento (DER)

![Diagrama DER](Docs/DER_Java.png)

## 📊 Estrutura do Banco de Dados

### T_SOS_EVENTO

- ID_EVENTO (UUID)
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

- ID_SMS (UUID)
- REMETENTE (VARCHAR 100)
- NUMERO_TELEFONE (VARCHAR 20)
- DDD (VARCHAR 3)
- NUMERO (VARCHAR 10)
- MENSAGEM (VARCHAR 1000)
- DATA_ENVIO (TIMESTAMP)
- ENVIADO_COM_SUCESSO (BOOLEAN)
- ERRO (VARCHAR 500)
- ID_EVENTO (UUID, FK)

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

## ☁️ Deploy na Nuvem

### Pré-requisitos
- Conta em provedor de nuvem (Azure, AWS, GCP)
- VM Linux criada (Ubuntu 22.04 LTS recomendado)
- Docker instalado na VM

### Passos para Deploy

#### 1. Conectar à VM
```bash
# SSH para a VM
ssh usuario@ip-da-vm
```

#### 2. Instalar Docker na VM
```bash
# Atualizar pacotes
sudo apt update

# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Adicionar usuário ao grupo docker (para não usar sudo)
sudo usermod -aG docker $USER

# Reiniciar sessão SSH ou executar:
newgrp docker

# Verificar instalação
docker --version
docker-compose --version
```

#### 3. Clonar Repositório na VM
```bash
# Instalar Git (se necessário)
sudo apt install git -y

# Clonar repositório
git clone https://github.com/seu-usuario/SOS-Localiza.git
cd SOS-Localiza/SosLocaliza
```

#### 4. Build e Deploy
```bash
# Build do JAR (ou fazer upload do JAR já compilado)
mvn clean package -DskipTests

# Build da imagem Docker
docker build -t soslocaliza:latest .

# Executar com Docker Compose em background
docker-compose up -d

# Verificar logs
docker-compose logs -f

# Verificar status
docker ps
```

#### 5. Configurar Firewall
```bash
# Permitir porta 8081 (ajustar conforme seu provedor de nuvem)
# Azure: Configurar Network Security Group (NSG)
# AWS: Configurar Security Group
# GCP: Configurar Firewall Rules
```

#### 6. Monitoramento
- Configure monitoramento de saúde da VM
- Configure monitoramento de desempenho
- Configure alertas de rede
- Documente com prints/evidências

### Persistência de Dados

Os dados são persistidos no Oracle Database FIAP, que está acessível via internet. Não é necessário configurar volumes Docker para persistência local, pois o banco está na nuvem.

### Evidências de Deleção

**⚠️ IMPORTANTE**: Capture evidências (prints/vídeo) da deleção do grupo de recursos da VM após os testes, conforme requisito da Sprint 2.

## 🧪 Testes

Consulte o arquivo [TESTES.md](TESTES.md) para instruções detalhadas sobre como testar a API.

### Testes Rápidos

#### Testar Health Check
```bash
curl http://localhost:8081/api/actuator/health
```

#### Testar Endpoint de Eventos
```bash
curl -X POST http://localhost:8081/api/eventos/add \
  -H "Content-Type: application/json" \
  -d '{
    "nomeEvento": "Teste Docker",
    "descricaoEvento": "Testando deploy Docker"
  }'
```

#### Testar Endpoint de Procedures
```bash
curl -X POST http://localhost:8081/api/procedures/localizacao \
  -H "Content-Type: application/json" \
  -d '{
    "nomeLocal": "Localização Teste",
    "ruaLocal": "Rua Teste",
    "numeroLocal": 123,
    "cepLocal": "01234-567"
  }'
```
