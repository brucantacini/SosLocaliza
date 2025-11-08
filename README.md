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

## 🧪 Testes

Consulte o arquivo [TESTES.md](TESTES.md) para instruções detalhadas sobre como testar a API.
