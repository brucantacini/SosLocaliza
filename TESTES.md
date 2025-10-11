# 🧪 Testes da API SOS Localiza

## 📋 Coleção de Testes Postman/Insomnia

### **Arquivo de Coleção**
- **Postman**: `postman-collection.json`
- **Insomnia**: Importe o mesmo arquivo JSON

## 🚀 Como Executar os Testes

### **1. Configuração Inicial**
1. **Importe a coleção** no Postman ou Insomnia
2. **Configure as variáveis**:
   - `base_url`: `http://localhost:8080/api`
   - `evento_id`: Substitua pelo ID do evento criado

### **2. Configuração do Ambiente**
```bash
# Configure as variáveis de ambiente do Twilio
export TWILIO_ACCOUNT_SID="seu_account_sid_aqui"
export TWILIO_AUTH_TOKEN="seu_auth_token_aqui"
export TWILIO_TRIAL_NUMBER="seu_numero_trial_aqui"

# Execute a aplicação
mvn spring-boot:run
```

### **3. Sequência de Testes Recomendada**

#### **Fase 1: Testes de Eventos**
1. **Criar Evento** → Anote o `idEvento` retornado
2. **Listar Todos Eventos** → Verifique paginação
3. **Buscar Evento por ID** → Use o ID anotado
4. **Atualizar Evento** → Use o ID anotado
5. **Buscar Eventos por Nome** → Teste filtros
6. **Obter Estatísticas** → Verifique contadores

#### **Fase 2: Testes de SMS**
1. **Enviar SMS** → Teste envio básico
2. **Enviar SMS de Emergência** → Use o ID do evento
3. **Listar Todos SMS** → Verifique histórico
4. **Buscar SMS por Número** → Teste filtros
5. **Buscar SMS por DDD** → Teste filtros por região
6. **Buscar SMS por Evento** → Relacione com evento
7. **Obter Estatísticas SMS** → Verifique contadores

#### **Fase 3: Testes de Validação**
1. **Criar Evento - Validação** → Teste campos obrigatórios
2. **Enviar SMS - Validação** → Teste formato de telefone
3. **Buscar Evento - ID Inexistente** → Teste tratamento de erro

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
