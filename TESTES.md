# 🧪 Testes da API SOS Localiza

## 📋 Base URL
```
http://localhost:8081/api
```

## 🚀 Como Executar os Testes

### **1. Configuração Inicial**
```bash
# Execute a aplicação
mvn spring-boot:run
```

### **2. Configuração do Ambiente**
- **Porta**: 8081
- **Context Path**: `/api`
- **Banco**: Oracle FIAP
- **SMS**: Twilio Real (configurado) ou Simulado

---

## 🔗 HATEOAS (Hypermedia as the Engine of Application State)

A API implementa **HATEOAS nível 3**, o que significa que todas as respostas incluem links navegáveis (`_links`) que permitem descobrir e acessar recursos relacionados sem precisar conhecer as URLs de antemão.

### **Estrutura de Links HATEOAS**

Cada resposta de recurso inclui um objeto `_links` com:
- **`self`**: Link para o próprio recurso
- **`collection`**: Link para listar todos os recursos

**Exemplo de resposta com HATEOAS:**
```json
{
  "idSms": 1,
  "remetente": "Sistema SOS Localiza",
  "numeroTelefone": "+5511989302572",
  "mensagem": "Teste!",
  "_links": {
    "self": {
      "href": "/api/sms/ultimoSms/%2B5511989302572"
    },
    "collection": {
      "href": "/api/sms/getAll?page=0&size=10&direction=DESC"
    }
  }
}
```

### **Navegação HATEOAS**
1. **Criar** um recurso → recebe links na resposta
2. **Copiar** o link `collection` → colar no navegador/Postman
3. **Listar** recursos → cada item tem seus próprios links
4. **Seguir** link `self` → ver detalhes do recurso

---

### **EVENTOS**

#### **1. Criar Evento**
**endpoint** = `POST /api/eventos/add`
**json para teste** = 
```json
{
  "nomeEvento": "Deslizamento de Terra",
  "descricaoEvento": "Deslizamento causado por chuvas intensas em áreas de risco",
  "causas": "Chuvas intensas, solo saturado, desmatamento",
  "alertas": "Monitoramento de encostas, alertas meteorológicos",
  "acoesAntes": "Evacuar áreas de risco, monitorar encostas",
  "acoesDurante": "Evitar áreas de risco, seguir orientações da defesa civil",
  "acoesDepois": "Avaliar danos, reconstruir com técnicas adequadas"
}
```

#### **2. Listar Todos Eventos**
**endpoint** = `GET /api/eventos/getAll`
**json para teste** = 
```json
{
  "page": 0,
  "size": 10,
  "direction": "ASC",
  "nome": "",
  "apenasAtivos": true
}
```

#### **3. Buscar Evento por ID**
**endpoint** = `GET /api/eventos/getById/1`
**json para teste** = 
```json
{}
```

#### **4. Atualizar Evento**
**endpoint** = `PUT /api/eventos/update/1`
**json para teste** = 
```json
{
  "nomeEvento": "Enchente Atualizada",
  "descricaoEvento": "Descrição atualizada do evento",
  "causas": "Chuvas intensas atualizadas",
  "alertas": "Alertas atualizados",
  "acoesAntes": "Ações antes atualizadas",
  "acoesDurante": "Ações durante atualizadas",
  "acoesDepois": "Ações depois atualizadas"
}
```

#### **5. Deletar Evento**
**endpoint** = `DELETE /api/eventos/delete/1`
**json para teste** = 
```json
{}
```

#### **6. Desativar Evento**
**endpoint** = `PATCH /api/eventos/desativar/1`
**json para teste** = 
```json
{}
```

### **SMS**

#### **7. Enviar SMS (com HATEOAS)**
**endpoint** = `POST /api/sms`
**json para teste** = 
```json
{
  "remetente": "SOS Localiza",
  "ddd": "11",
  "numeroTelefone": "+5511989302572",
  "mensagem": "Teste de SMS com HATEOAS!"
}
```

**Resposta esperada (com links HATEOAS):**
```json
{
  "idSms": 1,
  "remetente": "SOS Localiza",
  "numeroTelefone": "+5511989302572",
  "ddd": "11",
  "mensagem": "Teste de SMS com HATEOAS!",
  "dataEnvio": "2025-11-04T21:00:00",
  "enviadoComSucesso": true,
  "erro": null,
  "idEvento": null,
  "_links": {
    "self": {
      "href": "/api/sms/ultimoSms/%2B5511989302572"
    },
    "collection": {
      "href": "/api/sms/getAll?page=0&size=10&direction=DESC"
    }
  }
}
```

**✨ Navegação:** Copie o link `collection.href` e cole no navegador para listar todos os SMS.

#### **8. Enviar SMS de Emergência (com HATEOAS)**
**endpoint** = `POST /api/sms/emergencia/2`
**json para teste** = 
```json
{
  "remetente": "Defesa Civil",
  "ddd": "11",
  "numeroTelefone": "+5511989302572",
  "mensagem": "ALERTA: Risco de enchente na região!"
}
```

**Resposta:** SMS criado com links HATEOAS (`self` e `collection`)

#### **9. Listar Todos SMS (com HATEOAS)**
**endpoint** = `GET /api/sms/getAll?page=0&size=10&direction=DESC`

**Parâmetros opcionais:**
- `page`: Número da página (padrão: 0)
- `size`: Tamanho da página (padrão: 10)
- `direction`: ASC ou DESC (padrão: DESC)
- `sucesso`: true/false (opcional, filtra por status)

**Resposta esperada:**
- Lista paginada de SMS
- **Cada SMS na lista tem seus próprios links HATEOAS** (`self` e `collection`)

**Exemplo de uso:**
```bash
# Listar todos
GET /api/sms/getAll?page=0&size=10&direction=DESC

# Listar apenas SMS com sucesso
GET /api/sms/getAll?page=0&size=10&direction=DESC&sucesso=true

# Listar apenas SMS com erro
GET /api/sms/getAll?page=0&size=10&direction=DESC&sucesso=false
```

#### **10. Buscar SMS por Número (com HATEOAS)**
**endpoint** = `GET /api/sms/buscarPorNumero?numeroTelefone=+5511989302572`

**Resposta:** Lista de SMS do número, **cada um com links HATEOAS**

#### **11. Buscar SMS por DDD (com HATEOAS)**
**endpoint** = `GET /api/sms/buscarPorDdd?ddd=11`

**Resposta:** Lista de SMS do DDD 11, **cada um com links HATEOAS**

#### **12. Buscar SMS por Evento (com HATEOAS)**
**endpoint** = `GET /api/sms/buscarPorEvento/2`

**Resposta:** Lista de SMS do evento, **cada um com links HATEOAS**

#### **13. Buscar SMS por Período (com HATEOAS)**
**endpoint** = `GET /api/sms/buscarPorPeriodo?dataInicio=2025-11-04T00:00:00&dataFim=2025-11-04T23:59:59`

**Resposta:** Lista de SMS do período, **cada um com links HATEOAS**

#### **14. Buscar Último SMS por Número (com HATEOAS)**
**endpoint** = `GET /api/sms/ultimoSms/%2B5511989302572`

**Nota:** Use `%2B` para codificar o `+` na URL, ou simplesmente `+5511989302572`

**Resposta esperada:**
```json
{
  "idSms": 1,
  "remetente": "SOS Localiza",
  "numeroTelefone": "+5511989302572",
  "mensagem": "Último SMS enviado",
  "_links": {
    "self": {
      "href": "/api/sms/ultimoSms/%2B5511989302572"
    },
    "collection": {
      "href": "/api/sms/getAll?page=0&size=10&direction=DESC"
    }
  }
}
```

**✨ Navegação:** Copie o link `self.href` para ver os detalhes novamente, ou `collection.href` para listar todos os SMS.

#### **15. Estatísticas de SMS**
**endpoint** = `GET /api/sms/estatisticas`
**json para teste** = 
```json
{}
```

#### **16. Marcar SMS como Sucesso**
**endpoint** = `PATCH /api/sms/marcarSucesso/1`
**json para teste** = 
```json
{}
```

#### **17. Marcar SMS como Erro**
**endpoint** = `PATCH /api/sms/marcarErro/1?erro=Erro de conexão`
**json para teste** = 
```json
{}
```

---

## 🔧 Integração Procedures Oracle - Sprint 2

Este documento descreve como usar os endpoints REST que chamam as procedures do Oracle Database.

### 📋 Endpoints Disponíveis

#### **Localização**

##### **1. INSERT - Criar Localização**
```http
POST /api/procedures/localizacao
Content-Type: application/json

{
  "nomeLocal": "Escola Municipal Sol Nascente",
  "ruaLocal": "Rua das Flores",
  "numeroLocal": 120,
  "cepLocal": "04567-000"
}
```

**Resposta de Sucesso (201):**
```json
{
  "id": 1,
  "mensagem": "Localização inserida com sucesso. ID: 1",
  "sucesso": true
}
```

##### **2. UPDATE - Atualizar Localização**
```http
PUT /api/procedures/localizacao/1
Content-Type: application/json

{
  "nomeLocal": "Escola Municipal Sol Nascente - Atualizada",
  "ruaLocal": "Rua das Flores",
  "numeroLocal": 150,
  "cepLocal": "04567-001"
}
```

##### **3. DELETE - Excluir Localização**
```http
DELETE /api/procedures/localizacao/1
```

#### **Usuário**

##### **1. INSERT - Criar Usuário**
```http
POST /api/procedures/usuario
Content-Type: application/json

{
  "nomeCompleto": "João Silva",
  "email": "joao.silva@email.com",
  "senha": "senha123",
  "cpf": "12345678901",
  "dataNascimento": "1990-01-15",
  "idLocal": 1,
  "ativo": 1
}
```

##### **2. UPDATE - Atualizar Usuário**
```http
PUT /api/procedures/usuario/1
Content-Type: application/json

{
  "nomeCompleto": "João Silva Santos",
  "email": "joao.santos@email.com",
  "senha": "novaSenha123",
  "cpf": "12345678901",
  "dataNascimento": "1990-01-15",
  "idLocal": 2,
  "ativo": 1
}
```

##### **3. DELETE - Excluir Usuário**
```http
DELETE /api/procedures/usuario/1
```

### 🧪 Testando com Postman/Insomnia

#### **Sequência de Testes (12 operações conforme requisito)**

**Localização:**
1. POST `/api/procedures/localizacao` - Criar Localização 1
2. POST `/api/procedures/localizacao` - Criar Localização 2
3. PUT `/api/procedures/localizacao/1` - Atualizar Localização 1
4. PUT `/api/procedures/localizacao/2` - Atualizar Localização 2
5. DELETE `/api/procedures/localizacao/1` - Excluir Localização 1
6. DELETE `/api/procedures/localizacao/2` - Excluir Localização 2

**Usuário:**
7. POST `/api/procedures/usuario` - Criar Usuário 1
8. POST `/api/procedures/usuario` - Criar Usuário 2
9. PUT `/api/procedures/usuario/1` - Atualizar Usuário 1
10. PUT `/api/procedures/usuario/2` - Atualizar Usuário 2
11. DELETE `/api/procedures/usuario/1` - Excluir Usuário 1
12. DELETE `/api/procedures/usuario/2` - Excluir Usuário 2

---

## 📊 Resultados Esperados

### **Status HTTP Esperados**
- **200**: Sucesso (GET, PUT)
- **201**: Criado (POST)
- **204**: Sem conteúdo (DELETE, PATCH)
- **400**: Erro de validação
- **404**: Não encontrado
- **500**: Erro interno

### **Presença de Links HATEOAS**
Todos os endpoints de SMS retornam objeto `_links` com:
- ✅ `self`: Link para o próprio recurso
- ✅ `collection`: Link para listar todos os recursos

**Exemplo:**
```json
{
  "idSms": 1,
  "mensagem": "Teste",
  "_links": {
    "self": { "href": "/api/sms/ultimoSms/%2B5511989302572" },
    "collection": { "href": "/api/sms/getAll?page=0&size=10&direction=DESC" }
  }
}
```

### **Estrutura de Resposta de Erro**
```json
{
  "errorCode": "VALIDATION_ERROR",
  "message": "Nome do evento é obrigatório",
  "timestamp": "2024-01-01T12:00:00",
  "path": "/api/eventos/add",
  "details": ["Nome do evento é obrigatório"]
}
```

### **Estrutura de Resposta de Sucesso com HATEOAS**
```json
{
  "idSms": 1,
  "remetente": "Sistema SOS Localiza",
  "numeroTelefone": "+5511989302572",
  "ddd": "11",
  "mensagem": "Teste de SMS!",
  "dataEnvio": "2025-11-04T21:00:00",
  "enviadoComSucesso": true,
  "erro": null,
  "idEvento": null,
  "_links": {
    "self": {
      "href": "/api/sms/ultimoSms/%2B5511989302572"
    },
    "collection": {
      "href": "/api/sms/getAll?page=0&size=10&direction=DESC"
    }
  }
}
```

## 🔍 Validações Específicas

### **Eventos**
- ✅ Nome obrigatório (máximo 100 caracteres)
- ✅ Descrição opcional (máximo 500 caracteres)
- ✅ Causas opcional (máximo 300 caracteres)
- ✅ Alertas opcional (máximo 300 caracteres)
- ✅ Ações opcionais (máximo 500 caracteres cada)

### **SMS**
- ✅ Remetente obrigatório (máximo 100 caracteres)
- ✅ DDD obrigatório (exatamente 2 dígitos)
- ✅ Número obrigatório (8-9 dígitos)
- ✅ Mensagem obrigatória (máximo 1000 caracteres)
- ✅ Validação de telefone brasileiro

### **Paginação**
- ✅ `page`: Número da página (padrão: 0)
- ✅ `size`: Tamanho da página (padrão: 10)
- ✅ `direction`: ASC/DESC (padrão: ASC para eventos, DESC para SMS)
- ✅ Cada item na lista paginada tem seus próprios links HATEOAS

## 🚨 Cenários de Erro

### **Evento Não Encontrado**
- **Endpoint**: `GET /eventos/getById/{id-inexistente}`
- **Status**: 404
- **Erro**: `EVENTO_NOT_FOUND`

### **Validação de Telefone**
- **Endpoint**: `POST /sms` com DDD inválido
- **Status**: 400
- **Erro**: `VALIDATION_ERROR`

### **Erro Twilio**
- **Endpoint**: `POST /sms` com credenciais inválidas
- **Status**: 503
- **Erro**: `TWILIO_ERROR`

## 📝 Relatório de Testes

### **Checklist de Validação**
- [ ] Todos os endpoints respondem corretamente
- [ ] Validações funcionam conforme esperado
- [ ] Tratamento de erros retorna códigos corretos
- [ ] Paginação funciona em listagens
- [ ] Relacionamentos entre entidades funcionam
- [ ] Logs são gerados adequadamente
- [ ] Performance está dentro do esperado
- [ ] **HATEOAS**: Todos os endpoints de SMS retornam `_links`
- [ ] **HATEOAS**: Links `self` e `collection` são funcionais
- [ ] **HATEOAS**: Navegação completa funcionando (criar → listar → buscar)

### **Métricas de Sucesso**
- **Taxa de Sucesso**: > 95% dos testes passando
- **Tempo de Resposta**: < 2 segundos por requisição
- **Cobertura**: Todos os endpoints testados
- **Validação**: Todos os cenários de erro cobertos

## 🔧 Troubleshooting

### **Erro de Conexão**
- Verifique se a aplicação está rodando na porta 8080
- Confirme se o contexto `/api` está configurado

### **Erro de Validação**
- Verifique se os dados estão no formato correto
- Confirme se os campos obrigatórios estão preenchidos

### **Erro Twilio**
- Verifique se as variáveis de ambiente estão configuradas
- Confirme se as credenciais estão corretas
- Teste com números de teste do Twilio

### **Erro de Banco**
- Verifique se o Oracle FIAP está acessível
- Confirme se as credenciais do banco estão corretas
- Para desenvolvimento, use o perfil H2: `--spring.profiles.active=dev`

### **Erro com Links HATEOAS**
- Verifique se o objeto `_links` está presente na resposta
- Teste copiando e colando o `href` diretamente no navegador
- Para números com `+`, use encoding `%2B` ou `+` diretamente
- Certifique-se de que a aplicação está rodando na porta 8081

---

## 🔗 Teste Completo de Navegação HATEOAS

### **Fluxo: Criar → Listar → Buscar**

1. **POST** `/api/sms` → Recebe SMS criado com links
2. **Copiar** link `collection.href` → Cole no navegador/Postman
3. **GET** `/api/sms/getAll?...` → Ver lista paginada (cada item com links)
4. **Copiar** link `self.href` de um SMS → Cole no navegador/Postman
5. **GET** `/api/sms/ultimoSms/{numero}` → Ver detalhes do SMS

**Resultado:** Navegação completa pela API seguindo apenas os links retornados!
