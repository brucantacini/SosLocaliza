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
- **SMS**: Simulado (sem Twilio)

## 📊 Endpoints e JSONs para Teste

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

#### **7. Enviar SMS Simulado**
**endpoint** = `POST /api/sms`
**json para teste** = 
```json
{
  "remetente": "SOS Localiza",
  "ddd": "11",
  "numeroTelefone": "999999999",
  "mensagem": "Teste de SMS simulado - sistema funcionando!"
}
```

#### **8. Enviar SMS de Emergência**
**endpoint** = `POST /api/sms/emergencia/2`
**json para teste** = 
```json
{
  "remetente": "Defesa Civil",
  "ddd": "11",
  "numeroTelefone": "888888888",
  "mensagem": "ALERTA: Risco de enchente na região!"
}
```

#### **9. Listar Todos SMS**
**endpoint** = `GET /api/sms/getAll`
**json para teste** = 
```json
{
  "page": 0,
  "size": 10,
  "direction": "DESC",
  "sucesso": true
}
```

#### **10. Buscar SMS por Número**
**endpoint** = `GET /api/sms/buscarPorNumero?numeroTelefone=+5511999999999`
**json para teste** = 
```json
{}
```

#### **11. Buscar SMS por DDD**
**endpoint** = `GET /api/sms/buscarPorDdd?ddd=11`
**json para teste** = 
```json
{}
```

#### **12. Buscar SMS por Evento**
**endpoint** = `GET /api/sms/buscarPorEvento/2`
**json para teste** = 
```json
{}
```

#### **13. Buscar SMS por Período**
**endpoint** = `GET /api/sms/buscarPorPeriodo?dataInicio=2025-10-01T00:00:00&dataFim=2025-10-31T23:59:59`
**json para teste** = 
```json
{}
```

#### **14. Buscar Último SMS por Número**
**endpoint** = `GET /api/sms/ultimoSms/+5511999999999`
**json para teste** = 
```json
{}
```

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

## 📊 Resultados Esperados

### **Status HTTP Esperados**
- **200**: Sucesso (GET, PUT)
- **201**: Criado (POST)
- **204**: Sem conteúdo (DELETE, PATCH)
- **400**: Erro de validação
- **404**: Não encontrado
- **500**: Erro interno

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

### **Estrutura de Resposta de Sucesso**
```json
{
  "idEvento": "uuid-gerado",
  "nomeEvento": "Enchente na Região Sul",
  "descricaoEvento": "Alagamento severo...",
  "dataCriacao": "2024-01-01T12:00:00",
  "ativo": true
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
