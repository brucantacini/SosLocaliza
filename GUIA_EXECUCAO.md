# 🚀 Guia de Execução - SOS Localiza

## 📋 Pré-requisitos
- Java 21 instalado
- Maven instalado (ou usar o Maven Wrapper incluído)
- Navegador web
- Postman ou Insomnia (opcional)

## ⚡ Execução Rápida

### 1. Abrir Terminal/PowerShell
```bash
# Navegar para o diretório do projeto
cd "C:\ProjetosBruno\Faculdade\Java\OracleSOSLocaliza\SosLocaliza"
```

### 2. Configurar Variáveis do Twilio (Opcional)
```bash
# Windows PowerShell
$env:TWILIO_ACCOUNT_SID="ACb84a1c0afbb6628678209002a9db4d6c"
$env:TWILIO_AUTH_TOKEN="7a420445aa2fc29161ac7cc22e2b8353"
$env:TWILIO_TRIAL_NUMBER="+5511989302572"

# Linux/Mac
export TWILIO_ACCOUNT_SID="ACb84a1c0afbb6628678209002a9db4d6c"
export TWILIO_AUTH_TOKEN="7a420445aa2fc29161ac7cc22e2b8353"
export TWILIO_TRIAL_NUMBER="+5511989302572"
```

### 3. Executar a Aplicação
```bash
# Usando Maven Wrapper (recomendado)
.\mvnw.cmd spring-boot:run

# Ou usando Maven instalado
mvn spring-boot:run
```

### 4. Aguardar Inicialização
- Aguarde a mensagem: `Started SosLocalizaApplication`
- A aplicação estará disponível em: `http://localhost:8080`

## 🌐 Testando a Aplicação

### Opção 1: Swagger UI (Mais Fácil)
1. Abra o navegador
2. Acesse: `http://localhost:8080/swagger-ui.html`
3. Teste os endpoints diretamente na interface

### Opção 2: Postman/Insomnia
1. Importe o arquivo: `postman-collection.json`
2. Configure a variável: `base_url = http://localhost:8080`
3. Execute os testes da coleção

### Opção 3: PowerShell/curl
```bash
# Listar eventos
Invoke-WebRequest -Uri "http://localhost:8080/eventos/getAll" -Method GET

# Criar evento
$body = @{
    nomeEvento = "Inundação Teste"
    descricaoEvento = "Teste via PowerShell"
    causas = "Chuva intensa"
    alertas = "Área de risco"
    acoesAntes = "Evacuar área"
    acoesDurante = "Buscar abrigo"
    acoesDepois = "Verificar danos"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/eventos/add" -Method POST -Body $body -ContentType "application/json"
```

## 📊 Endpoints Principais

### Eventos
- `GET /eventos/getAll` - Listar eventos
- `POST /eventos/add` - Criar evento
- `GET /eventos/getById/{id}` - Buscar por ID
- `PUT /eventos/update/{id}` - Atualizar evento
- `DELETE /eventos/delete/{id}` - Deletar evento

### SMS
- `POST /sms` - Enviar SMS
- `GET /sms/getAll` - Listar SMS
- `GET /sms/buscarPorNumero?numeroTelefone=X` - Buscar por número

## 🧪 Teste Rápido no Swagger

### 1. Criar um Evento
1. Acesse: `http://localhost:8080/swagger-ui.html`
2. Clique em "Eventos" → "POST /eventos/add"
3. Clique em "Try it out"
4. Cole o JSON:
```json
{
  "nomeEvento": "Inundação Teste",
  "descricaoEvento": "Teste via Swagger",
  "causas": "Chuva intensa",
  "alertas": "Área de risco",
  "acoesAntes": "Evacuar área",
  "acoesDurante": "Buscar abrigo",
  "acoesDepois": "Verificar danos"
}
```
5. Clique em "Execute"
6. Anote o `idEvento` retornado

### 2. Listar Eventos
1. Clique em "GET /eventos/getAll"
2. Clique em "Try it out" → "Execute"
3. Verifique se o evento criado aparece na lista

### 3. Testar SMS (Opcional)
1. Clique em "Sms" → "POST /sms"
2. Clique em "Try it out"
3. Cole o JSON:
```json
{
  "remetente": "SOS Localiza",
  "ddd": "11",
  "numero": "999999999",
  "mensagem": "Teste via Swagger",
  "idEvento": "ID_DO_EVENTO_CRIADO"
}
```
4. Clique em "Execute"
5. Resultado esperado: erro 503 (Twilio indisponível - normal)

## 🔧 Solução de Problemas

### Erro: "Porta 8080 já está em uso"
```bash
# Encontrar processo usando a porta
netstat -ano | findstr :8080

# Matar o processo (substitua PID)
taskkill /PID <PID> /F
```

### Erro: "Java não encontrado"
- Instale Java 21
- Configure a variável JAVA_HOME

### Erro: "Maven não encontrado"
- Use o Maven Wrapper: `.\mvnw.cmd` (Windows) ou `./mvnw` (Linux/Mac)

### Aplicação não inicia
- Verifique se está no diretório correto: `SosLocaliza/`
- Verifique se o Java 21 está instalado
- Execute: `.\mvnw.cmd clean compile`

## 📱 URLs Importantes

- **Aplicação**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

## ✅ Checklist de Teste

- [ ] Aplicação inicia sem erros
- [ ] Swagger UI carrega corretamente
- [ ] Endpoint GET /eventos/getAll retorna 200
- [ ] Endpoint POST /eventos/add cria evento com sucesso
- [ ] Endpoint GET /eventos/getById/{id} retorna evento específico
- [ ] Validações funcionam (campos obrigatórios)
- [ ] Tratamento de erros retorna códigos corretos

## 🎯 Resultado Esperado

Se tudo estiver funcionando:
- ✅ Aplicação rodando na porta 8080
- ✅ Swagger UI acessível
- ✅ Endpoints respondendo corretamente
- ✅ Banco H2 em memória funcionando
- ⚠️ SMS com erro 503 (normal - Twilio indisponível)

---

**Pronto!** Sua aplicação está funcionando e pronta para demonstração.
